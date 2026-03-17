package cc.darak.aptanywhere.viewmodel

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PermissionViewModel : ViewModel() {
    private val _generalGranted = MutableStateFlow(false)
    private val _notificationAccessGranted = MutableStateFlow(false)
    private val _overlayGranted = MutableStateFlow(false)
    private val _batteryGranted = MutableStateFlow(false)

    val generalGranted = _generalGranted.asStateFlow()
    val notificationAccessGranted = _notificationAccessGranted.asStateFlow()
    val overlayGranted = _overlayGranted.asStateFlow()
    val batteryGranted = _batteryGranted.asStateFlow()

    val permissionsList = arrayOf(
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CALL_LOG
    )

    fun checkAllPermissions(context: Context) {
        checkGeneralPermissions(context)
        checkNotificationAccessPermission(context)
        checkOverlayPermissions(context)
        checkBatteryPermissions(context)
    }

    fun checkGeneralPermissions(context: Context) {
        _generalGranted.value = hasGeneralPermissions(context)
    }

    fun checkNotificationAccessPermission(context: Context) {
        _notificationAccessGranted.value = isNotificationAccessGranted(context)
    }

    fun checkOverlayPermissions(context: Context) {
        _overlayGranted.value = canDrawOverlays(context)
    }

    fun checkBatteryPermissions(context: Context) {
        _batteryGranted.value = isIgnoringBatteryOptimizations(context)
    }

    fun hasGeneralPermissions(context: Context) = permissionsList.all { permission ->
        val isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        isGranted
    }

    fun isNotificationAccessGranted(context: Context) =
        NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.packageName)

    private fun canDrawOverlays(context: Context) = Settings.canDrawOverlays(context)

    private fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isIgnoringBatteryOptimizations(context.packageName)
    }

    // Requesting general permission is done in Compose

    fun requestNotificationAccessPermission(context: Context) {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        context.startActivity(intent)
    }

    fun requestOverlayPermission(context: Context) {
        // Trigger intent only when user clicks the button
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            "package:${context.packageName}".toUri()
        )
        context.startActivity(intent)
    }

    fun requestIgnoreBatteryOptimizations(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = "package:${context.packageName}".toUri()
            }
            ContextCompat.startActivity(context, intent, null)
        } catch (e: Exception) {
            // 기기마다 인텐트가 다를 수 있으므로 예외 처리
            val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            ContextCompat.startActivity(context, intent, null)
        }
    }
}