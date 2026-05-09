package cc.darak.aptanywhere.service

import android.app.Notification
import android.app.Person
import android.content.Intent
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationListener : NotificationListenerService() {

    private val TAG = NotificationListener::class.java.simpleName

    override fun onNotificationPosted(sbn: StatusBarNotification) {

        val packageName = sbn.packageName

        if (packageName == "com.samsung.android.messaging" ||
            packageName == "com.google.android.apps.messaging") {

            val extras = sbn.notification.extras
            val messages =
                extras.getParcelableArray(Notification.EXTRA_MESSAGES, Bundle::class.java)

            var finalNumber: String? = null

            // Step 0. Check message array
            if (!messages.isNullOrEmpty()) {
                val lastMessage = messages.last() as Bundle
                val person = lastMessage.getParcelable("sender_person", Person::class.java)

                // Step 1. Try parsing URI from Person object
                val uri = person?.uri
                if (uri != null && uri.startsWith("tel:")) {
                    finalNumber = uri.substringAfter("tel:").replace(Regex("[^0-9]"), "")
                }

                // Step 2. Try parsing number from Person object's name field (In case of unregistered contact)
                if (finalNumber == null) {
                    val name = person?.name?.toString() ?: ""
                    finalNumber = extractPhoneNumber(name)
                }
            }

            // Step 3. Try acquiring the number from the notification title (EXTRA_TITLE) if above steps fails.
            if (finalNumber == null) {
                val title = extras.getString(Notification.EXTRA_TITLE) ?: ""
                finalNumber = extractPhoneNumber(title)
            }

            if (finalNumber != null) {
                Log.d(TAG, "Number acquired: $finalNumber")
                startOverlayService(finalNumber)
            } else {
                Log.d(TAG, "Failed to extract incoming number; possibly filtered by system or existing contact.")
            }
        }
    }

    private fun extractPhoneNumber(text: String): String? {
        val regex = Regex("""(01[016789])[-.\s]?(\d{3,4})[-.\s]?(\d{4})""")
        return regex.find(text)?.value?.replace(Regex("""[-.\s]"""), "")
    }

    private fun startOverlayService(number: String) {
        // Launch service and pass phone number
        val serviceIntent = Intent(this, OverlayService::class.java).apply {
            putExtra("phone_number", number)
        }
        this.startService(serviceIntent)
    }
}