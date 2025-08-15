package com.safecom.taskmanagement.data.mappers

import com.safecom.taskmanagement.data.local.entity.*
import com.safecom.taskmanagement.data.remote.dto.*
import com.safecom.taskmanagement.domain.model.*
import java.util.*

// User Mappers
fun UserDto.toDomainModel(): User {
    return User(
        id = id,
        name = name,
        email = email,
        role = role,
        profileImageUrl = profileImageUrl,
        createdAt = Date(createdAt),
        updatedAt = Date(updatedAt)
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        role = role,
        profileImageUrl = profileImageUrl,
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

fun UserEntity.toDomainModel(): User {
    return User(
        id = id,
        name = name,
        email = email,
        role = role,
        profileImageUrl = profileImageUrl,
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

fun User.toDto(): UserDto {
    return UserDto(
        id = id,
        name = name,
        email = email,
        role = role,
        profileImageUrl = profileImageUrl,
        createdAt = createdAt.time,
        updatedAt = updatedAt.time
    )
}

// Task Mappers
fun TaskDto.toDomainModel(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        priority = priority,
        status = TaskStatus.valueOf(status),
        dueDate = dueDate?.let { Date(it) },
        assignedTo = assignedTo,
        assignedBy = assignedBy,
        createdBy = createdBy,
        createdAt = Date(createdAt),
        updatedAt = Date(updatedAt),
        tags = tags,
        attachments = attachments
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        priority = priority,
        status = status.name,
        dueDate = dueDate?.time,
        assignedTo = assignedTo,
        assignedBy = assignedBy,
        createdBy = createdBy,
        createdAt = createdAt.time,
        updatedAt = updatedAt.time,
        completedAt = completedAt?.time,
        estimatedHours = estimatedHours,
        actualHours = actualHours,
        category = category,
        location = location,
        reminderDate = reminderDate?.time,
        isRecurring = isRecurring,
        recurringPattern = recurringPattern,
        tags = tags,
        attachments = attachments
    )
}

fun TaskEntity.toDomainModel(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        priority = priority,
        status = TaskStatus.valueOf(status),
        dueDate = dueDate?.let { Date(it) },
        assignedTo = assignedTo,
        assignedBy = assignedBy,
        createdBy = createdBy,
        createdAt = Date(createdAt),
        updatedAt = Date(updatedAt),
        completedAt = completedAt?.let { Date(it) },
        estimatedHours = estimatedHours,
        actualHours = actualHours,
        category = category,
        location = location,
        reminderDate = reminderDate?.let { Date(it) },
        isRecurring = isRecurring,
        recurringPattern = recurringPattern,
        tags = tags,
        attachments = attachments
    )
}

fun Task.toDto(): TaskDto {
    return TaskDto(
        id = id,
        title = title,
        description = description,
        priority = priority,
        status = status.name,
        dueDate = dueDate?.time,
        assignedTo = assignedTo,
        assignedBy = assignedBy,
        createdBy = createdBy,
        createdAt = createdAt.time,
        updatedAt = updatedAt.time,
        completedAt = completedAt?.time,
        estimatedHours = estimatedHours,
        actualHours = actualHours,
        category = category,
        location = location,
        reminderDate = reminderDate?.time,
        isRecurring = isRecurring,
        recurringPattern = recurringPattern,
        tags = tags,
        attachments = attachments
    )
}
