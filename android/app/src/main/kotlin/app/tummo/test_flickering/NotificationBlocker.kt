package app.tummo.test_flickering

import android.content.*
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationBlocker : NotificationListenerService() {

    override fun onNotificationPosted(notification: StatusBarNotification) {
        Log.d("NotificationBlocker", notification.packageName)
        Log.d("NotificationBlocker", notification.notification.channelId)

        if (notification.packageName.equals(packageName) &&
                        notification.notification.channelId.equals("1")
        ) {
            Log.d("NotificationBlocker", "Cancelling Notification")
            snoozeNotification(notification.key, 1000 * 60)
        }
    }
}
