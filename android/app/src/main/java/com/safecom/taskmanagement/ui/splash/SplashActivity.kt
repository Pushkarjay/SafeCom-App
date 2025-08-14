package com.safecom.taskmanagement.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.safecom.taskmanagement.databinding.ActivitySplashBinding
import com.safecom.taskmanagement.ui.auth.AuthActivity
import com.safecom.taskmanagement.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySplashBinding
    private val splashTimeOut: Long = 3000 // 3 seconds
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Set app version
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            binding.tvVersion.text = "Version ${packageInfo.versionName}"
        } catch (e: Exception) {
            binding.tvVersion.text = "Version 1.0"
        }
        
        // Navigate to next screen after delay
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, splashTimeOut)
    }
    
    private fun navigateToNextScreen() {
        // Check if user is logged in
        val isLoggedIn = checkLoginStatus()
        
        val intent = if (isLoggedIn) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, AuthActivity::class.java)
        }
        
        startActivity(intent)
        finish()
        
        // Add transition animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
    
    private fun checkLoginStatus(): Boolean {
        // TODO: Implement actual login check using SharedPreferences or Repository
        val sharedPref = getSharedPreferences("SafeComPrefs", MODE_PRIVATE)
        return sharedPref.getBoolean("is_logged_in", false)
    }
}
