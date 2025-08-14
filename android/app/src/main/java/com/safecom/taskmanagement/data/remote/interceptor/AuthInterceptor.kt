package com.safecom.taskmanagement.data.remote.interceptor

import com.safecom.taskmanagement.data.local.preferences.UserPreferences
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val userPreferences: UserPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip auth for login/register endpoints
        val url = originalRequest.url.toString()
        if (url.contains("/auth/login") || 
            url.contains("/auth/register") || 
            url.contains("/auth/refresh")) {
            return chain.proceed(originalRequest)
        }

        // Add auth token to request
        val token = runBlocking { userPreferences.getAuthToken() }
        
        val newRequest = if (token != null) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json")
                .build()
        } else {
            originalRequest.newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()
        }

        val response = chain.proceed(newRequest)

        // Handle token refresh if needed
        if (response.code == 401 && token != null) {
            response.close()
            
            // Try to refresh token
            val refreshToken = runBlocking { userPreferences.getRefreshToken() }
            if (refreshToken != null) {
                // TODO: Implement token refresh logic
                // For now, just proceed with original request
                return chain.proceed(originalRequest)
            }
        }

        return response
    }
}
