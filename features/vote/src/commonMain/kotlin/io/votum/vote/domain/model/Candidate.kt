/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.domain.model

data class Candidate(
    val id: String,
    val name: String,
    val party: String,
    val description: String,
    val imageUrl: String?,
    val blockchainIndex: Int,
    val manifesto: String?,
    val qualifications: List<String>
)
