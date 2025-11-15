/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.election.data.mapper

import io.votum.election.data.model.CandidateDto
import io.votum.election.data.model.EligibilityCriteriaDto
import io.votum.election.data.model.ElectionDetailDto
import io.votum.election.data.model.ElectionDto
import io.votum.election.data.model.ElectionListResponse
import io.votum.election.data.model.ElectionStatisticsDto
import io.votum.election.data.model.PaginationDto
import io.votum.election.domain.model.Candidate
import io.votum.election.domain.model.Election
import io.votum.election.domain.model.ElectionDetail
import io.votum.election.domain.model.ElectionStatistics
import io.votum.election.domain.model.ElectionStatus
import io.votum.election.domain.model.ElectionType
import io.votum.election.domain.model.EligibilityCriteria
import io.votum.election.domain.model.PaginatedElections
import io.votum.election.domain.model.Pagination
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun ElectionDto.toDomain(): Election {
    return Election(
        id = id,
        title = title,
        description = description,
        startTime = Instant.parse(startTime),
        endTime = Instant.parse(endTime),
        registrationDeadline = Instant.parse(registrationDeadline),
        electionType = try {
            ElectionType.valueOf(electionType)
        } catch (e: IllegalArgumentException) {
            ElectionType.OTHER
        },
        status = try {
            ElectionStatus.valueOf(status)
        } catch (e: IllegalArgumentException) {
            ElectionStatus.DRAFT
        },
        minimumAge = minimumAge,
        voteCount = count.voteRecords,
        candidateCount = candidates.size
    )
}

@OptIn(ExperimentalTime::class)
fun ElectionDetailDto.toDomain(): ElectionDetail {
    return ElectionDetail(
        id = id,
        title = title,
        description = description,
        startTime = Instant.parse(startTime),
        endTime = Instant.parse(endTime),
        registrationDeadline = Instant.parse(registrationDeadline),
        electionType = try {
            ElectionType.valueOf(electionType)
        } catch (e: IllegalArgumentException) {
            ElectionType.OTHER
        },
        status = try {
            ElectionStatus.valueOf(status)
        } catch (e: IllegalArgumentException) {
            ElectionStatus.DRAFT
        },
        minimumAge = minimumAge,
        blockchainAddress = blockchainAddress,
        eligibilityCriteria = eligibilityCriteria?.map { it.toDomain() } ?: emptyList(),
        candidates = candidates.map { it.toDomain() },
        statistics = electionStatistics?.toDomain(),
        voteCount = count.voteRecords
    )
}

fun CandidateDto.toDomain(): Candidate {
    return Candidate(
        id = id,
        name = name,
        party = party,
        description = description,
        imageUrl = imageUrl,
        blockchainIndex = blockchainIndex,
        manifesto = manifesto,
        qualifications = qualifications ?: emptyList()
    )
}

fun EligibilityCriteriaDto.toDomain(): EligibilityCriteria {
    return EligibilityCriteria(
        id = id,
        criterion = criterion,
        description = description,
        isRequired = isRequired
    )
}

fun ElectionStatisticsDto.toDomain(): ElectionStatistics {
    return ElectionStatistics(
        totalRegisteredVoters = totalRegisteredVoters,
        totalVotesCast = totalVotesCast,
        votingProgress = votingProgress
    )
}

fun PaginationDto.toDomain(): Pagination {
    return Pagination(
        currentPage = currentPage,
        totalPages = totalPages,
        totalItems = totalItems,
        itemsPerPage = itemsPerPage,
        hasNextPage = hasNextPage,
        hasPreviousPage = hasPreviousPage
    )
}

fun ElectionListResponse.toDomain(): PaginatedElections {
    return PaginatedElections(
        elections = data.map { it.toDomain() },
        pagination = pagination.toDomain()
    )
}
