package com.safecom.taskmanagement.domain.model

import java.util.Date

data class User(
    val id: String,
    val name: String,
    val email: String,
    val profileImageUrl: String? = null,
    val role: String, // Admin, Manager, Employee
    val department: String? = null,
    val phoneNumber: String? = null,
    val createdAt: Date,
    val updatedAt: Date,
    val lastLoginAt: Date? = null,
    val isActive: Boolean = true,
    val settings: UserSettings = UserSettings()
)

data class UserSettings(
    val isDarkModeEnabled: Boolean = false,
    val isNotificationEnabled: Boolean = true,
    val isPushNotificationEnabled: Boolean = true,
    val isEmailNotificationEnabled: Boolean = true,
    val language: String = "en",
    val timezone: String = "UTC",
    val workingHoursStart: String = "09:00",
    val workingHoursEnd: String = "17:00"
)

data class UserTaskStatistics(
    val completedTasks: Int,
    val pendingTasks: Int,
    val inProgressTasks: Int,
    val overdueTasks: Int,
    val totalTasksCreated: Int,
    val averageCompletionTime: Double, // in days
    val productivityScore: Float
)
