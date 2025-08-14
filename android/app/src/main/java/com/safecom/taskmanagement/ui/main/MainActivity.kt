package com.safecom.taskmanagement.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.safecom.taskmanagement.R
import com.safecom.taskmanagement.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupNavigation()
        setupUI()
    }
    
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        
        // Setup ActionBar with NavController
        setSupportActionBar(binding.toolbar)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.dashboardFragment,
                R.id.tasksFragment,
                R.id.messagesFragment,
                R.id.profileFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        
        // Setup BottomNavigationView with NavController
        binding.bottomNavigation.setupWithNavController(navController)
        
        // Handle destination changes
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.dashboardFragment -> binding.toolbar.title = getString(R.string.nav_dashboard)
                R.id.tasksFragment -> binding.toolbar.title = getString(R.string.nav_tasks)
                R.id.messagesFragment -> binding.toolbar.title = getString(R.string.nav_messages)
                R.id.profileFragment -> binding.toolbar.title = getString(R.string.nav_profile)
            }
        }
    }
    
    private fun setupUI() {
        binding.fabCreateTask.setOnClickListener {
            // Navigate to create task
            navController.navigate(R.id.createTaskFragment)
        }
        
        // Handle toolbar menu items
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_search -> {
                    // TODO: Implement search functionality
                    true
                }
                R.id.action_notifications -> {
                    // Navigate to notifications
                    navController.navigate(R.id.notificationsFragment)
                    true
                }
                R.id.action_settings -> {
                    // Navigate to settings
                    navController.navigate(R.id.settingsFragment)
                    true
                }
                else -> false
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
