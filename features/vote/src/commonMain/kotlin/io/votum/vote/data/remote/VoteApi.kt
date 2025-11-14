/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.data.remote

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import io.votum.vote.data.model.VoteCastRequest
import io.votum.vote.data.model.VoteCastResponse
import io.votum.vote.data.model.VoteReceiptResponse
import io.votum.vote.data.model.VoteVerificationResponse
import io.votum.vote.data.model.VotingStatusResponse

interface VoteApi {

    @GET("api/voting/status/{electionId}/{voterId}")
    suspend fun getVotingStatus(
        @Path("electionId") electionId: String,
        @Path("voterId") voterId: String
    ): VotingStatusResponse

    @POST("api/voting/cast")
    suspend fun castVote(
        @Body request: VoteCastRequest
    ): VoteCastResponse

    @GET("api/voting/verify/{transactionHash}")
    suspend fun verifyVote(
        @Path("transactionHash") transactionHash: String
    ): VoteVerificationResponse

    @GET("api/voting/receipt/{voteId}/{voterId}")
    suspend fun getVoteReceipt(
        @Path("voteId") voteId: String,
        @Path("voterId") voterId: String
    ): VoteReceiptResponse
}
