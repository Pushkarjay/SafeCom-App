package com.safecom.taskmanagement.data.repository

import com.safecom.taskmanagement.data.local.dao.TaskDao
import com.safecom.taskmanagement.data.local.entity.toEntity
import com.safecom.taskmanagement.data.remote.api.TaskApiService
import com.safecom.taskmanagement.data.remote.api.CompatTaskApiService
import com.safecom.taskmanagement.data.remote.api.toTaskDto
import com.safecom.taskmanagement.data.local.preferences.UserPreferences
import com.safecom.taskmanagement.data.remote.dto.toCreateDto
import com.safecom.taskmanagement.data.remote.dto.toDto
import com.safecom.taskmanagement.data.remote.dto.UpdateTaskStatusDto
import com.safecom.taskmanagement.domain.model.RecentActivity
import com.safecom.taskmanagement.domain.model.Task
import com.safecom.taskmanagement.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskApiService: TaskApiService,
    private val compatTaskApiService: CompatTaskApiService,
    private val taskDao: TaskDao,
    private val userPreferences: UserPreferences
) {

    suspend fun getAllTasks(): List<Task> {
        // Prefer compat role-based endpoint first (B option)
        return try {
            val role = (userPreferences.getUserRole() ?: "employee").lowercase()
            val compat = compatTaskApiService.getRoleTasks(role)
            val currentUserId = userPreferences.getCurrentUserId()
            val mapped = compat.map { it.toTaskDto(currentUserId).toDomainModel() }
            // Cache
            taskDao.insertTasks(mapped.map { it.toEntity() })
            mapped
        } catch (_: Exception) {
            try {
                val apiTasks = taskApiService.getAllTasks()
                taskDao.insertTasks(apiTasks.map { it.toDomainModel().toEntity() })
                apiTasks.map { it.toDomainModel() }
            } catch (e: Exception) {
                taskDao.getAllTasks().map { it.toDomainModel() }
            }
        }
    }

    suspend fun getTaskById(taskId: String): Task? {
        return try {
            val apiTask = taskApiService.getTaskById(taskId)
            taskDao.insertTask(apiTask.toDomainModel().toEntity())
            apiTask.toDomainModel()
        } catch (e: Exception) {
            taskDao.getTaskById(taskId)?.toDomainModel()
        }
    }

    suspend fun createTask(task: Task): Result<Task> {
        return try {
            val createdTask = taskApiService.createTask(task.toCreateDto())
            taskDao.insertTask(createdTask.toDomainModel().toEntity())
            Result.success(createdTask.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTask(task: Task): Result<Task> {
        return try {
            val updatedTask = taskApiService.updateTask(task.id, task.toDto())
            taskDao.updateTask(updatedTask.toDomainModel().toEntity())
            Result.success(updatedTask.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTaskStatus(taskId: String, status: TaskStatus): Result<Task> {
        return try {
            val updatedTask = taskApiService.updateTaskStatus(taskId, UpdateTaskStatusDto(status.name))
            taskDao.updateTaskStatus(taskId, status.name)
            Result.success(updatedTask.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            taskApiService.deleteTask(taskId)
            taskDao.deleteTask(taskId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTasksByStatus(status: TaskStatus): List<Task> {
        return try {
            val apiTasks = taskApiService.getTasksByStatus(status.name)
            apiTasks.map { it.toDomainModel() }
        } catch (e: Exception) {
            taskDao.getTasksByStatus(status.name).map { it.toDomainModel() }
        }
    }

    suspend fun getTasksByAssignee(userId: String): List<Task> {
        return try {
            val apiTasks = taskApiService.getTasksByAssignee(userId)
            apiTasks.map { it.toDomainModel() }
        } catch (e: Exception) {
            taskDao.getTasksByAssignee(userId).map { it.toDomainModel() }
        }
    }

    suspend fun searchTasks(query: String): List<Task> {
        return try {
            val apiTasks = taskApiService.searchTasks(query)
            apiTasks.map { it.toDomainModel() }
        } catch (e: Exception) {
            taskDao.searchTasks("%$query%").map { it.toDomainModel() }
        }
    }

    suspend fun getTotalTasksCount(): Int {
        return try {
            taskApiService.getTasksCount()
        } catch (e: Exception) {
            taskDao.getTotalTasksCount()
        }
    }

    suspend fun getPendingTasksCount(): Int {
        return try {
            taskApiService.getTasksCountByStatus(TaskStatus.PENDING.name)
        } catch (e: Exception) {
            taskDao.getTasksCountByStatus(TaskStatus.PENDING.name)
        }
    }

    suspend fun getInProgressTasksCount(): Int {
        return try {
            taskApiService.getTasksCountByStatus(TaskStatus.IN_PROGRESS.name)
        } catch (e: Exception) {
            taskDao.getTasksCountByStatus(TaskStatus.IN_PROGRESS.name)
        }
    }

    suspend fun getCompletedTasksCount(): Int {
        return try {
            taskApiService.getTasksCountByStatus(TaskStatus.COMPLETED.name)
        } catch (e: Exception) {
            taskDao.getTasksCountByStatus(TaskStatus.COMPLETED.name)
        }
    }

    suspend fun getRecentActivities(limit: Int = 10): List<RecentActivity> {
        return try {
            val role = (userPreferences.getUserRole() ?: "employee").lowercase()
            // Use compat activity endpoint where available (admin/employee)
            val compatActivities = compatTaskApiService.getRoleActivity(role)
            compatActivities.map { raw ->
                // Map minimal fields to domain RecentActivity (fallback values for missing)
                RecentActivity(
                    id = raw.timestamp ?: System.currentTimeMillis().toString(),
                    type = try { ActivityType.valueOf((raw.type ?: "TASK").uppercase()) } catch (_: Exception) { ActivityType.TASK },
                    title = raw.message ?: "Activity",
                    description = raw.message ?: "",
                    timestamp = java.util.Date(
                        try { java.time.Instant.parse(raw.timestamp).toEpochMilli() } catch (_: Exception) { System.currentTimeMillis() }
                    ),
                    userId = userPreferences.getCurrentUserId() ?: "",
                    userName = "",
                    userAvatar = null,
                    taskId = null,
                    taskTitle = null,
                    icon = null
                )
            }
        } catch (_: Exception) {
            try {
                val apiActivities = taskApiService.getRecentActivities(limit)
                apiActivities.map { it.toDomainModel() }
            } catch (e: Exception) { emptyList() }
        }
    }

    fun getTasksFlow(): Flow<List<Task>> = flow {
        try {
            val tasks = getAllTasks()
            emit(tasks)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}
