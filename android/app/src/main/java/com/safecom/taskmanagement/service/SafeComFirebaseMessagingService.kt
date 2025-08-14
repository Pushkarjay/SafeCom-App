package com.safecom.taskmanagement.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.safecom.taskmanagement.R
import com.safecom.taskmanagement.ui.splash.SplashActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SafeComFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID = "safecom_notifications"
        private const val CHANNEL_NAME = "SafeCom Notifications"
        private const val CHANNEL_DESCRIPTION = "Notifications for SafeCom Task Management"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Check if message contains a data payload
        remoteMessage.data.isNotEmpty().let {
            handleDataMessage(remoteMessage.data)
        }

        // Check if message contains a notification payload
        remoteMessage.notification?.let {
            handleNotificationMessage(it)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        
        // Send the new token to your server
        sendRegistrationTokenToServer(token)
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val type = data["type"] ?: return
        val title = data["title"] ?: "SafeCom"
        val body = data["body"] ?: ""
        val taskId = data["taskId"]
        val conversationId = data["conversationId"]

        when (type) {
            "task_assigned" -> {
                showNotification(
                    title = title,
                    body = body,
                    type = NotificationType.TASK_ASSIGNED,
                    taskId = taskId
                )
            }
            "task_due_soon" -> {
                showNotification(
                    title = title,
                    body = body,
                    type = NotificationType.TASK_DUE_SOON,
                    taskId = taskId
                )
            }
            "new_message" -> {
                showNotification(
                    title = title,
                    body = body,
                    type = NotificationType.NEW_MESSAGE,
                    conversationId = conversationId
                )
            }
            "task_completed" -> {
                showNotification(
                    title = title,
                    body = body,
                    type = NotificationType.TASK_COMPLETED,
                    taskId = taskId
                )
            }
            "task_updated" -> {
                showNotification(
                    title = title,
                    body = body,
                    type = NotificationType.TASK_UPDATED,
                    taskId = taskId
                )
            }
        }
    }

    private fun handleNotificationMessage(notification: RemoteMessage.Notification) {
        showNotification(
            title = notification.title ?: "SafeCom",
            body = notification.body ?: "",
            type = NotificationType.GENERAL
        )
    }

    private fun showNotification(
        title: String,
        body: String,
        type: NotificationType,
        taskId: String? = null,
        conversationId: String? = null
    ) {
        val intent = Intent(this, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            when (type) {
                NotificationType.TASK_ASSIGNED,
                NotificationType.TASK_DUE_SOON,
                NotificationType.TASK_COMPLETED,
                NotificationType.TASK_UPDATED -> {
                    putExtra("navigate_to", "task_detail")
                    putExtra("task_id", taskId)
                }
                NotificationType.NEW_MESSAGE -> {
                    putExtra("navigate_to", "conversation")
                    putExtra("conversation_id", conversationId)
                }
                NotificationType.GENERAL -> {
                    putExtra("navigate_to", "dashboard")
                }
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Set custom icon based on notification type
        when (type) {
            NotificationType.TASK_ASSIGNED -> {
                notificationBuilder.setSmallIcon(R.drawable.ic_assignment)
                notificationBuilder.setColor(getColor(R.color.primary))
            }
            NotificationType.TASK_DUE_SOON -> {
                notificationBuilder.setSmallIcon(R.drawable.ic_schedule)
                notificationBuilder.setColor(getColor(R.color.warning))
                notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH)
            }
            NotificationType.NEW_MESSAGE -> {
                notificationBuilder.setSmallIcon(R.drawable.ic_message)
                notificationBuilder.setColor(getColor(R.color.info))
            }
            NotificationType.TASK_COMPLETED -> {
                notificationBuilder.setSmallIcon(R.drawable.ic_check_circle)
                notificationBuilder.setColor(getColor(R.color.success))
            }
            NotificationType.TASK_UPDATED -> {
                notificationBuilder.setSmallIcon(R.drawable.ic_edit)
                notificationBuilder.setColor(getColor(R.color.info))
            }
            NotificationType.GENERAL -> {
                notificationBuilder.setSmallIcon(R.drawable.ic_notification)
                notificationBuilder.setColor(getColor(R.color.primary))
            }
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendRegistrationTokenToServer(token: String) {
        // TODO: Implement API call to send FCM token to your server
        // This should be done through a repository/API service
    }

    enum class NotificationType {
        TASK_ASSIGNED,
        TASK_DUE_SOON,
        NEW_MESSAGE,
        TASK_COMPLETED,
        TASK_UPDATED,
        GENERAL
    }
}
