package com.safecom.taskmanagement.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.safecom.taskmanagement.databinding.ActivityAuthBinding
import com.safecom.taskmanagement.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAuthBinding
    private val authViewModel: AuthViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        setupObservers()
        checkBiometricAvailability()
    }
    
    private fun setupUI() {
        binding.apply {
            btnLogin.setOnClickListener {
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()
                
                if (validateInput(email, password)) {
                    authViewModel.login(email, password)
                }
            }
            
            btnBiometricLogin.setOnClickListener {
                showBiometricPrompt()
            }
            
            tvForgotPassword.setOnClickListener {
                // TODO: Implement forgot password functionality
                Toast.makeText(this@AuthActivity, "Forgot password clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun setupObservers() {
        lifecycleScope.launch {
            authViewModel.authState.collect { state ->
                when (state) {
                    is AuthState.Loading -> {
                        binding.progressBar.isVisible = true
                        binding.btnLogin.isEnabled = false
                        binding.tvError.isVisible = false
                    }
                    is AuthState.Success -> {
                        binding.progressBar.isVisible = false
                        navigateToMain()
                    }
                    is AuthState.Error -> {
                        binding.progressBar.isVisible = false
                        binding.btnLogin.isEnabled = true
                        binding.tvError.apply {
                            text = state.message
                            isVisible = true
                        }
                    }
                    is AuthState.Idle -> {
                        binding.progressBar.isVisible = false
                        binding.btnLogin.isEnabled = true
                        binding.tvError.isVisible = false
                    }
                }
            }
        }
    }
    
    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true
        
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Invalid email format"
            isValid = false
        } else {
            binding.tilEmail.error = null
        }
        
        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }
        
        return isValid
    }
    
    private fun checkBiometricAvailability() {
        val biometricManager = BiometricManager.from(this)
        
        binding.btnBiometricLogin.isVisible = when (biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }
    
    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(this@AuthActivity, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
            }
            
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                authViewModel.biometricLogin()
            }
            
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(this@AuthActivity, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        })
        
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Use your fingerprint or face to sign in")
            .setNegativeButtonText("Cancel")
            .build()
        
        biometricPrompt.authenticate(promptInfo)
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
