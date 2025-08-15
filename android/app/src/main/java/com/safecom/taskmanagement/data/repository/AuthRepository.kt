package com.safecom.taskmanagement.data.repository

import com.safecom.taskmanagement.data.local.preferences.UserPreferences
import com.safecom.taskmanagement.data.mappers.*
import com.safecom.taskmanagement.data.remote.api.AuthApiService
import com.safecom.taskmanagement.data.remote.dto.*
import com.safecom.taskmanagement.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val userPreferences: UserPreferences,
    private val userRepository: UserRepository
) {

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val loginResponse = authApiService.login(LoginRequestDto(email, password))
            
            // Save authentication token
            userPreferences.setAuthToken(loginResponse.token)
            userPreferences.setRefreshToken(loginResponse.refreshToken)
            userPreferences.setCurrentUserId(loginResponse.user.id)
            
            // Cache user data
            val user = loginResponse.user.toDomainModel()
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginWithBiometric(): Result<User> {
        return try {
            val userId = userPreferences.getCurrentUserId()
            if (userId != null && userPreferences.isBiometricEnabled()) {
                val user = userRepository.getUserById(userId)
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("User not found"))
                }
            } else {
                Result.failure(Exception("Biometric login not available"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        role: String = "Employee"
    ): Result<User> {
        return try {
            val registerResponse = authApiService.register(
                RegisterRequestDto(name, email, password, password, role)
            )
            
            // Save authentication token
            userPreferences.setAuthToken(registerResponse.token)
            userPreferences.setRefreshToken(registerResponse.refreshToken)
            userPreferences.setCurrentUserId(registerResponse.user.id)
            
            val user = registerResponse.user.toDomainModel()
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            // Call logout API if needed
            try {
                authApiService.logout()
            } catch (e: Exception) {
                // Continue with local logout even if API call fails
            }
            
            // Clear local data
            userRepository.clearUserData()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshToken(): Result<String> {
        return try {
            val refreshToken = userPreferences.getRefreshToken()
            if (refreshToken != null) {
                val tokenResponse = authApiService.refreshToken(RefreshTokenRequestDto(refreshToken))
                userPreferences.setAuthToken(tokenResponse.token)
                userPreferences.setRefreshToken(tokenResponse.refreshToken)
                Result.success(tokenResponse.token)
            } else {
                Result.failure(Exception("No refresh token available"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> {
        return try {
            authApiService.changePassword(
                ChangePasswordRequestDto(currentPassword, newPassword, newPassword)
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun forgotPassword(email: String): Result<Unit> {
        return try {
            authApiService.forgotPassword(ForgotPasswordRequestDto(email))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(token: String, newPassword: String): Result<Unit> {
        return try {
            authApiService.resetPassword(
                ResetPasswordRequestDto(token, newPassword, newPassword)
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isLoggedIn(): Boolean {
        val token = userPreferences.getAuthToken()
        val userId = userPreferences.getCurrentUserId()
        return !token.isNullOrEmpty() && !userId.isNullOrEmpty()
    }

    suspend fun getCurrentAuthToken(): String? {
        return userPreferences.getAuthToken()
    }
}
