package com.safecom.taskmanagement

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * SafeCom Task Management Application
 * 
 * A comprehensive task management application with real-time messaging,
 * push notifications, and collaborative features.
 * 
 * @author Pushkarjay Ajay
 * @email pushkarjay.ajay1@gmail.com
 * @organization SafeCom
 * @version 1.0.0
 * @since 2025
 */
@HiltAndroidApp
class SafeComApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    companion object {
        const val APP_NAME = "SafeCom Task Management"
        const val DEVELOPER = "Pushkarjay Ajay"
        const val ORGANIZATION = "SafeCom"
        const val DEVELOPER_EMAIL = "pushkarjay.ajay1@gmail.com"
        const val VERSION = "1.0.0"
        const val REPOSITORY_URL = "https://github.com/Pushkarjay/SafeCom-App"
    }

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        initializeFirebase()
        
        // Create notification channels
        createNotificationChannels()
        
        // Initialize WorkManager
        initializeWorkManager()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    private fun initializeFirebase() {
        try {
            FirebaseApp.initializeApp(this)
            
            // Get FCM token
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@addOnCompleteListener
                }
                
                // Get new FCM registration token
                val token = task.result
                
                // TODO: Send token to server
                // You can send this token to your server for push notifications
            }
        } catch (e: Exception) {
            // Handle Firebase initialization error
        }
    }

    private fun initializeWorkManager() {
        try {
            WorkManager.initialize(this, workManagerConfiguration)
        } catch (e: Exception) {
            // WorkManager already initialized
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            // High Priority Channel for Critical Tasks
            val highPriorityChannel = NotificationChannel(
                CHANNEL_HIGH_PRIORITY,
                "High Priority Tasks",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical tasks and emergencies"
                enableVibration(true)
                enableLights(true)
            }

            // Medium Priority Channel for Regular Tasks
            val mediumPriorityChannel = NotificationChannel(
                CHANNEL_MEDIUM_PRIORITY,
                "Regular Tasks",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Regular task updates"
                enableVibration(true)
            }

            // Low Priority Channel for General Information
            val lowPriorityChannel = NotificationChannel(
                CHANNEL_LOW_PRIORITY,
                "General Information",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "General information and tips"
            }

            // Messages Channel
            val messagesChannel = NotificationChannel(
                CHANNEL_MESSAGES,
                "Messages",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Team communication messages"
                enableVibration(true)
                enableLights(true)
            }

            // System Channel
            val systemChannel = NotificationChannel(
                CHANNEL_SYSTEM,
                "System Notifications",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "App-related notifications"
            }

            notificationManager.createNotificationChannels(
                listOf(
                    highPriorityChannel,
                    mediumPriorityChannel,
                    lowPriorityChannel,
                    messagesChannel,
                    systemChannel
                )
            )
        }
    }

    companion object {
        const val CHANNEL_HIGH_PRIORITY = "high_priority_channel"
        const val CHANNEL_MEDIUM_PRIORITY = "medium_priority_channel"
        const val CHANNEL_LOW_PRIORITY = "low_priority_channel"
        const val CHANNEL_MESSAGES = "messages_channel"
        const val CHANNEL_SYSTEM = "system_channel"
    }
}
