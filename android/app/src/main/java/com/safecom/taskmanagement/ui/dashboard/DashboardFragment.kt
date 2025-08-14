package com.safecom.taskmanagement.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.safecom.taskmanagement.databinding.FragmentDashboardBinding
import com.safecom.taskmanagement.ui.dashboard.adapter.RecentActivitiesAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DashboardFragment : Fragment() {
    
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private lateinit var recentActivitiesAdapter: RecentActivitiesAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupRecyclerView()
        setupObservers()
        
        // Load dashboard data
        dashboardViewModel.loadDashboardData()
    }
    
    private fun setupUI() {
        // Set current date
        val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
        binding.tvCurrentDate.text = dateFormat.format(Date())
        
        // Set greeting based on time
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        
        binding.tvGreeting.text = when (hour) {
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..20 -> "Good Evening"
            else -> "Good Night"
        }
        
        // Set click listeners
        binding.btnCreateTask.setOnClickListener {
            findNavController().navigate(
                DashboardFragmentDirections.actionDashboardToCreateTask()
            )
        }
        
        binding.btnViewTasks.setOnClickListener {
            findNavController().navigate(
                DashboardFragmentDirections.actionDashboardToTasks()
            )
        }
        
        binding.tvViewAllActivities.setOnClickListener {
            // TODO: Navigate to activities screen
        }
    }
    
    private fun setupRecyclerView() {
        recentActivitiesAdapter = RecentActivitiesAdapter { activity ->
            // Handle activity item click
            if (activity.taskId != null) {
                findNavController().navigate(
                    DashboardFragmentDirections.actionDashboardToTaskDetail(activity.taskId)
                )
            }
        }
        
        binding.rvRecentActivities.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recentActivitiesAdapter
        }
    }
    
    private fun setupObservers() {
        lifecycleScope.launch {
            dashboardViewModel.dashboardState.collect { state ->
                when (state) {
                    is DashboardState.Loading -> {
                        // Show loading state
                    }
                    is DashboardState.Success -> {
                        updateUI(state.data)
                    }
                    is DashboardState.Error -> {
                        // Show error state
                    }
                }
            }
        }
        
        lifecycleScope.launch {
            dashboardViewModel.userInfo.collect { user ->
                user?.let {
                    binding.tvUserName.text = it.name
                }
            }
        }
    }
    
    private fun updateUI(data: DashboardData) {
        binding.apply {
            // Update task statistics
            tvTotalTasksCount.text = data.totalTasks.toString()
            tvPendingTasksCount.text = data.pendingTasks.toString()
            tvInProgressTasksCount.text = data.inProgressTasks.toString()
            tvCompletedTasksCount.text = data.completedTasks.toString()
            
            // Update recent activities
            recentActivitiesAdapter.submitList(data.recentActivities)
            
            // TODO: Setup productivity chart
            // chartProductivity.data = data.chartData
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
