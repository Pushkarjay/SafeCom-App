package com.safecom.taskmanagement.data.local.dao

import androidx.room.*
import com.safecom.taskmanagement.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE isActive = 1 ORDER BY name ASC")
    suspend fun getAllActiveUsers(): List<UserEntity>
    
    @Query("SELECT * FROM users WHERE role = :role ORDER BY name ASC")
    suspend fun getUsersByRole(role: String): List<UserEntity>
    
    @Query("SELECT * FROM users WHERE department = :department ORDER BY name ASC")
    suspend fun getUsersByDepartment(department: String): List<UserEntity>
    
    @Query("SELECT * FROM users WHERE name LIKE :query OR email LIKE :query ORDER BY name ASC")
    suspend fun searchUsers(query: String): List<UserEntity>
    
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserFlow(userId: String): Flow<UserEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)
    
    @Update
    suspend fun updateUser(user: UserEntity)
    
    @Query("UPDATE users SET profileImageUrl = :imageUrl, updatedAt = :updatedAt WHERE id = :userId")
    suspend fun updateProfileImage(userId: String, imageUrl: String?, updatedAt: Long = System.currentTimeMillis())
    
    @Query("UPDATE users SET lastLoginAt = :loginTime WHERE id = :userId")
    suspend fun updateLastLogin(userId: String, loginTime: Long)
    
    @Query("UPDATE users SET isActive = :isActive WHERE id = :userId")
    suspend fun updateUserStatus(userId: String, isActive: Boolean)
    
    @Delete
    suspend fun deleteUser(user: UserEntity)
    
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)
    
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}
