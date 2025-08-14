package com.safecom.taskmanagement.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.safecom.taskmanagement.domain.model.Message
import com.safecom.taskmanagement.domain.model.MessageType
import java.util.Date

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val senderId: String,
    val receiverId: String,
    val conversationId: String,
    val content: String,
    val messageType: String,
    val timestamp: Long,
    val isRead: Boolean,
    val readAt: Long?,
    val attachments: List<String>,
    val replyToMessageId: String?,
    val isEdited: Boolean,
    val editedAt: Long?
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
            attachments = emptyList(), // TODO: Convert from strings to MessageAttachment objects
            replyToMessageId = replyToMessageId,
            isEdited = isEdited,
            editedAt = editedAt?.let { Date(it) }
        )
    }
}

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey
    val id: String,
    val participantIds: List<String>,
    val participantName: String,
    val participantAvatar: String?,
    val lastMessage: String,
    val lastMessageTime: Long,
    val unreadCount: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val isGroup: Boolean,
    val groupName: String?,
    val groupAvatar: String?,
    val isArchived: Boolean,
    val isMuted: Boolean
) {
    fun toDomainModel(): com.safecom.taskmanagement.domain.model.Conversation {
        return com.safecom.taskmanagement.domain.model.Conversation(
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
        attachments = attachments.map { it.fileUrl }, // Simplified for now
        replyToMessageId = replyToMessageId,
        isEdited = isEdited,
        editedAt = editedAt?.time
    )
}

fun com.safecom.taskmanagement.domain.model.Conversation.toEntity(): ConversationEntity {
    return ConversationEntity(
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
