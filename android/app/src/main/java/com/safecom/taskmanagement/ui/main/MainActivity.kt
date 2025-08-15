package com.safecom.taskmanagement.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.safecom.taskmanagement.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContentView(R.layout.activity_main)
        } catch (e: Exception) {
            // Handle layout inflation error
            e.printStackTrace()
            // You could show a simple error message or basic layout here
            finish()
        }
    }
}
