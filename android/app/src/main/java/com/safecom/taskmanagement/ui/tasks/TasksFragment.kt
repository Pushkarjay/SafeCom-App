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
import com.safecom.taskmanagement.databinding.FragmentTasksBinding
import com.safecom.taskmanagement.domain.model.TaskStatus
import com.safecom.taskmanagement.ui.tasks.adapter.TasksAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TasksFragment : Fragment() {
    
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    
    private val tasksViewModel: TasksViewModel by viewModels()
    private lateinit var tasksAdapter: TasksAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
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
        binding.fabCreateTask.setOnClickListener {
            findNavController().navigate(
                TasksFragmentDirections.actionTasksToCreateTask()
            )
        }
        
        binding.btnSearch.setOnClickListener {
            val query = binding.etSearch.text.toString()
            tasksViewModel.searchTasks(query)
        }
        
        binding.etSearch.setOnEditorActionListener { _, _, _ ->
            val query = binding.etSearch.text.toString()
            tasksViewModel.searchTasks(query)
            true
        }
    }
    
    private fun setupRecyclerView() {
        tasksAdapter = TasksAdapter(
            onTaskClick = { task ->
                findNavController().navigate(
                    TasksFragmentDirections.actionTasksToTaskDetail(task.id)
                )
            },
            onTaskStatusChanged = { task, newStatus ->
                tasksViewModel.updateTaskStatus(task.id, newStatus)
            }
        )
        
        binding.rvTasks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tasksAdapter
        }
    }
    
    private fun setupFilters() {
        // Status filter chips
        binding.chipGroupStatus.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedStatuses = checkedIds.mapNotNull { chipId ->
                when (chipId) {
                    R.id.chip_all -> null
                    R.id.chip_pending -> TaskStatus.PENDING
                    R.id.chip_in_progress -> TaskStatus.IN_PROGRESS
                    R.id.chip_completed -> TaskStatus.COMPLETED
                    R.id.chip_overdue -> TaskStatus.OVERDUE
                    else -> null
                }
            }
            tasksViewModel.filterByStatus(selectedStatuses)
        }
        
        // Priority filter chips
        binding.chipGroupPriority.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedPriorities = checkedIds.mapNotNull { chipId ->
                val chip = group.findViewById<Chip>(chipId)
                chip?.text?.toString()
            }
            tasksViewModel.filterByPriority(selectedPriorities)
        }
        
        // Sort options
        binding.btnSort.setOnClickListener {
            showSortOptionsDialog()
        }
    }
    
    private fun setupObservers() {
        lifecycleScope.launch {
            tasksViewModel.tasksState.collect { state ->
                when (state) {
                    is TasksState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.rvTasks.visibility = View.GONE
                        binding.tvEmptyState.visibility = View.GONE
                    }
                    is TasksState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        if (state.tasks.isEmpty()) {
                            binding.rvTasks.visibility = View.GONE
                            binding.tvEmptyState.visibility = View.VISIBLE
                        } else {
                            binding.rvTasks.visibility = View.VISIBLE
                            binding.tvEmptyState.visibility = View.GONE
                            tasksAdapter.submitList(state.tasks)
                        }
                    }
                    is TasksState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.rvTasks.visibility = View.GONE
                        binding.tvEmptyState.visibility = View.VISIBLE
                        binding.tvEmptyState.text = state.message
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
        // Update status chips
        binding.chipGroupStatus.clearCheck()
        if (filters.statuses.isEmpty()) {
            binding.chipAll.isChecked = true
        } else {
            filters.statuses.forEach { status ->
                when (status) {
                    TaskStatus.PENDING -> binding.chipPending.isChecked = true
                    TaskStatus.IN_PROGRESS -> binding.chipInProgress.isChecked = true
                    TaskStatus.COMPLETED -> binding.chipCompleted.isChecked = true
                    TaskStatus.OVERDUE -> binding.chipOverdue.isChecked = true
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
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
