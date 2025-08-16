package com.safecom.taskmanagement.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.safecom.taskmanagement.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private val dashboardViewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // For now, let's create a simple dashboard view
        setupDashboard(view)
        observeViewModel()
    }
    
    private fun setupDashboard(view: View) {
        // The dashboard will be set up based on the actual layout
        // For now, just initialize the dashboard
        // TODO: Implement dashboard functionality
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Observe any dashboard data from ViewModel
            // This will be expanded when we have actual dashboard functionality
        }
    }
}
