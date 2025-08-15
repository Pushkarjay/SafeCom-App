package com.safecom.taskmanagement.data.remote.dto

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class RegisterRequestDto(
    val fullName: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val role: String = "EMPLOYEE"
) : Parcelable
