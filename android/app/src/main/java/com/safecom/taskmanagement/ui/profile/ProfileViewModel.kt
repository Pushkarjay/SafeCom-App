package com.safecom.taskmanagement.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safecom.taskmanagement.data.repository.AuthRepository
import com.safecom.taskmanagement.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val _logoutState = MutableStateFlow(false)
    val logoutState: StateFlow<Boolean> = _logoutState.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            try {
                _profileState.value = ProfileState.Loading
                
                val user = userRepository.getCurrentUser()
                val taskStats = userRepository.getUserTaskStatistics()
                
                val profile = UserProfile(
                    id = user?.id ?: "",
                    name = user?.name ?: "",
                    email = user?.email ?: "",
                    role = user?.role ?: "",
                    profileImageUrl = user?.profileImageUrl,
                    memberSince = user?.createdAt?.let { formatDate(it.time) } ?: "",
                    completedTasks = taskStats.completedTasks,
                    pendingTasks = taskStats.pendingTasks,
                    isDarkModeEnabled = userRepository.isDarkModeEnabled(),
                    isNotificationEnabled = userRepository.isNotificationEnabled()
                )
                
                _profileState.value = ProfileState.Success(profile)
                
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "Failed to load profile")
            }
        }
    }

    fun updateProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            try {
                userRepository.updateProfileImage(imageUri)
                loadProfile() // Refresh profile
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun removeProfileImage() {
        viewModelScope.launch {
            try {
                userRepository.removeProfileImage()
                loadProfile() // Refresh profile
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateThemeMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            try {
                userRepository.setDarkModeEnabled(isDarkMode)
                // Apply theme change immediately
                applyThemeChange(isDarkMode)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateNotificationEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            try {
                userRepository.setNotificationEnabled(isEnabled)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.logout()
                _logoutState.value = true
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun formatDate(timestamp: Long): String {
        val dateFormat = java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault())
        return dateFormat.format(java.util.Date(timestamp))
    }

    private fun applyThemeChange(isDarkMode: Boolean) {
        // TODO: Implement theme change logic
        // This would typically involve updating the app's theme
    }
}

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val profile: UserProfile) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val profileImageUrl: String?,
    val memberSince: String,
    val completedTasks: Int,
    val pendingTasks: Int,
    val isDarkModeEnabled: Boolean,
    val isNotificationEnabled: Boolean
)
