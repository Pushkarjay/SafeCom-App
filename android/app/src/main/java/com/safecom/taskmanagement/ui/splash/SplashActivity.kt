package com.safecom.taskmanagement.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.safecom.taskmanagement.ui.main.MainActivity
import com.safecom.taskmanagement.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContentView(R.layout.activity_splash)
            
            // Navigate to main activity after 2 seconds
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } catch (e: Exception) {
                    // If MainActivity fails to start, finish this activity
                    e.printStackTrace()
                    finish()
                }
            }, 2000)
        } catch (e: Exception) {
            // If splash setup fails, go directly to main activity
            e.printStackTrace()
            try {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } catch (ex: Exception) {
                // If everything fails, just finish
                ex.printStackTrace()
                finish()
            }
        }
    }
}
