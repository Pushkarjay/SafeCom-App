package com.safecom.taskmanagement.data.remote.dto

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ForgotPasswordRequestDto(
    val email: String
) : Parcelable
