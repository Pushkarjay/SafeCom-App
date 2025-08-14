package com.safecom.taskmanagement.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safecom.taskmanagement.data.repository.TaskRepository
import com.safecom.taskmanagement.domain.model.Task
import com.safecom.taskmanagement.domain.model.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _tasksState = MutableStateFlow<TasksState>(TasksState.Loading)
    val tasksState: StateFlow<TasksState> = _tasksState.asStateFlow()

    private val _selectedFilters = MutableStateFlow(TaskFilters())
    val selectedFilters: StateFlow<TaskFilters> = _selectedFilters.asStateFlow()

    private var allTasks = listOf<Task>()
    private var filteredTasks = listOf<Task>()

    fun loadTasks() {
        viewModelScope.launch {
            try {
                _tasksState.value = TasksState.Loading
                
                val tasks = taskRepository.getAllTasks()
                allTasks = tasks
                applyFiltersAndSort()
                
            } catch (e: Exception) {
                _tasksState.value = TasksState.Error(e.message ?: "Failed to load tasks")
            }
        }
    }

    fun searchTasks(query: String) {
        val currentFilters = _selectedFilters.value
        _selectedFilters.value = currentFilters.copy(searchQuery = query)
        applyFiltersAndSort()
    }

    fun filterByStatus(statuses: List<TaskStatus>) {
        val currentFilters = _selectedFilters.value
        _selectedFilters.value = currentFilters.copy(statuses = statuses)
        applyFiltersAndSort()
    }

    fun filterByPriority(priorities: List<String>) {
        val currentFilters = _selectedFilters.value
        _selectedFilters.value = currentFilters.copy(priorities = priorities)
        applyFiltersAndSort()
    }

    fun sortTasks(sortOption: SortOption) {
        val currentFilters = _selectedFilters.value
        _selectedFilters.value = currentFilters.copy(sortOption = sortOption)
        applyFiltersAndSort()
    }

    fun updateTaskStatus(taskId: String, newStatus: TaskStatus) {
        viewModelScope.launch {
            try {
                taskRepository.updateTaskStatus(taskId, newStatus)
                loadTasks() // Reload to get updated data
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun applyFiltersAndSort() {
        val filters = _selectedFilters.value
        
        // Apply filters
        filteredTasks = allTasks.filter { task ->
            // Search query filter
            val matchesSearch = if (filters.searchQuery.isNotEmpty()) {
                task.title.contains(filters.searchQuery, ignoreCase = true) ||
                task.description.contains(filters.searchQuery, ignoreCase = true)
            } else {
                true
            }
            
            // Status filter
            val matchesStatus = if (filters.statuses.isNotEmpty()) {
                filters.statuses.contains(task.status)
            } else {
                true
            }
            
            // Priority filter
            val matchesPriority = if (filters.priorities.isNotEmpty()) {
                filters.priorities.contains(task.priority)
            } else {
                true
            }
            
            matchesSearch && matchesStatus && matchesPriority
        }
        
        // Apply sorting
        filteredTasks = when (filters.sortOption) {
            SortOption.DATE_CREATED_DESC -> filteredTasks.sortedByDescending { it.createdAt }
            SortOption.DATE_CREATED_ASC -> filteredTasks.sortedBy { it.createdAt }
            SortOption.DUE_DATE_ASC -> filteredTasks.sortedBy { it.dueDate }
            SortOption.DUE_DATE_DESC -> filteredTasks.sortedByDescending { it.dueDate }
            SortOption.PRIORITY_DESC -> filteredTasks.sortedWith(
                compareByDescending { 
                    when (it.priority) {
                        "High" -> 3
                        "Medium" -> 2
                        "Low" -> 1
                        else -> 0
                    }
                }
            )
            SortOption.PRIORITY_ASC -> filteredTasks.sortedWith(
                compareBy { 
                    when (it.priority) {
                        "High" -> 3
                        "Medium" -> 2
                        "Low" -> 1
                        else -> 0
                    }
                }
            )
            SortOption.TITLE_ASC -> filteredTasks.sortedBy { it.title }
            SortOption.TITLE_DESC -> filteredTasks.sortedByDescending { it.title }
        }
        
        _tasksState.value = TasksState.Success(filteredTasks)
    }
}

sealed class TasksState {
    object Loading : TasksState()
    data class Success(val tasks: List<Task>) : TasksState()
    data class Error(val message: String) : TasksState()
}

data class TaskFilters(
    val searchQuery: String = "",
    val statuses: List<TaskStatus> = emptyList(),
    val priorities: List<String> = emptyList(),
    val sortOption: SortOption = SortOption.DATE_CREATED_DESC
)

enum class SortOption {
    DATE_CREATED_DESC,
    DATE_CREATED_ASC,
    DUE_DATE_ASC,
    DUE_DATE_DESC,
    PRIORITY_DESC,
    PRIORITY_ASC,
    TITLE_ASC,
    TITLE_DESC
}
