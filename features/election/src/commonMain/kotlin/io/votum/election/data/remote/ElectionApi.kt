/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.election.data.remote

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.votum.election.data.model.CandidateListResponse
import io.votum.election.data.model.ElectionDetailResponse
import io.votum.election.data.model.ElectionListResponse

interface ElectionApi {

    @GET("api/elections")
    suspend fun getElections(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): ElectionListResponse

    @GET("api/elections/{electionId}")
    suspend fun getElectionDetails(
        @Path("electionId") electionId: String
    ): ElectionDetailResponse

    @GET("api/elections/{electionId}/candidates")
    suspend fun getElectionCandidates(
        @Path("electionId") electionId: String
    ): CandidateListResponse
}
