package com.safecom.taskmanagement.data.remote.dto

import com.safecom.taskmanagement.domain.model.*
import java.util.Date

// Response DTOs
data class LoginResponseDto(
    val token: String,
    val refreshToken: String,
    val user: UserDto
) {
    fun toDomainModel(): User = user.toDomainModel()
}

data class RegisterResponseDto(
    val token: String,
    val refreshToken: String,
    val user: UserDto
) {
    fun toDomainModel(): User = user.toDomainModel()
}

data class RefreshTokenResponseDto(
    val token: String,
    val refreshToken: String
)

data class TokenResponseDto(
    val token: String,
    val refreshToken: String
)

// User DTOs
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val profileImageUrl: String? = null,
    val role: String,
    val department: String? = null,
    val phoneNumber: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val lastLoginAt: Long? = null,
    val isActive: Boolean = true,
    val settings: UserSettingsDto = UserSettingsDto()
) {
    fun toDomainModel(): User {
        return User(
            id = id,
            name = name,
            email = email,
            profileImageUrl = profileImageUrl,
            role = role,
            department = department,
            phoneNumber = phoneNumber,
            createdAt = Date(createdAt),
            updatedAt = Date(updatedAt),
            lastLoginAt = lastLoginAt?.let { Date(it) },
            isActive = isActive,
            settings = settings.toDomainModel()
        )
    }
}

data class UserSettingsDto(
    val isDarkModeEnabled: Boolean = false,
    val isNotificationEnabled: Boolean = true,
    val isPushNotificationEnabled: Boolean = true,
    val isEmailNotificationEnabled: Boolean = true,
    val language: String = "en",
    val timezone: String = "UTC",
    val workingHoursStart: String = "09:00",
    val workingHoursEnd: String = "17:00"
) {
    fun toDomainModel(): UserSettings {
        return UserSettings(
            isDarkModeEnabled = isDarkModeEnabled,
            isNotificationEnabled = isNotificationEnabled,
            isPushNotificationEnabled = isPushNotificationEnabled,
            isEmailNotificationEnabled = isEmailNotificationEnabled,
            language = language,
            timezone = timezone,
            workingHoursStart = workingHoursStart,
            workingHoursEnd = workingHoursEnd
        )
    }
}

data class UpdateProfileDto(
    val name: String,
    val email: String,
    val department: String?,
    val phoneNumber: String?,
    val settings: UserSettingsDto
)

data class UserTaskStatisticsDto(
    val completedTasks: Int,
    val pendingTasks: Int,
    val inProgressTasks: Int,
    val overdueTasks: Int,
    val totalTasksCreated: Int,
    val averageCompletionTime: Double,
    val productivityScore: Float
) {
    fun toDomainModel(): UserTaskStatistics {
        return UserTaskStatistics(
            completedTasks = completedTasks,
            pendingTasks = pendingTasks,
            inProgressTasks = inProgressTasks,
            overdueTasks = overdueTasks,
            totalTasksCreated = totalTasksCreated,
            averageCompletionTime = averageCompletionTime,
            productivityScore = productivityScore
        )
    }
}

// Extension functions for domain to DTO conversion
fun User.toUpdateProfileDto(): UpdateProfileDto {
    return UpdateProfileDto(
        name = name,
        email = email,
        department = department,
        phoneNumber = phoneNumber,
        settings = UserSettingsDto(
            isDarkModeEnabled = settings.isDarkModeEnabled,
            isNotificationEnabled = settings.isNotificationEnabled,
            isPushNotificationEnabled = settings.isPushNotificationEnabled,
            isEmailNotificationEnabled = settings.isEmailNotificationEnabled,
            language = settings.language,
            timezone = settings.timezone,
            workingHoursStart = settings.workingHoursStart,
            workingHoursEnd = settings.workingHoursEnd
        )
    )
}
