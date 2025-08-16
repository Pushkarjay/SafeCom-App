package com.safecom.taskmanagement.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.safecom.taskmanagement.R
import com.safecom.taskmanagement.ui.auth.AuthActivity
import com.safecom.taskmanagement.data.local.preferences.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences
    
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        setupNavigation()
        setupToolbar()
        setupFab()
    }
    
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavView.setupWithNavController(navController)
        
        // Define top-level destinations
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.dashboardFragment,
                R.id.tasksFragment,
                R.id.messagesFragment,
                R.id.profileFragment
            )
        )
        
        setupActionBarWithNavController(navController, appBarConfiguration)
    }
    
    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }
    
    private fun setupFab() {
        val fab = findViewById<FloatingActionButton>(R.id.fabCreateTask)
        fab.setOnClickListener {
            // Navigate to create task (when implemented)
            try {
                navController.navigate(R.id.tasksFragment)
            } catch (e: Exception) {
                // If navigation fails, ignore for now
                e.printStackTrace()
            }
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            R.id.action_settings -> {
                // Handle settings action
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    
    private fun logout() {
        // Clear user preferences using coroutine
        lifecycleScope.launch {
            userPreferences.clearAuthData()
            
            // Navigate to auth activity
            val intent = Intent(this@MainActivity, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
