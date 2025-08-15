package com.safecom.taskmanagement.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.TextView
import android.widget.Button
import android.view.Gravity
import android.graphics.Color
import com.safecom.taskmanagement.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            // Create a simple layout programmatically for now
            createSimpleLayout()
        } catch (e: Exception) {
            // If layout creation fails, show a toast and close gracefully
            e.printStackTrace()
            Toast.makeText(this, "App initialization error. Please check if backend is running.", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    private fun createSimpleLayout() {
        // Create root layout
        val rootLayout = ConstraintLayout(this).apply {
            setBackgroundColor(Color.parseColor("#F5F5F5"))
        }
        
        // Create title text
        val titleText = TextView(this).apply {
            text = "SafeCom Task Management"
            textSize = 24f
            setTextColor(Color.parseColor("#2196F3"))
            gravity = Gravity.CENTER
            id = 1000
        }
        
        // Create status text
        val statusText = TextView(this).apply {
            text = "App loaded successfully!\n\nOffline Mode Active\n\nTo enable full functionality:\n1. Start the backend server\n2. Install the latest APK\n3. Connect to the internet"
            textSize = 16f
            setTextColor(Color.parseColor("#666666"))
            gravity = Gravity.CENTER
            id = 1001
        }
        
        // Create refresh button
        val refreshButton = Button(this).apply {
            text = "Test Button"
            setBackgroundColor(Color.parseColor("#2196F3"))
            setTextColor(Color.WHITE)
            id = 1002
            setOnClickListener {
                Toast.makeText(this@MainActivity, "SafeCom app is working! 🎉", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Add views to layout
        rootLayout.addView(titleText)
        rootLayout.addView(statusText)
        rootLayout.addView(refreshButton)
        
        // Set layout parameters
        val titleParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            topMargin = 200
        }
        titleText.layoutParams = titleParams
        
        val statusParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topToBottom = titleText.id
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            topMargin = 100
            marginStart = 40
            marginEnd = 40
        }
        statusText.layoutParams = statusParams
        
        val buttonParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topToBottom = statusText.id
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            topMargin = 80
        }
        refreshButton.layoutParams = buttonParams
        
        setContentView(rootLayout)
        
        // Show success message
        Toast.makeText(this, "SafeCom App Started Successfully! 🚀", Toast.LENGTH_LONG).show()
    }
}
