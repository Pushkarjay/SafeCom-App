package com.safecom.taskmanagement.data.remote.dto

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ChangePasswordRequestDto(
    val currentPassword: String,
    val newPassword: String,
    val confirmPassword: String
) : Parcelable
