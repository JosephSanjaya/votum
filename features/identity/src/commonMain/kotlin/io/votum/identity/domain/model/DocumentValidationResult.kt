/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */
package io.votum.identity.domain.model

data class DocumentValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)
