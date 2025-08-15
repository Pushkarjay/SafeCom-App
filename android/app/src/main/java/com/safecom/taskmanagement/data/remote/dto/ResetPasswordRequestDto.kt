package com.safecom.taskmanagement.data.remote.dto

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ResetPasswordRequestDto(
    val token: String,
    val newPassword: String,
    val confirmPassword: String
) : Parcelable
