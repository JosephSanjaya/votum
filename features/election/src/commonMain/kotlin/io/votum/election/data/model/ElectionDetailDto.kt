/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.election.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ElectionDetailDto(
    val id: String,
    val title: String,
    val description: String,
    val startTime: String,
    val endTime: String,
    val registrationDeadline: String,
    val electionType: String,
    val status: String,
    val minimumAge: Int,
    val blockchainAddress: String? = null,
    val eligibilityCriteria: List<EligibilityCriteriaDto>? = null,
    val candidates: List<CandidateDto>,
    val electionStatistics: ElectionStatisticsDto? = null,
    @SerialName("_count")
    val count: VoteCountDto
)

@Serializable
data class EligibilityCriteriaDto(
    val id: String,
    val criterion: String,
    val description: String,
    val isRequired: Boolean
)

@Serializable
data class ElectionStatisticsDto(
    val totalRegisteredVoters: Int,
    val totalVotesCast: Int,
    val votingProgress: Double
)
