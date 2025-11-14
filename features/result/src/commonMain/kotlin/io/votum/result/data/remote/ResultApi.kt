/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.result.data.remote

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import io.votum.result.data.model.ElectionResultsResponse
import io.votum.result.data.model.LiveResultsResponse

interface ResultApi {

    @GET("api/results/{electionId}")
    suspend fun getElectionResults(
        @Path("electionId") electionId: String
    ): ElectionResultsResponse

    @GET("api/results/{electionId}/live")
    suspend fun getLiveResults(
        @Path("electionId") electionId: String
    ): LiveResultsResponse
}
