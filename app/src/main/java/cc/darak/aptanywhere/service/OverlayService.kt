package cc.darak.aptanywhere.service

import android.content.Intent
import android.graphics.PixelFormat
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import cc.darak.aptanywhere.R
import cc.darak.aptanywhere.data.model.NotificationID
import cc.darak.aptanywhere.ui.components.overlay.OverlayCard
import cc.darak.aptanywhere.data.model.UiState
import cc.darak.aptanywhere.data.repository.PropertyRepository.fetchInfoByNumber
import cc.darak.aptanywhere.util.NotificationHelper
import cc.darak.aptanywhere.util.PreferencesHelper.getMaxOverlayMaxHeight
import cc.darak.aptanywhere.util.PreferencesHelper.getOverlayYOffset
import cc.darak.aptanywhere.util.PreferencesHelper.getShowOverlay
import kotlinx.coroutines.launch

class PhoneMonitorService : LifecycleService(), ViewModelStoreOwner, SavedStateRegistryOwner {

    private lateinit var windowManager: WindowManager
    private lateinit var notificationHelper: NotificationHelper
    private var overlayView: ComposeView? = null

    // Lifecycle management for Compose
    private val mViewModelStore = ViewModelStore()
    private val mSavedStateRegistryController = SavedStateRegistryController.create(this)

    override val viewModelStore: ViewModelStore = mViewModelStore
    override val savedStateRegistry: SavedStateRegistry = mSavedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        mSavedStateRegistryController.performRestore(null)
        notificationHelper = NotificationHelper(this)

        startForeground(
            NotificationID.OVERLAY_SERVICE,
            notificationHelper.createServiceNotification()
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        // Create service notification
        startForeground(
            NotificationID.OVERLAY_SERVICE,
            notificationHelper.createServiceNotification()
        )

        val number = intent?.getStringExtra("phone_number")
        if (number != null) {
            // Show overlay only if phone number is given
            lookupNumber(number)
        }

        return START_STICKY
    }

    private fun lookupNumber(number: String) {
        updateOverlayUI(UiState.Loading)

        lifecycleScope.launch {
            try {
                // 1. Sanitize the phone number
                val cleanNumber = number.replace(Regex("[^0-9]"), "")

                // 2. Direct call to the repository
                // This runs on Dispatchers.IO internally
                val data = fetchInfoByNumber(cleanNumber)

                // 3. Update UI based on the result
                if (data.isNotEmpty()) {
                    // Found properties: data is List<PropertyInfo>
                    updateOverlayUI(UiState.Success(number,data))
                } else {
                    // No properties linked to this number
                    updateOverlayUI(UiState.NotFound)
                }

                // 4. Show notification
                showResultNotification(UiState.Success(number,data))
            } catch (e: Exception) {
                // Handle network errors, timeouts, or unauthorized (401)
                updateOverlayUI(UiState.Error("${e.message}"))

                val message = getString(
                    R.string.notification_error_message_lookup,
                    e.message
                )
                val notification = notificationHelper.createErrorNotification(
                    getString(R.string.notification_error_title),
                    message,
                    message
                )
                notificationHelper.getManager().notify(
                    notificationHelper.generateUniqueID(),
                    notification
                )
            }
        }
    }

    private fun updateOverlayUI(state: UiState) {
        if (!getShowOverlay(this)) {
            return
        }

        try {
            val maxHeightDp = getMaxOverlayMaxHeight(this)

            // If view already exists
            overlayView?.let { view ->
                view.setContent {
                    OverlayCard(
                        maxHeightDp = maxHeightDp,
                        state = state,
                        onDismiss = { stopSelf() }
                    )
                }
                return // Update and exit
            }

            // If view doesn't exist -> Create one
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

            val layoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED, // Allow on lock screen
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP
                y = getOverlayYOffset(this@PhoneMonitorService) // Padding from top
            }

            val view = ComposeView(this).apply {
                setViewTreeLifecycleOwner(this@PhoneMonitorService)
                setViewTreeViewModelStoreOwner(this@PhoneMonitorService)
                setViewTreeSavedStateRegistryOwner(this@PhoneMonitorService)

                setContent {
                    OverlayCard(
                        maxHeightDp = maxHeightDp,
                        state = state,
                        onDismiss = { stopSelf() }
                    )
                }
            }

            // Add view to the windowManager
            windowManager.addView(view, layoutParams)
            overlayView = view
        } catch (e: Exception) {
            Log.e("PhoneMonitorService", "Error updating overlay UI: ${e.message}")

            val message = getString(
                R.string.notification_error_message_overlay,
                e.message
            )
            val notification = notificationHelper.createErrorNotification(
                getString(R.string.notification_error_title),
                message,
                message
                )
            notificationHelper.getManager().notify(
                notificationHelper.generateUniqueID(),
                notification
            )
        }
    }

    fun showResultNotification(state: UiState) {
        val notification = notificationHelper.createLookupResultNotification(
            number = (state as UiState.Success).number,
            propertyList = state.infoList
        )
        // 2. Show it using the manager
        // Use a different ID (e.g., NotificationID.ERROR_ALERT) to not overwrite the service notification
        notificationHelper.getManager().notify(
            notificationHelper.generateUniqueID(),
            notification
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
        mViewModelStore.clear()
    }
}