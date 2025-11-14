/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.election.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ElectionDto(
    val id: String,
    val title: String,
    val description: String,
    val startTime: String,
    val endTime: String,
    val registrationDeadline: String,
    val electionType: String,
    val status: String,
    val minimumAge: Int,
    val candidates: List<CandidateDto>,
    @SerialName("_count")
    val count: VoteCountDto
)

@Serializable
data class CandidateDto(
    val id: String,
    val name: String,
    val party: String,
    val description: String,
    val imageUrl: String? = null,
    val blockchainIndex: Int,
    val manifesto: String? = null,
    val qualifications: List<String>? = null
)

@Serializable
data class VoteCountDto(
    val voteRecords: Int
)

@Serializable
data class PaginationDto(
    val currentPage: Int,
    val totalPages: Int,
    val totalItems: Int,
    val itemsPerPage: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean
)
