package com.safecom.taskmanagement.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safecom.taskmanagement.data.repository.TaskRepository
import com.safecom.taskmanagement.data.repository.UserRepository
import com.safecom.taskmanagement.domain.model.RecentActivity
import com.safecom.taskmanagement.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _dashboardState = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val dashboardState: StateFlow<DashboardState> = _dashboardState.asStateFlow()

    private val _userInfo = MutableStateFlow<User?>(null)
    val userInfo: StateFlow<User?> = _userInfo.asStateFlow()

    fun loadDashboardData() {
        viewModelScope.launch {
            try {
                _dashboardState.value = DashboardState.Loading
                
                // Load user info
                val user = userRepository.getCurrentUser()
                _userInfo.value = user
                
                // Load task statistics
                val totalTasks = taskRepository.getTotalTasksCount()
                val pendingTasks = taskRepository.getPendingTasksCount()
                val inProgressTasks = taskRepository.getInProgressTasksCount()
                val completedTasks = taskRepository.getCompletedTasksCount()
                
                // Load recent activities
                val recentActivities = taskRepository.getRecentActivities(limit = 10)
                
                val dashboardData = DashboardData(
                    totalTasks = totalTasks,
                    pendingTasks = pendingTasks,
                    inProgressTasks = inProgressTasks,
                    completedTasks = completedTasks,
                    recentActivities = recentActivities
                )
                
                _dashboardState.value = DashboardState.Success(dashboardData)
                
            } catch (e: Exception) {
                _dashboardState.value = DashboardState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}

sealed class DashboardState {
    object Loading : DashboardState()
    data class Success(val data: DashboardData) : DashboardState()
    data class Error(val message: String) : DashboardState()
}

data class DashboardData(
    val totalTasks: Int,
    val pendingTasks: Int,
    val inProgressTasks: Int,
    val completedTasks: Int,
    val recentActivities: List<RecentActivity>
)
