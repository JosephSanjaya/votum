/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.election.domain.model

import kotlinx.datetime.Instant

data class Election(
    val id: String,
    val title: String,
    val description: String,
    val startTime: Instant,
    val endTime: Instant,
    val registrationDeadline: Instant,
    val electionType: ElectionType,
    val status: ElectionStatus,
    val minimumAge: Int,
    val voteCount: Int,
    val candidateCount: Int
)

data class ElectionDetail(
    val id: String,
    val title: String,
    val description: String,
    val startTime: Instant,
    val endTime: Instant,
    val registrationDeadline: Instant,
    val electionType: ElectionType,
    val status: ElectionStatus,
    val minimumAge: Int,
    val blockchainAddress: String?,
    val eligibilityCriteria: List<EligibilityCriteria>,
    val candidates: List<Candidate>,
    val statistics: ElectionStatistics?,
    val voteCount: Int
)

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

data class EligibilityCriteria(
    val id: String,
    val criterion: String,
    val description: String,
    val isRequired: Boolean
)

data class ElectionStatistics(
    val totalRegisteredVoters: Int,
    val totalVotesCast: Int,
    val votingProgress: Double
)

data class PaginatedElections(
    val elections: List<Election>,
    val pagination: Pagination
)

data class Pagination(
    val currentPage: Int,
    val totalPages: Int,
    val totalItems: Int,
    val itemsPerPage: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean
)

enum class ElectionStatus {
    DRAFT,
    REGISTRATION_OPEN,
    VOTING_ACTIVE,
    VOTING_CLOSED,
    FINALIZED
}

enum class ElectionType {
    PRESIDENTIAL,
    PARLIAMENTARY,
    LOCAL,
    REFERENDUM,
    OTHER
}
