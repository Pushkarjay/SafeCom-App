package com.safecom.taskmanagement.data.repository

import android.net.Uri
import com.safecom.taskmanagement.data.local.dao.UserDao
import com.safecom.taskmanagement.data.local.preferences.UserPreferences
import com.safecom.taskmanagement.data.mappers.*
import com.safecom.taskmanagement.data.remote.api.UserApiService
import com.safecom.taskmanagement.data.remote.dto.*
import com.safecom.taskmanagement.domain.model.User
import com.safecom.taskmanagement.domain.model.UserTaskStatistics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userApiService: UserApiService,
    private val userDao: UserDao,
    private val userPreferences: UserPreferences
) {

    suspend fun getCurrentUser(): User? {
        return try {
            val currentUserId = userPreferences.getCurrentUserId()
            if (currentUserId != null) {
                val apiUser = userApiService.getUserById(currentUserId)
                userDao.insertUser(apiUser.toEntity())
                apiUser.toDomainModel()
            } else {
                null
            }
        } catch (e: Exception) {
            val currentUserId = userPreferences.getCurrentUserId()
            currentUserId?.let { userDao.getUserById(it)?.toDomainModel() }
        }
    }

    suspend fun getUserById(userId: String): User? {
        return try {
            val apiUser = userApiService.getUserById(userId)
            userDao.insertUser(apiUser.toEntity())
            apiUser.toDomainModel()
        } catch (e: Exception) {
            userDao.getUserById(userId)?.toDomainModel()
        }
    }

    suspend fun updateProfile(user: User): Result<User> {
        return try {
            val updatedUser = userApiService.updateProfile(user.toUpdateProfileDto())
            userDao.updateUser(updatedUser.toEntity())
            Result.success(updatedUser.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfileImage(imageUri: Uri): Result<String> {
        return try {
            val uploadDto = UploadImageDto(
                imageBase64 = "base64_encoded_image_data", // Convert URI to base64
                fileName = "profile_${System.currentTimeMillis()}.jpg"
            )
            val imageUrl = userApiService.uploadProfileImage(uploadDto)
            val currentUser = getCurrentUser()
            currentUser?.let { user ->
                val updatedUser = user.copy(profileImageUrl = imageUrl)
                updateProfile(updatedUser)
            }
            Result.success(imageUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeProfileImage(): Result<Unit> {
        return try {
            userApiService.removeProfileImage()
            val currentUser = getCurrentUser()
            currentUser?.let { user ->
                val updatedUser = user.copy(profileImageUrl = null)
                updateProfile(updatedUser)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserTaskStatistics(): UserTaskStatistics {
        return try {
            val stats = userApiService.getUserTaskStatistics()
            stats.toDomainModel()
        } catch (e: Exception) {
            // Return default statistics
            UserTaskStatistics(
                completedTasks = 0,
                pendingTasks = 0,
                inProgressTasks = 0,
                overdueTasks = 0,
                totalTasksCreated = 0,
                averageCompletionTime = 0.0,
                productivityScore = 0f
            )
        }
    }

    // User Preferences
    suspend fun setCurrentUserId(userId: String) {
        userPreferences.setCurrentUserId(userId)
    }

    suspend fun getCurrentUserId(): String? {
        return userPreferences.getCurrentUserId()
    }

    suspend fun setDarkModeEnabled(enabled: Boolean) {
        userPreferences.setDarkModeEnabled(enabled)
    }

    suspend fun isDarkModeEnabled(): Boolean {
        return userPreferences.isDarkModeEnabled()
    }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        userPreferences.setNotificationEnabled(enabled)
    }

    suspend fun isNotificationEnabled(): Boolean {
        return userPreferences.isNotificationEnabled()
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        userPreferences.setBiometricEnabled(enabled)
    }

    suspend fun isBiometricEnabled(): Boolean {
        return userPreferences.isBiometricEnabled()
    }

    suspend fun setLanguage(language: String) {
        userPreferences.setLanguage(language)
    }

    suspend fun getLanguage(): String {
        return userPreferences.getLanguage()
    }

    suspend fun clearUserData() {
        userPreferences.clearAll()
        userDao.deleteAllUsers()
    }
}
