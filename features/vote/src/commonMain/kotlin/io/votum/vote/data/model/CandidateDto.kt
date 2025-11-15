/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CandidateDto(
    val id: String,
    val name: String,
    val party: String,
    val description: String,
    val imageUrl: String? = null,
    val blockchainIndex: Int,
    val manifesto: String? = null,
    val qualifications: List<String> = emptyList()
)
