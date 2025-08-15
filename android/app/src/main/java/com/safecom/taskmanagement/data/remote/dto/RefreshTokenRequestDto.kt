package com.safecom.taskmanagement.data.remote.dto

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class RefreshTokenRequestDto(
    val refreshToken: String
) : Parcelable
