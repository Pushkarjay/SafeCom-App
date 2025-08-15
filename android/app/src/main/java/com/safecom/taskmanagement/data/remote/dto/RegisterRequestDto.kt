package com.safecom.taskmanagement.data.remote.dto

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class RegisterRequestDto(
    val name: String,
    val email: String,
    val password: String,
    val role: String = "EMPLOYEE"
) : Parcelable
