package com.safecom.taskmanagement.data.local.dao

import androidx.room.*
import com.safecom.taskmanagement.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    suspend fun getAllTasks(): List<TaskEntity>
    
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): TaskEntity?
    
    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY createdAt DESC")
    suspend fun getTasksByStatus(status: String): List<TaskEntity>
    
    @Query("SELECT * FROM tasks WHERE assignedTo = :userId ORDER BY createdAt DESC")
    suspend fun getTasksByAssignee(userId: String): List<TaskEntity>
    
    @Query("SELECT * FROM tasks WHERE createdBy = :userId ORDER BY createdAt DESC")
    suspend fun getTasksByCreator(userId: String): List<TaskEntity>
    
    @Query("SELECT * FROM tasks WHERE title LIKE :query OR description LIKE :query ORDER BY createdAt DESC")
    suspend fun searchTasks(query: String): List<TaskEntity>
    
    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getTotalTasksCount(): Int
    
    @Query("SELECT COUNT(*) FROM tasks WHERE status = :status")
    suspend fun getTasksCountByStatus(status: String): Int
    
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getTasksFlow(): Flow<List<TaskEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)
    
    @Update
    suspend fun updateTask(task: TaskEntity)
    
    @Query("UPDATE tasks SET status = :status, updatedAt = :updatedAt WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: String, status: String, updatedAt: Long = System.currentTimeMillis())
    
    @Delete
    suspend fun deleteTask(task: TaskEntity)
    
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: String)
    
    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
    
    @Query("SELECT * FROM tasks WHERE dueDate < :currentTime AND status != 'COMPLETED' ORDER BY dueDate ASC")
    suspend fun getOverdueTasks(currentTime: Long = System.currentTimeMillis()): List<TaskEntity>
    
    @Query("SELECT * FROM tasks WHERE dueDate BETWEEN :startTime AND :endTime ORDER BY dueDate ASC")
    suspend fun getTasksDueBetween(startTime: Long, endTime: Long): List<TaskEntity>
    
    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY createdAt DESC")
    suspend fun getTasksByCategory(category: String): List<TaskEntity>
    
    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY createdAt DESC")
    suspend fun getTasksByPriority(priority: String): List<TaskEntity>
}
