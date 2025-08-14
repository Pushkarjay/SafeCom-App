package com.safecom.taskmanagement.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val encryptedPreferences: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            "safecom_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    
    private val regularPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("safecom_prefs", Context.MODE_PRIVATE)
    }
    
    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_CURRENT_USER_ID = "current_user_id"
        private const val KEY_DARK_MODE_ENABLED = "dark_mode_enabled"
        private const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_FIRST_TIME_USER = "first_time_user"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }
    
    // Secure preferences (encrypted)
    suspend fun setAuthToken(token: String) {
        encryptedPreferences.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }
    
    suspend fun getAuthToken(): String? {
        return encryptedPreferences.getString(KEY_AUTH_TOKEN, null)
    }
    
    suspend fun setRefreshToken(token: String) {
        encryptedPreferences.edit().putString(KEY_REFRESH_TOKEN, token).apply()
    }
    
    suspend fun getRefreshToken(): String? {
        return encryptedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }
    
    suspend fun setCurrentUserId(userId: String) {
        encryptedPreferences.edit().putString(KEY_CURRENT_USER_ID, userId).apply()
    }
    
    suspend fun getCurrentUserId(): String? {
        return encryptedPreferences.getString(KEY_CURRENT_USER_ID, null)
    }
    
    // Regular preferences
    suspend fun setDarkModeEnabled(enabled: Boolean) {
        regularPreferences.edit().putBoolean(KEY_DARK_MODE_ENABLED, enabled).apply()
    }
    
    suspend fun isDarkModeEnabled(): Boolean {
        return regularPreferences.getBoolean(KEY_DARK_MODE_ENABLED, false)
    }
    
    suspend fun setNotificationEnabled(enabled: Boolean) {
        regularPreferences.edit().putBoolean(KEY_NOTIFICATION_ENABLED, enabled).apply()
    }
    
    suspend fun isNotificationEnabled(): Boolean {
        return regularPreferences.getBoolean(KEY_NOTIFICATION_ENABLED, true)
    }
    
    suspend fun setBiometricEnabled(enabled: Boolean) {
        regularPreferences.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }
    
    suspend fun isBiometricEnabled(): Boolean {
        return regularPreferences.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }
    
    suspend fun setLanguage(language: String) {
        regularPreferences.edit().putString(KEY_LANGUAGE, language).apply()
    }
    
    suspend fun getLanguage(): String {
        return regularPreferences.getString(KEY_LANGUAGE, "en") ?: "en"
    }
    
    suspend fun setFirstTimeUser(isFirstTime: Boolean) {
        regularPreferences.edit().putBoolean(KEY_FIRST_TIME_USER, isFirstTime).apply()
    }
    
    suspend fun isFirstTimeUser(): Boolean {
        return regularPreferences.getBoolean(KEY_FIRST_TIME_USER, true)
    }
    
    suspend fun setOnboardingCompleted(completed: Boolean) {
        regularPreferences.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }
    
    suspend fun isOnboardingCompleted(): Boolean {
        return regularPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }
    
    suspend fun clearAll() {
        encryptedPreferences.edit().clear().apply()
        regularPreferences.edit().clear().apply()
    }
    
    suspend fun clearAuthData() {
        encryptedPreferences.edit()
            .remove(KEY_AUTH_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_CURRENT_USER_ID)
            .apply()
    }
}
