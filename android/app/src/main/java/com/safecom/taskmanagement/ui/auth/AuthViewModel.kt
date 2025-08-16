package com.safecom.taskmanagement.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safecom.taskmanagement.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginResult = MutableStateFlow<AuthResult?>(null)
    val loginResult: StateFlow<AuthResult?> = _loginResult.asStateFlow()

    fun login(email: String, password: String, role: String = "employee") {
        viewModelScope.launch {
            try {
                _loginResult.value = AuthResult.Loading
                
                // For now, let's implement a simple validation with role support
                // In a real app, this would call the API with role validation
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    // Simulate API call delay
                    kotlinx.coroutines.delay(1500)
                    
                    // For demo purposes, accept any valid email/password with the selected role
                    // Validate role-specific credentials if needed
                    when (role) {
                        "admin" -> {
                            if (email.contains("admin") || password == "admin123") {
                                _loginResult.value = AuthResult.Success("Administrator")
                            } else {
                                _loginResult.value = AuthResult.Error("Invalid admin credentials")
                            }
                        }
                        "manager" -> {
                            if (email.contains("manager") || password == "manager123") {
                                _loginResult.value = AuthResult.Success("Manager")
                            } else {
                                _loginResult.value = AuthResult.Error("Invalid manager credentials")
                            }
                        }
                        "employee" -> {
                            if (email.isNotEmpty() && password.isNotEmpty()) {
                                _loginResult.value = AuthResult.Success("Employee")
                            } else {
                                _loginResult.value = AuthResult.Error("Invalid employee credentials")
                            }
                        }
                        else -> {
                            _loginResult.value = AuthResult.Error("Invalid role selected")
                        }
                    }
                    
                    // TODO: Replace with actual API call
                    // val result = authRepository.login(email, password, role)
                    
                } else {
                    _loginResult.value = AuthResult.Error("Please enter valid credentials")
                }
            } catch (e: Exception) {
                _loginResult.value = AuthResult.Error(e.message ?: "Login failed")
            }
        }
    }
    
    fun clearResult() {
        _loginResult.value = null
    }
}
