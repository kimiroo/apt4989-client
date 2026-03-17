package cc.darak.aptanywhere.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.material3.contentColorFor
import androidx.core.app.NotificationCompat
import cc.darak.aptanywhere.R
import cc.darak.aptanywhere.data.model.ChannelID
import cc.darak.aptanywhere.data.model.PropertyInfo
import cc.darak.aptanywhere.ui.main.MainActivity
import cc.darak.aptanywhere.util.PropertyUtils.isOwner

class NotificationHelper(private val context: Context) {

    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    /**
     * Create and return a notification for the Foreground Service.
     */
    fun createServiceNotification(): Notification {
        createChannel(
            id = ChannelID.OVERLAY_SERVICE,
            name = context.getString(R.string.notification_overlay_service_name),
            description = context.getString(R.string.notification_overlay_service_description),
            importance = NotificationManager.IMPORTANCE_LOW
        )

        return NotificationCompat.Builder(context, ChannelID.OVERLAY_SERVICE)
            .setContentTitle(context.getString(R.string.notification_overlay_service_title))
            .setContentText(context.getString(R.string.notification_overlay_service_message))
            .setSmallIcon(R.drawable.ic_apartment)
            .setContentIntent(createPendingIntent())
            .setOngoing(true) // Cannot be dismissed by the user
            .build()
    }

    /**
     * Create and return a notification for information or error alerts.
     */
    fun createErrorNotification(title: String, message: String, detailedMessage: String): Notification {
        createChannel(
            id = ChannelID.ERROR,
            name = context.getString(R.string.notification_error_name),
            description = context.getString(R.string.notification_error_description),
            importance = NotificationManager.IMPORTANCE_HIGH
        )

        return NotificationCompat.Builder(context, ChannelID.ERROR)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle()
                .setBigContentTitle(title) // Title shown when expanded
                .bigText(detailedMessage) // Content shown when expanded
            )
            .setSmallIcon(R.drawable.ic_apartment)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // For backward compatibility
            .setAutoCancel(false) // Automatically dismissed when clicked
            .build()
    }

    /**
     * Create and return a notification for information or error alerts.
     */
    fun createLookupResultNotification(
        number: String,
        propertyList: List<PropertyInfo>? = null
    ): Notification {
        // 1. Ensure the notification channel exists
        createChannel(
            id = ChannelID.LOOKUP_RESULT,
            name = context.getString(R.string.notification_lookup_result_name),
            description = context.getString(R.string.notification_lookup_result_description),
            importance = NotificationManager.IMPORTANCE_HIGH
        )

        val formattedNumber = formatPhoneNumber(number)

        // 2. Prepare text resources using string injection
        val title = context.getString(
            R.string.notification_lookup_result_title,
            formattedNumber
        )
        val message = context.getString(
            R.string.notification_lookup_result_message,
            formattedNumber,
            propertyList?.size ?: 0
        )

        // 3. Initialize the builder
        val builder = NotificationCompat.Builder(context, ChannelID.LOOKUP_RESULT)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_apartment)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(createDetailPendingIntent(propertyList ?: emptyList()))

        // 4. Conditionally apply InboxStyle if propertyList is not empty
        if (!propertyList.isNullOrEmpty()) {
            val inboxStyle = NotificationCompat.InboxStyle()
                //.setBigContentTitle("title") //context.getString(R.string.notification_search_result_detail_title)
                //.setSummaryText("summary") //context.getString(R.string.notification_search_result_summary)

            // Add up to 7 lines for the preview
            propertyList.take(7).forEach { info ->
                val personType = if (isOwner(info, number)) {
                    context.getString(R.string.label_owner)
                } else {
                    context.getString(R.string.label_tenant)
                }
                val line = context.getString(
                    R.string.notification_lookup_result_line,
                    personType,
                    info.complex,
                    info.bld,
                    info.unit
                )
                inboxStyle.addLine(line)
            }
            builder.setStyle(inboxStyle)
        }

        return builder.build()
    }

    /**
     * Helper method to create notification channels for Android 8.0+.
     */
    private fun createChannel(
        id: String,
        name: String,
        description:
        String,
        importance: Int,
        showBadge: Boolean = true
    ) {
        // Create channel only if it doesn't exist or to update info
        val channel = NotificationChannel(id, name, importance).apply {
            this.description = description // Set the channel description here
            // Optional: You can also set light color or vibration patterns here
            setShowBadge(showBadge)
        }
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * Create a PendingIntent to open the app's main activity.
     */
    private fun createPendingIntent(): PendingIntent {
        // Replace 'MainActivity::class.java' with your target activity
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Use FLAG_IMMUTABLE for security on Android 12+
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun createDetailPendingIntent(propertyList: List<PropertyInfo>): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply { //TODO
            // Wrap the list in an ArrayList if it isn't one already
            val arrayList = ArrayList(propertyList)
            putParcelableArrayListExtra("PROPERTY_LIST", arrayList)

            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        return PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(), // Unique request code
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun generateUniqueID(): Int {
        val minId = 1000
        val uniqueId = minId + (System.currentTimeMillis() % (Int.MAX_VALUE - minId)).toInt()
        return uniqueId
    }

    // For convenience: expose the manager if needed
    fun getManager(): NotificationManager = notificationManager
}