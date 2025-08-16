package com.safecom.taskmanagement.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.safecom.taskmanagement.ui.main.MainActivity
import com.safecom.taskmanagement.ui.auth.AuthActivity
import com.safecom.taskmanagement.data.local.preferences.UserPreferences
import com.safecom.taskmanagement.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContentView(R.layout.activity_splash)
            
            // Check authentication status and navigate accordingly
            Handler(Looper.getMainLooper()).postDelayed({
                checkAuthenticationAndNavigate()
            }, 2000)
        } catch (e: Exception) {
            // If splash setup fails, go directly to auth activity
            e.printStackTrace()
            navigateToAuth()
        }
    }
    
    private fun checkAuthenticationAndNavigate() {
        lifecycleScope.launch {
            try {
                val authToken = userPreferences.getAuthToken()
                val userId = userPreferences.getCurrentUserId()
                
                if (!authToken.isNullOrEmpty() && !userId.isNullOrEmpty()) {
                    // User is logged in, go to main activity
                    navigateToMain()
                } else {
                    // User is not logged in, go to auth activity
                    navigateToAuth()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // If anything fails, go to auth
                navigateToAuth()
            }
        }
    }
    
    private fun navigateToMain() {
        try {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
            navigateToAuth()
        }
    }
    
    private fun navigateToAuth() {
        try {
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }
    }
}
