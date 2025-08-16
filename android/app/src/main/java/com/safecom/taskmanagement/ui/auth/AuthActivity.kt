package com.safecom.taskmanagement.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.radiobutton.MaterialRadioButton
import android.widget.TextView
import android.widget.RadioGroup
import com.safecom.taskmanagement.R
import com.safecom.taskmanagement.ui.main.MainActivity
import com.safecom.taskmanagement.data.local.preferences.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    
    @Inject
    lateinit var userPreferences: UserPreferences
    
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnBiometricLogin: MaterialButton
    private lateinit var tvForgotPassword: TextView
    private lateinit var tvError: TextView
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var rgUserRole: RadioGroup
    private lateinit var rbEmployee: MaterialRadioButton
    private lateinit var rbManager: MaterialRadioButton
    private lateinit var rbAdmin: MaterialRadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        
        initViews()
        setupClickListeners()
        observeViewModel()
    }
    
    private fun initViews() {
        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnBiometricLogin = findViewById(R.id.btnBiometricLogin)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        tvError = findViewById(R.id.tvError)
        progressBar = findViewById(R.id.progressBar)
        
        // Role selection - we'll add these to the layout
        try {
            rgUserRole = findViewById(R.id.radioGroupRole)
            rbEmployee = findViewById(R.id.radioEmployee)
            rbManager = findViewById(R.id.radioManager)
            rbAdmin = findViewById(R.id.radioAdmin)
        } catch (e: Exception) {
            // If role selection views don't exist, we'll use a simple approach
            e.printStackTrace()
        }
    }
    
    private fun setupClickListeners() {
        btnLogin.setOnClickListener {
            performLogin()
        }
        
        btnBiometricLogin.setOnClickListener {
            // For now, just show a message that biometric is not implemented yet
            Toast.makeText(this, "Biometric login will be available in future updates", Toast.LENGTH_SHORT).show()
        }
        
        tvForgotPassword.setOnClickListener {
            // For now, just show a message
            Toast.makeText(this, "Forgot password feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun performLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        
        // Get selected role
        val selectedRole = getSelectedRole()
        
        // Clear previous errors
        tilEmail.error = null
        tilPassword.error = null
        tvError.visibility = View.GONE
        
        // Basic validation
        var hasError = false
        
        if (email.isEmpty()) {
            tilEmail.error = "Email is required"
            hasError = true
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Please enter a valid email address"
            hasError = true
        }
        
        if (password.isEmpty()) {
            tilPassword.error = "Password is required"
            hasError = true
        } else if (password.length < 6) {
            tilPassword.error = "Password must be at least 6 characters"
            hasError = true
        }
        
        if (selectedRole.isEmpty()) {
            showError("Please select your role (Employee, Manager, or Admin)")
            hasError = true
        }
        
        if (hasError) return
        
        // Show progress
        showLoading(true)
        
        // Perform login with role
        lifecycleScope.launch {
            authViewModel.login(email, password, selectedRole)
        }
    }
    
    private fun getSelectedRole(): String {
        return try {
            when (rgUserRole.checkedRadioButtonId) {
                R.id.radioEmployee -> "employee"
                R.id.radioManager -> "manager"
                R.id.radioAdmin -> "admin"
                else -> "employee"
            }
        } catch (e: Exception) {
            // If radio group doesn't exist, default to employee for now
            "employee"
        }
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            authViewModel.loginResult.collect { result ->
                showLoading(false)
                
                when (result) {
                    is AuthResult.Success -> {
                        // Save authentication data including role
                        val selectedRole = getSelectedRole()
                        lifecycleScope.launch {
                            userPreferences.setAuthToken("demo_token_${System.currentTimeMillis()}")
                            userPreferences.setCurrentUserId("user_${System.currentTimeMillis()}")
                            userPreferences.setUserRole(selectedRole)
                            
                            Toast.makeText(this@AuthActivity, "Login successful! Welcome ${result.role}", Toast.LENGTH_SHORT).show()
                            navigateToMain()
                        }
                    }
                    is AuthResult.Error -> {
                        showError(result.message)
                    }
                    is AuthResult.Loading -> {
                        showLoading(true)
                    }
                    null -> {
                        // Initial state, do nothing
                    }
                }
            }
        }
    }
    
    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !isLoading
        btnBiometricLogin.isEnabled = !isLoading
    }
    
    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

sealed class AuthResult {
    object Loading : AuthResult()
    data class Success(val role: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}
