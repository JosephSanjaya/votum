/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.presentation.screen.model

import io.votum.core.presentation.navigation.NavigationIntent

sealed interface VoteReceiptScreenIntent : NavigationIntent {
    data class LoadReceipt(val voteId: String, val voterId: String) : VoteReceiptScreenIntent
    data class VerifyVote(val transactionHash: String) : VoteReceiptScreenIntent
    data object ShareReceipt : VoteReceiptScreenIntent
    data object NavigateBack : VoteReceiptScreenIntent
    data class ShowError(val message: String) : VoteReceiptScreenIntent
}
