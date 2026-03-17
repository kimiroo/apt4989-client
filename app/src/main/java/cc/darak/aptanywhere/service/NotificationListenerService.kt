package cc.darak.aptanywhere.service

import android.app.Notification
import android.app.Person
import android.content.Intent
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {

        val packageName = sbn.packageName

        if (packageName == "com.samsung.android.messaging" ||
            packageName == "com.google.android.apps.messaging") {

            val extras = sbn.notification.extras
            val messages = extras.getParcelableArray(Notification.EXTRA_MESSAGES)

            var finalNumber: String? = null

            // [Step 0] 메시지 배열 확인
            if (!messages.isNullOrEmpty()) {
                val lastMessage = messages.last() as Bundle
                val person = lastMessage.getParcelable<Person>("sender_person")

                // [Step 1] Person 객체에서 tel: URI 추출 시도
                val uri = person?.uri
                if (uri != null && uri.startsWith("tel:")) {
                    finalNumber = uri.substringAfter("tel:").replace(Regex("[^0-9]"), "")
                }

                // [Step 2] Person 객체의 이름 필드에서 번호 추출 시도 (연락처 미등록 대응)
                if (finalNumber == null) {
                    val name = person?.name?.toString() ?: ""
                    finalNumber = extractPhoneNumber(name)
                }
            }

            // [Step 3] 그래도 안되면 EXTRA_TITLE (알림 제목)에서 다시 시도
            if (finalNumber == null) {
                val title = extras.getString(Notification.EXTRA_TITLE) ?: ""
                finalNumber = extractPhoneNumber(title)
            }

            // 최종 결과 처리
            if (finalNumber != null) {
                Log.d("NotiListener", "번호 획득 성공: $finalNumber")
                startOverlayService(finalNumber)
            } else {
                Log.d("NotiListener", "번호 획득 실패. 이미 저장된 연락처일 수 있음.")
                // 필요 시 "연락처 저장으로 인해 조회가 제한됨" 알림 띄우기
            }
        }
    }

    private fun extractPhoneNumber(text: String): String? {
        val regex = Regex("""(01[016789])[-.\s]?(\d{3,4})[-.\s]?(\d{4})""")
        return regex.find(text)?.value?.replace(Regex("""[-.\s]"""), "")
    }

    private fun showGuidanceNotification(name: String) {
        // "이미 저장된 연락처($name)라 번호 조회가 안 됩니다"라는 간단한 시스템 알림 노출
    }

    private fun startOverlayService(number: String) {
        // Launch service and pass phone number
        val serviceIntent = Intent(this, PhoneMonitorService::class.java).apply {
            putExtra("phone_number", number)
        }
        this.startService(serviceIntent)
    }
}