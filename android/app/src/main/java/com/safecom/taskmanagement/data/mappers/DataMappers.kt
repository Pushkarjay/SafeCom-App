package com.safecom.taskmanagement.data.mappers

import com.safecom.taskmanagement.data.local.entity.*
import com.safecom.taskmanagement.data.remote.dto.*
import com.safecom.taskmanagement.domain.model.*
import java.util.*

// Note: UserDto.toDomainModel() is defined in AuthUserDto.kt to avoid circular dependencies

// User Entity Mappers

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
        profileImageUrl = profileImageUrl,
        role = role,
        department = department,
        phoneNumber = phoneNumber,
        createdAt = createdAt.time,
        updatedAt = updatedAt.time,
        lastLoginAt = lastLoginAt?.time,
        isActive = isActive,
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

/*
fun User.toUpdateUserDto(): UpdateUserDto {
    return UpdateUserDto(
        name = name,
        email = email,
        role = role,
        department = department,
        phoneNumber = phoneNumber,
        isActive = isActive
    )
}
*/

// Task Mappers

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
        // If Task model used in TaskEntity.toDomainModel() also needs settings, it's missing here too.
        // Assuming this Task constructor doesn't need 'settings' or it's provided via other fields from TaskEntity.
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

// Message Mappers
fun MessageDto.toEntity(): MessageEntity {
    return MessageEntity(
        id = id,
        senderId = senderId,
        receiverId = receiverId,
        conversationId = conversationId,
        content = content,
        messageType = messageType,
        timestamp = timestamp,
        isRead = isRead,
        readAt = readAt,
        attachments = attachments.map { it.fileUrl },
        replyToMessageId = replyToMessageId,
        isEdited = isEdited,
        editedAt = editedAt
    )
}

fun MessageDto.toDomainModel(): Message {
    return Message(
        id = id,
        senderId = senderId,
        receiverId = receiverId,
        conversationId = conversationId,
        content = content,
        messageType = MessageType.valueOf(messageType),
        timestamp = Date(timestamp),
        isRead = isRead,
        readAt = readAt?.let { Date(it) },
        attachments = emptyList(), // TODO: Implement attachment mapping
        replyToMessageId = replyToMessageId,
        isEdited = isEdited,
        editedAt = editedAt?.let { Date(it) }
    )
}

fun Message.toDto(): MessageDto {
    return MessageDto(
        id = id,
        senderId = senderId,
        receiverId = receiverId,
        conversationId = conversationId,
        content = content,
        messageType = messageType.name,
        timestamp = timestamp.time,
        isRead = isRead,
        readAt = readAt?.time,
        attachments = emptyList(), // TODO: Implement attachment mapping
        replyToMessageId = replyToMessageId,
        isEdited = isEdited,
        editedAt = editedAt?.time
    )
}

fun Message.toSendMessageDto(): SendMessageDto {
    return SendMessageDto(
        receiverId = receiverId,
        conversationId = conversationId,
        content = content,
        messageType = messageType.name,
        replyToMessageId = replyToMessageId,
        attachments = emptyList()
    )
}

// Conversation Mappers
fun ConversationDto.toEntity(): ConversationEntity {
    return ConversationEntity(
        id = id,
        participantIds = participantIds,
        participantName = participantName,
        participantAvatar = participantAvatar,
        lastMessage = lastMessage,
        lastMessageTime = lastMessageTime,
        unreadCount = unreadCount,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isGroup = isGroup,
        groupName = groupName,
        groupAvatar = groupAvatar,
        isArchived = isArchived,
        isMuted = isMuted
    )
}

fun ConversationDto.toDomainModel(): Conversation {
    return Conversation(
        id = id,
        participantIds = participantIds,
        participantName = participantName,
        participantAvatar = participantAvatar,
        lastMessage = lastMessage,
        lastMessageTime = Date(lastMessageTime),
        unreadCount = unreadCount,
        createdAt = Date(createdAt),
        updatedAt = Date(updatedAt),
        isGroup = isGroup,
        groupName = groupName,
        groupAvatar = groupAvatar,
        isArchived = isArchived,
        isMuted = isMuted
    )
}
