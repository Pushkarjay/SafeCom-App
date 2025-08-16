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
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences
    
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContentView(R.layout.activity_main)
            setupNavigation()
            setupToolbar()
            setupFab()
        } catch (e: Exception) {
            // If there's an error setting up the main UI, create a simple fallback
            e.printStackTrace()
            createFallbackUI()
        }
    }
    
    private fun setupNavigation() {
        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
            // Navigation setup failed, we'll handle this in the fallback UI
        }
    }
    
    private fun setupToolbar() {
        try {
            val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
            setSupportActionBar(toolbar)
        } catch (e: Exception) {
            e.printStackTrace()
            // Toolbar setup failed, continue without it
        }
    }
    
    private fun setupFab() {
        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
            // FAB setup failed, continue without it
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
        return try {
            navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
        } catch (e: Exception) {
            e.printStackTrace()
            super.onSupportNavigateUp()
        }
    }
    
    private fun createFallbackUI() {
        try {
            // Create a simple layout programmatically if the main layout fails
            val textView = android.widget.TextView(this)
            textView.text = "✅ SafeCom Test 1 - Admin Dashboard\n\n🔐 Welcome Administrator!\n\nFeatures:\n• Task Management\n• Team Communication\n• System Administration\n\n🚀 Full features coming soon..."
            textView.textSize = 18f
            textView.setPadding(60, 80, 60, 40)
            textView.gravity = android.view.Gravity.CENTER
            
            // Create logout button
            val button = android.widget.Button(this)
            button.text = "Logout"
            button.textSize = 16f
            button.setPadding(40, 20, 40, 20)
            button.setOnClickListener {
                logout()
            }
            
            // Create linear layout
            val layout = android.widget.LinearLayout(this)
            layout.orientation = android.widget.LinearLayout.VERTICAL
            layout.gravity = android.view.Gravity.CENTER
            layout.setPadding(40, 100, 40, 100)
            layout.addView(textView)
            layout.addView(button)
            
            setContentView(layout)
            
            // Set title
            title = "SafeCom Test 1"
        } catch (e: Exception) {
            e.printStackTrace()
            // Ultimate fallback - just show a basic activity
            finish()
        }
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
