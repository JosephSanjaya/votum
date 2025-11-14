/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VoteCastRequest(
    val electionId: String,
    val voterId: String,
    val candidateId: String,
    val voterSignature: String
)
