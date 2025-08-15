package com.safecom.taskmanagement.data.remote.dto

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class UploadImageDto(
    val imageBase64: String,
    val fileName: String,
    val contentType: String = "image/*"
) : Parcelable
