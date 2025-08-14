package com.safecom.taskmanagement.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.safecom.taskmanagement.domain.model.User
import com.safecom.taskmanagement.domain.model.UserSettings
import java.util.Date

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val profileImageUrl: String?,
    val role: String,
    val department: String?,
    val phoneNumber: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val lastLoginAt: Long?,
    val isActive: Boolean,
    val isDarkModeEnabled: Boolean,
    val isNotificationEnabled: Boolean,
    val isPushNotificationEnabled: Boolean,
    val isEmailNotificationEnabled: Boolean,
    val language: String,
    val timezone: String,
    val workingHoursStart: String,
    val workingHoursEnd: String
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
            settings = UserSettings(
                isDarkModeEnabled = isDarkModeEnabled,
                isNotificationEnabled = isNotificationEnabled,
                isPushNotificationEnabled = isPushNotificationEnabled,
                isEmailNotificationEnabled = isEmailNotificationEnabled,
                language = language,
                timezone = timezone,
                workingHoursStart = workingHoursStart,
                workingHoursEnd = workingHoursEnd
            )
        )
    }
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        profileImageUrl = profileImageUrl,
        role = role,
        department = department,
        phoneNumber = phoneNumber,
        createdAt = createdAt.time,
        updatedAt = updatedAt.time,
        lastLoginAt = lastLoginAt?.time,
        isActive = isActive,
        isDarkModeEnabled = settings.isDarkModeEnabled,
        isNotificationEnabled = settings.isNotificationEnabled,
        isPushNotificationEnabled = settings.isPushNotificationEnabled,
        isEmailNotificationEnabled = settings.isEmailNotificationEnabled,
        language = settings.language,
        timezone = settings.timezone,
        workingHoursStart = settings.workingHoursStart,
        workingHoursEnd = settings.workingHoursEnd
    )
}
