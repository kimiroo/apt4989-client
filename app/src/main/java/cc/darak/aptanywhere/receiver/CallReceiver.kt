package cc.darak.aptanywhere.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import cc.darak.aptanywhere.service.PhoneMonitorService
import kotlin.jvm.java

class CallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            if (state == TelephonyManager.EXTRA_STATE_RINGING && number != null) {
                // Launch service and pass phone number
                val serviceIntent = Intent(context, PhoneMonitorService::class.java).apply {
                    putExtra("phone_number", number)
                }
                context.startService(serviceIntent)
            }
        }
    }
}