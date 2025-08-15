package com.safecom.taskmanagement.data.mappers

import com.safecom.taskmanagement.data.local.entity.*
import com.safecom.taskmanagement.data.remote.dto.*
import com.safecom.taskmanagement.domain.model.*
import java.util.*

// User Mappers
fun UserDto.toDomainModel(): User {
    return User(
        id = id,
        fullName = fullName,
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
        fullName = fullName,
        email = email,
        role = role,
        profileImageUrl = profileImageUrl,
        createdAt = createdAt.time,
        updatedAt = updatedAt.time
    )
}

fun UserEntity.toDomainModel(): User {
    return User(
        id = id,
        fullName = fullName,
        email = email,
        role = role,
        profileImageUrl = profileImageUrl,
        createdAt = Date(createdAt),
        updatedAt = Date(updatedAt)
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        id = id,
        fullName = fullName,
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
        priority = TaskPriority.valueOf(priority),
        status = TaskStatus.valueOf(status),
        dueDate = dueDate?.let { Date(it) },
        assigneeId = assigneeId,
        assigneeName = assigneeName,
        createdBy = createdBy,
        createdAt = Date(createdAt),
        updatedAt = Date(updatedAt),
        tags = tags?.split(",") ?: emptyList(),
        attachments = attachments?.split(",") ?: emptyList()
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        priority = priority.name,
        status = status.name,
        dueDate = dueDate?.time,
        assigneeId = assigneeId,
        assigneeName = assigneeName,
        createdBy = createdBy,
        createdAt = createdAt.time,
        updatedAt = updatedAt.time,
        tags = tags.joinToString(","),
        attachments = attachments.joinToString(",")
    )
}

fun TaskEntity.toDomainModel(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        priority = TaskPriority.valueOf(priority),
        status = TaskStatus.valueOf(status),
        dueDate = dueDate?.let { Date(it) },
        assigneeId = assigneeId,
        assigneeName = assigneeName,
        createdBy = createdBy,
        createdAt = Date(createdAt),
        updatedAt = Date(updatedAt),
        tags = if (tags.isNotEmpty()) tags.split(",") else emptyList(),
        attachments = if (attachments.isNotEmpty()) attachments.split(",") else emptyList()
    )
}

fun Task.toDto(): TaskDto {
    return TaskDto(
        id = id,
        title = title,
        description = description,
        priority = priority.name,
        status = status.name,
        dueDate = dueDate?.time,
        assigneeId = assigneeId,
        assigneeName = assigneeName,
        createdBy = createdBy,
        createdAt = createdAt.time,
        updatedAt = updatedAt.time,
        tags = tags.joinToString(","),
        attachments = attachments.joinToString(",")
    )
}

// Message and Conversation Mappers
fun ConversationDto.toDomainModel(): Conversation {
    return Conversation(
        id = id,
        participantIds = participantIds.split(","),
        participantNames = participantNames.split(","),
        lastMessage = lastMessage,
        lastMessageTime = Date(lastMessageTime),
        isRead = isRead,
        isMuted = isMuted,
        createdAt = Date(createdAt)
    )
}

fun Conversation.toEntity(): ConversationEntity {
    return ConversationEntity(
        id = id,
        participantIds = participantIds.joinToString(","),
        participantNames = participantNames.joinToString(","),
        lastMessage = lastMessage,
        lastMessageTime = lastMessageTime.time,
        isRead = isRead,
        isMuted = isMuted,
        createdAt = createdAt.time
    )
}

fun ConversationEntity.toDomainModel(): Conversation {
    return Conversation(
        id = id,
        participantIds = participantIds.split(","),
        participantNames = participantNames.split(","),
        lastMessage = lastMessage,
        lastMessageTime = Date(lastMessageTime),
        isRead = isRead,
        isMuted = isMuted,
        createdAt = Date(createdAt)
    )
}

fun MessageDto.toDomainModel(): Message {
    return Message(
        id = id,
        conversationId = conversationId,
        senderId = senderId,
        senderName = senderName,
        content = content,
        messageType = MessageType.valueOf(messageType),
        attachmentUrl = attachmentUrl,
        isRead = isRead,
        timestamp = Date(timestamp)
    )
}

fun Message.toEntity(): MessageEntity {
    return MessageEntity(
        id = id,
        conversationId = conversationId,
        senderId = senderId,
        senderName = senderName,
        content = content,
        messageType = messageType.name,
        attachmentUrl = attachmentUrl,
        isRead = isRead,
        timestamp = timestamp.time
    )
}

fun MessageEntity.toDomainModel(): Message {
    return Message(
        id = id,
        conversationId = conversationId,
        senderId = senderId,
        senderName = senderName,
        content = content,
        messageType = MessageType.valueOf(messageType),
        attachmentUrl = attachmentUrl,
        isRead = isRead,
        timestamp = Date(timestamp)
    )
}

fun Message.toDto(): MessageDto {
    return MessageDto(
        id = id,
        conversationId = conversationId,
        senderId = senderId,
        senderName = senderName,
        content = content,
        messageType = messageType.name,
        attachmentUrl = attachmentUrl,
        isRead = isRead,
        timestamp = timestamp.time
    )
}
