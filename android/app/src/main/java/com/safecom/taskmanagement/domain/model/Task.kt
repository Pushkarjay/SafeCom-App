package com.safecom.taskmanagement.domain.model

import java.util.Date

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val status: TaskStatus,
    val priority: String, // High, Medium, Low
    val assignedTo: String?, // User ID
    val assignedBy: String?, // User ID
    val createdBy: String,
    val dueDate: Date?,
    val createdAt: Date,
    val updatedAt: Date,
    val completedAt: Date? = null,
    val tags: List<String> = emptyList(),
    val attachments: List<String> = emptyList(),
    val comments: List<TaskComment> = emptyList(),
    val estimatedHours: Int? = null,
    val actualHours: Int? = null,
    val category: String? = null,
    val location: String? = null,
    val reminderDate: Date? = null,
    val isRecurring: Boolean = false,
    val recurringPattern: String? = null // daily, weekly, monthly
)

enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    OVERDUE,
    CANCELLED
}

data class TaskComment(
    val id: String,
    val taskId: String,
    val userId: String,
    val userName: String,
    val userAvatar: String?,
    val content: String,
    val createdAt: Date,
    val updatedAt: Date? = null,
    val attachments: List<String> = emptyList()
)

data class TaskStatistics(
    val totalTasks: Int,
    val pendingTasks: Int,
    val inProgressTasks: Int,
    val completedTasks: Int,
    val overdueTasks: Int,
    val completionRate: Float
)
