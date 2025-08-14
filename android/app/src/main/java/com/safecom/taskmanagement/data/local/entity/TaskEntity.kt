package com.safecom.taskmanagement.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.safecom.taskmanagement.domain.model.Task
import com.safecom.taskmanagement.domain.model.TaskStatus
import java.util.Date

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
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
    val tags: List<String>,
    val attachments: List<String>,
    val estimatedHours: Int?,
    val actualHours: Int?,
    val category: String?,
    val location: String?,
    val reminderDate: Long?,
    val isRecurring: Boolean,
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

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        status = status.name,
        priority = priority,
        assignedTo = assignedTo,
        assignedBy = assignedBy,
        createdBy = createdBy,
        dueDate = dueDate?.time,
        createdAt = createdAt.time,
        updatedAt = updatedAt.time,
        completedAt = completedAt?.time,
        tags = tags,
        attachments = attachments,
        estimatedHours = estimatedHours,
        actualHours = actualHours,
        category = category,
        location = location,
        reminderDate = reminderDate?.time,
        isRecurring = isRecurring,
        recurringPattern = recurringPattern
    )
}
