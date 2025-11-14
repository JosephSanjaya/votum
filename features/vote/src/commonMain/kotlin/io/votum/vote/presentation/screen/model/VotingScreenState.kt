/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.presentation.screen.model

import io.votum.election.domain.model.Candidate
import io.votum.vote.domain.model.VoteReceipt
import io.votum.vote.domain.model.VotingStatus

data class VotingScreenState(
    val electionId: String = "",
    val electionTitle: String = "",
    val candidates: List<Candidate> = emptyList(),
    val selectedCandidateId: String? = null,
    val votingStatus: VotingStatus? = null,
    val isLoadingStatus: Boolean = false,
    val isSubmittingVote: Boolean = false,
    val showConfirmationDialog: Boolean = false,
    val error: String? = null,
    val voteReceipt: VoteReceipt? = null
)
