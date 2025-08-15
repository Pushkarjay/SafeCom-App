package com.safecom.taskmanagement.data.mappers

import com.safecom.taskmanagement.data.local.entity.*
import com.safecom.taskmanagement.data.remote.dto.*
import com.safecom.taskmanagement.domain.model.*
import java.util.*

// Message Mappers
fun Message.toEntity(): MessageEntity {
    return MessageEntity(
        id = id,
        senderId = senderId,
        receiverId = receiverId,
        conversationId = conversationId,
        content = content,
        messageType = messageType.name,
        timestamp = timestamp.time,
        isRead = isRead,
        readAt = readAt?.time,
        replyToMessageId = replyToMessageId,
        isEdited = isEdited,
        editedAt = editedAt?.time
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
        replyToMessageId = replyToMessageId,
        isEdited = isEdited,
        editedAt = editedAt?.time
    )
}

// Conversation Mappers  
fun Conversation.toEntity(): ConversationEntity {
    return ConversationEntity(
        id = id,
        participantIds = participantIds,
        lastMessageId = lastMessageId,
        lastMessageContent = lastMessageContent,
        lastMessageTimestamp = lastMessageTimestamp.time,
        unreadCount = unreadCount,
        isArchived = isArchived,
        isMuted = isMuted,
        createdAt = createdAt.time,
        updatedAt = updatedAt.time
    )
}
