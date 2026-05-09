package cc.darak.aptanywhere.receiver

import android.app.ActivityOptions
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import cc.darak.aptanywhere.service.OverlayService
import kotlin.jvm.java

class CallReceiver : BroadcastReceiver() {

    private val TAG = CallReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            @Suppress("DEPRECATION")
            val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            if (state == TelephonyManager.EXTRA_STATE_RINGING && !number.isNullOrEmpty()) {
                // Launch service and pass phone number
                val serviceIntent = Intent(context, OverlayService::class.java).apply {
                    putExtra("phone_number", number)
                }

                val options = ActivityOptions.makeBasic()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // API 34 (Android 14)
                    // For API 36+, use ALLOW_ALWAYS instead of ALLOWED
                    val mode = if (Build.VERSION.SDK_INT >= 36) {
                        ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOW_ALWAYS
                    } else {
                        @Suppress("DEPRECATION")
                        ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED
                    }

                    options.setPendingIntentBackgroundActivityStartMode(mode)
                }

                // wrap in a PendingIntent to apply options.
                val pendingIntent = PendingIntent.getForegroundService(
                    context,
                    0,
                    serviceIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

                try {
                    pendingIntent.send(context, 0, null, null, null, null, options.toBundle())
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to start service via PendingIntent: ${e.message}")
                }
            }
        }
    }
}