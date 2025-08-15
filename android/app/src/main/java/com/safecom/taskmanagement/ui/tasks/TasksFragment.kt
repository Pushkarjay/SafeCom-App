package com.safecom.taskmanagement.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.safecom.taskmanagement.R
import com.safecom.taskmanagement.domain.model.TaskStatus
import com.safecom.taskmanagement.ui.tasks.adapter.TasksAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TasksFragment : Fragment() {
    
    private val tasksViewModel: TasksViewModel by viewModels()
    private lateinit var tasksAdapter: TasksAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupRecyclerView()
        setupFilters()
        setupObservers()
        
        // Load tasks
        tasksViewModel.loadTasks()
    }
    
    private fun setupUI() {
        view?.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabCreateTask)?.setOnClickListener {
            // Navigation will be implemented later
        }
        
        view?.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSearch)?.setOnClickListener {
            val searchEditText = view?.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etSearch)
            val query = searchEditText?.text.toString() ?: ""
            tasksViewModel.searchTasks(query)
        }
    }
    
    private fun setupRecyclerView() {
        tasksAdapter = TasksAdapter(
            onTaskClick = { task ->
                // Navigation will be implemented later
            },
            onTaskStatusChanged = { task, newStatus ->
                tasksViewModel.updateTaskStatus(task.id, newStatus)
            }
        )
        
        view?.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvTasks)?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tasksAdapter
        }
    }
    
    private fun setupFilters() {
        // Status filter chips - simplified implementation
        view?.findViewById<com.google.android.material.chip.ChipGroup>(R.id.chipGroupFilters)?.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedStatuses = checkedIds.mapNotNull { chipId ->
                when (chipId) {
                    R.id.chipAll -> null
                    R.id.chipPending -> TaskStatus.PENDING
                    R.id.chipInProgress -> TaskStatus.IN_PROGRESS
                    R.id.chipCompleted -> TaskStatus.COMPLETED
                    R.id.chipOverdue -> TaskStatus.OVERDUE
                    else -> null
                }
            }
            tasksViewModel.filterByStatus(selectedStatuses)
        }
    }
    
    private fun setupObservers() {
        lifecycleScope.launch {
            tasksViewModel.tasksState.collect { state ->
                when (state) {
                    is TasksState.Loading -> {
                        view?.findViewById<android.widget.ProgressBar>(R.id.progressBar)?.visibility = View.VISIBLE
                        view?.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvTasks)?.visibility = View.GONE
                        view?.findViewById<android.widget.TextView>(R.id.tvEmptyState)?.visibility = View.GONE
                    }
                    is TasksState.Success -> {
                        view?.findViewById<android.widget.ProgressBar>(R.id.progressBar)?.visibility = View.GONE
                        if (state.tasks.isEmpty()) {
                            view?.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvTasks)?.visibility = View.GONE
                            view?.findViewById<android.widget.TextView>(R.id.tvEmptyState)?.visibility = View.VISIBLE
                        } else {
                            view?.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvTasks)?.visibility = View.VISIBLE
                            view?.findViewById<android.widget.TextView>(R.id.tvEmptyState)?.visibility = View.GONE
                            tasksAdapter.submitList(state.tasks)
                        }
                    }
                    is TasksState.Error -> {
                        view?.findViewById<android.widget.ProgressBar>(R.id.progressBar)?.visibility = View.GONE
                        view?.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvTasks)?.visibility = View.GONE
                        view?.findViewById<android.widget.TextView>(R.id.tvEmptyState)?.let { textView ->
                            textView.visibility = View.VISIBLE
                            textView.text = state.message
                        }
                    }
                }
            }
        }
        
        lifecycleScope.launch {
            tasksViewModel.selectedFilters.collect { filters ->
                updateFilterUI(filters)
            }
        }
    }
    
    private fun updateFilterUI(filters: TaskFilters) {
        // Update status chips - simplified for now
        if (filters.statuses.isEmpty()) {
            view?.findViewById<com.google.android.material.chip.Chip>(R.id.chipAll)?.isChecked = true
        } else {
            filters.statuses.forEach { status ->
                when (status) {
                    TaskStatus.PENDING -> view?.findViewById<com.google.android.material.chip.Chip>(R.id.chipPending)?.isChecked = true
                    TaskStatus.IN_PROGRESS -> view?.findViewById<com.google.android.material.chip.Chip>(R.id.chipInProgress)?.isChecked = true
                    TaskStatus.COMPLETED -> view?.findViewById<com.google.android.material.chip.Chip>(R.id.chipCompleted)?.isChecked = true
                    TaskStatus.OVERDUE -> view?.findViewById<com.google.android.material.chip.Chip>(R.id.chipOverdue)?.isChecked = true
                    TaskStatus.CANCELLED -> { /* No chip for cancelled */ }
                }
            }
        }
    }
    
    private fun showSortOptionsDialog() {
        val sortOptions = arrayOf(
            "Date Created (Newest)",
            "Date Created (Oldest)",
            "Due Date (Earliest)",
            "Due Date (Latest)",
            "Priority (High to Low)",
            "Priority (Low to High)",
            "Title (A-Z)",
            "Title (Z-A)"
        )
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Sort Tasks")
            .setItems(sortOptions) { _, which ->
                val sortOption = when (which) {
                    0 -> SortOption.DATE_CREATED_DESC
                    1 -> SortOption.DATE_CREATED_ASC
                    2 -> SortOption.DUE_DATE_ASC
                    3 -> SortOption.DUE_DATE_DESC
                    4 -> SortOption.PRIORITY_DESC
                    5 -> SortOption.PRIORITY_ASC
                    6 -> SortOption.TITLE_ASC
                    7 -> SortOption.TITLE_DESC
                    else -> SortOption.DATE_CREATED_DESC
                }
                tasksViewModel.sortTasks(sortOption)
            }
            .show()
    }
}
