/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.election.domain.repository

import io.votum.election.domain.model.ElectionDetail
import io.votum.election.domain.model.PaginatedElections

interface ElectionRepository {

    suspend fun getElections(
        page: Int = 1,
        limit: Int = 10
    ): Result<PaginatedElections>

    suspend fun getElectionDetails(
        electionId: String
    ): Result<ElectionDetail>

    suspend fun searchElections(
        query: String,
        page: Int = 1
    ): Result<PaginatedElections>
}
