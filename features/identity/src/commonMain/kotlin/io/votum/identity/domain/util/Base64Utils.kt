/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */
package io.votum.identity.domain.util

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
object Base64Utils {

    fun encodeToBase64(byteArray: ByteArray, mimeType: String = "image/jpeg"): String {
        val base64Content = Base64.encode(byteArray)
        return "data:$mimeType;base64,$base64Content"
    }

    fun decodeFromBase64(base64String: String): ByteArray {
        val cleanBase64 = base64String.substringAfter("base64,", base64String)
        return Base64.decode(cleanBase64)
    }

    fun calculateBase64Size(base64String: String): Long {
        val cleanBase64 = base64String.substringAfter("base64,", base64String)
        val padding = cleanBase64.count { it == '=' }
        return ((cleanBase64.length * 3L) / 4L) - padding
    }

    fun validateBase64Format(base64String: String): Boolean {
        if (!base64String.startsWith("data:")) {
            return false
        }

        if (!base64String.contains(";base64,")) {
            return false
        }

        val base64Content = base64String.substringAfter("base64,")
        return base64Content.matches(Regex("^[A-Za-z0-9+/=]+$"))
    }

    fun validateFileExtension(fileName: String): Boolean {
        val allowedExtensions = listOf("jpg", "jpeg", "png", "pdf")
        val extension = fileName.substringAfterLast(".", "").lowercase()
        return extension in allowedExtensions
    }

    fun validateMimeType(mimeType: String): Boolean {
        val validMimeTypes = listOf(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "application/pdf"
        )
        return mimeType in validMimeTypes
    }
}
