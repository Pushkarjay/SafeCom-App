package com.safecom.taskmanagement.domain.model

import java.util.Date

data class Message(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val conversationId: String,
    val content: String,
    val messageType: MessageType,
    val timestamp: Date,
    val isRead: Boolean = false,
    val readAt: Date? = null,
    val attachments: List<MessageAttachment> = emptyList(),
    val replyToMessageId: String? = null,
    val isEdited: Boolean = false,
    val editedAt: Date? = null
)

enum class MessageType {
    TEXT,
    IMAGE,
    FILE,
    VOICE,
    LOCATION,
    TASK_REFERENCE,
    SYSTEM_NOTIFICATION
}

data class MessageAttachment(
    val id: String,
    val fileName: String,
    val fileUrl: String,
    val fileSize: Long,
    val mimeType: String,
    val thumbnailUrl: String? = null
)

data class Conversation(
    val id: String,
    val participantIds: List<String>,
    val participantName: String, // For display in conversation list
    val participantAvatar: String? = null,
    val lastMessage: String,
    val lastMessageTime: Date,
    val unreadCount: Int = 0,
    val createdAt: Date,
    val updatedAt: Date,
    val isGroup: Boolean = false,
    val groupName: String? = null,
    val groupAvatar: String? = null,
    val isArchived: Boolean = false,
    val isMuted: Boolean = false
)

data class RecentActivity(
    val id: String,
    val type: ActivityType,
    val title: String,
    val description: String,
    val timestamp: Date,
    val userId: String,
    val userName: String,
    val userAvatar: String? = null,
    val taskId: String? = null,
    val taskTitle: String? = null,
    val icon: String? = null
)

enum class ActivityType {
    TASK_CREATED,
    TASK_UPDATED,
    TASK_COMPLETED,
    TASK_ASSIGNED,
    MESSAGE_SENT,
    USER_JOINED,
    COMMENT_ADDED,
    FILE_UPLOADED,
    DEADLINE_APPROACHING
}
