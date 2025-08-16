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

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _loginResult.value = AuthResult.Loading
                
                // For now, let's implement a simple validation
                // In a real app, this would call the API
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    // Simulate API call delay
                    kotlinx.coroutines.delay(1500)
                    
                    // For demo purposes, accept any valid email/password
                    // TODO: Replace with actual API call
                    // val result = authRepository.login(email, password)
                    
                    _loginResult.value = AuthResult.Success
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
