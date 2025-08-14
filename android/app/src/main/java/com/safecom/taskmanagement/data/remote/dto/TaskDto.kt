package com.safecom.taskmanagement.data.remote.dto

import com.safecom.taskmanagement.domain.model.*
import java.util.Date

// Task DTOs
data class TaskDto(
    val id: String,
    val title: String,
    val description: String,
    val status: String,
    val priority: String,
    val assignedTo: String?,
    val assignedBy: String?,
    val createdBy: String,
    val dueDate: Long?,
    val createdAt: Long,
    val updatedAt: Long,
    val completedAt: Long?,
    val tags: List<String> = emptyList(),
    val attachments: List<String> = emptyList(),
    val estimatedHours: Int?,
    val actualHours: Int?,
    val category: String?,
    val location: String?,
    val reminderDate: Long?,
    val isRecurring: Boolean = false,
    val recurringPattern: String?
) {
    fun toDomainModel(): Task {
        return Task(
            id = id,
            title = title,
            description = description,
            status = TaskStatus.valueOf(status),
            priority = priority,
            assignedTo = assignedTo,
            assignedBy = assignedBy,
            createdBy = createdBy,
            dueDate = dueDate?.let { Date(it) },
            createdAt = Date(createdAt),
            updatedAt = Date(updatedAt),
            completedAt = completedAt?.let { Date(it) },
            tags = tags,
            attachments = attachments,
            estimatedHours = estimatedHours,
            actualHours = actualHours,
            category = category,
            location = location,
            reminderDate = reminderDate?.let { Date(it) },
            isRecurring = isRecurring,
            recurringPattern = recurringPattern
        )
    }
}

data class CreateTaskDto(
    val title: String,
    val description: String,
    val priority: String,
    val assignedTo: String?,
    val dueDate: Long?,
    val tags: List<String> = emptyList(),
    val estimatedHours: Int?,
    val category: String?,
    val location: String?,
    val reminderDate: Long?,
    val isRecurring: Boolean = false,
    val recurringPattern: String?
)

data class UpdateTaskDto(
    val title: String,
    val description: String,
    val priority: String,
    val assignedTo: String?,
    val dueDate: Long?,
    val tags: List<String> = emptyList(),
    val estimatedHours: Int?,
    val actualHours: Int?,
    val category: String?,
    val location: String?,
    val reminderDate: Long?,
    val isRecurring: Boolean = false,
    val recurringPattern: String?
)

data class UpdateTaskStatusDto(
    val status: String
)

data class TaskCommentDto(
    val id: String,
    val taskId: String,
    val userId: String,
    val userName: String,
    val userAvatar: String?,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long?,
    val attachments: List<String> = emptyList()
) {
    fun toDomainModel(): TaskComment {
        return TaskComment(
            id = id,
            taskId = taskId,
            userId = userId,
            userName = userName,
            userAvatar = userAvatar,
            content = content,
            createdAt = Date(createdAt),
            updatedAt = updatedAt?.let { Date(it) },
            attachments = attachments
        )
    }
}

data class CreateCommentDto(
    val content: String,
    val attachments: List<String> = emptyList()
)

data class RecentActivityDto(
    val id: String,
    val type: String,
    val title: String,
    val description: String,
    val timestamp: Long,
    val userId: String,
    val userName: String,
    val userAvatar: String?,
    val taskId: String?,
    val taskTitle: String?,
    val icon: String?
) {
    fun toDomainModel(): RecentActivity {
        return RecentActivity(
            id = id,
            type = ActivityType.valueOf(type),
            title = title,
            description = description,
            timestamp = Date(timestamp),
            userId = userId,
            userName = userName,
            userAvatar = userAvatar,
            taskId = taskId,
            taskTitle = taskTitle,
            icon = icon
        )
    }
}

data class AttachmentDto(
    val id: String,
    val fileName: String,
    val fileUrl: String,
    val fileSize: Long,
    val mimeType: String,
    val thumbnailUrl: String?
)

data class UploadAttachmentDto(
    val fileBase64: String,
    val fileName: String,
    val mimeType: String
)

// Extension functions for domain to DTO conversion
fun Task.toDto(): UpdateTaskDto {
    return UpdateTaskDto(
        title = title,
        description = description,
        priority = priority,
        assignedTo = assignedTo,
        dueDate = dueDate?.time,
        tags = tags,
        estimatedHours = estimatedHours,
        actualHours = actualHours,
        category = category,
        location = location,
        reminderDate = reminderDate?.time,
        isRecurring = isRecurring,
        recurringPattern = recurringPattern
    )
}

fun Task.toCreateDto(): CreateTaskDto {
    return CreateTaskDto(
        title = title,
        description = description,
        priority = priority,
        assignedTo = assignedTo,
        dueDate = dueDate?.time,
        tags = tags,
        estimatedHours = estimatedHours,
        category = category,
        location = location,
        reminderDate = reminderDate?.time,
        isRecurring = isRecurring,
        recurringPattern = recurringPattern
    )
}
