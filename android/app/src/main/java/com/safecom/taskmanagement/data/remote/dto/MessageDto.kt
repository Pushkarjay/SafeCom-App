package com.safecom.taskmanagement.data.remote.dto

import com.safecom.taskmanagement.domain.model.*
import java.util.Date

// Message DTOs
data class MessageDto(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val conversationId: String,
    val content: String,
    val messageType: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val readAt: Long? = null,
    val attachments: List<MessageAttachmentDto> = emptyList(),
    val replyToMessageId: String? = null,
    val isEdited: Boolean = false,
    val editedAt: Long? = null
) {
    fun toDomainModel(): Message {
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
            attachments = attachments.map { it.toDomainModel() },
            replyToMessageId = replyToMessageId,
            isEdited = isEdited,
            editedAt = editedAt?.let { Date(it) }
        )
    }
}

data class MessageAttachmentDto(
    val id: String,
    val fileName: String,
    val fileUrl: String,
    val fileSize: Long,
    val mimeType: String,
    val thumbnailUrl: String? = null
) {
    fun toDomainModel(): MessageAttachment {
        return MessageAttachment(
            id = id,
            fileName = fileName,
            fileUrl = fileUrl,
            fileSize = fileSize,
            mimeType = mimeType,
            thumbnailUrl = thumbnailUrl
        )
    }
}

data class SendMessageDto(
    val receiverId: String,
    val conversationId: String?,
    val content: String,
    val messageType: String = "TEXT",
    val attachments: List<MessageAttachmentDto> = emptyList(),
    val replyToMessageId: String? = null
)

data class ConversationDto(
    val id: String,
    val participantIds: List<String>,
    val participantName: String,
    val participantAvatar: String? = null,
    val lastMessage: String,
    val lastMessageTime: Long,
    val unreadCount: Int = 0,
    val createdAt: Long,
    val updatedAt: Long,
    val isGroup: Boolean = false,
    val groupName: String? = null,
    val groupAvatar: String? = null,
    val isArchived: Boolean = false,
    val isMuted: Boolean = false
) {
    fun toDomainModel(): Conversation {
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
}

data class CreateConversationDto(
    val participantIds: List<String>,
    val isGroup: Boolean = false,
    val groupName: String? = null,
    val initialMessage: String? = null
)

// Extension functions for domain to DTO conversion
fun Message.toDto(): SendMessageDto {
    return SendMessageDto(
        receiverId = receiverId,
        conversationId = conversationId,
        content = content,
        messageType = messageType.name,
        attachments = attachments.map { it.toDto() },
        replyToMessageId = replyToMessageId
    )
}

fun MessageAttachment.toDto(): MessageAttachmentDto {
    return MessageAttachmentDto(
        id = id,
        fileName = fileName,
        fileUrl = fileUrl,
        fileSize = fileSize,
        mimeType = mimeType,
        thumbnailUrl = thumbnailUrl
    )
}

fun Conversation.toDto(): ConversationDto {
    return ConversationDto(
        id = id,
        participantIds = participantIds,
        participantName = participantName,
        participantAvatar = participantAvatar,
        lastMessage = lastMessage,
        lastMessageTime = lastMessageTime.time,
        unreadCount = unreadCount,
        createdAt = createdAt.time,
        updatedAt = updatedAt.time,
        isGroup = isGroup,
        groupName = groupName,
        groupAvatar = groupAvatar,
        isArchived = isArchived,
        isMuted = isMuted
    )
}
