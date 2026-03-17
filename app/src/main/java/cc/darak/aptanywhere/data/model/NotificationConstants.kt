package cc.darak.aptanywhere.data.model

object ChannelID {
    const val OVERLAY_SERVICE = "channel_id_overlay_service"
    const val ERROR = "channel_id_overlay_error"
    const val LOOKUP_RESULT = "channel_id_lookup_result"
}

object NotificationID {
    // 1-99: Foreground Services
    const val OVERLAY_SERVICE = 1

    // 100+: General Alerts
}