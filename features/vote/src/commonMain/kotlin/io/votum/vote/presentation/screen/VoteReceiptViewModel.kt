/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.presentation.screen

import io.votum.core.presentation.utils.BaseViewModel
import io.votum.vote.domain.usecase.GetVoteReceiptUseCase
import io.votum.vote.domain.usecase.VerifyVoteUseCase
import io.votum.vote.presentation.screen.model.VoteReceiptScreenIntent
import io.votum.vote.presentation.screen.model.VoteReceiptScreenState
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam

@KoinViewModel
class VoteReceiptViewModel(
    @InjectedParam private val voteId: String,
    @InjectedParam private val voterId: String,
    private val getVoteReceiptUseCase: GetVoteReceiptUseCase,
    private val verifyVoteUseCase: VerifyVoteUseCase
) : BaseViewModel<VoteReceiptScreenState, Unit>(
    initialState = VoteReceiptScreenState(),
    onCreate = {
        sendIntent(VoteReceiptScreenIntent.LoadReceipt(voteId, voterId))
    }
) {

    override fun onIntent(intent: Any) {
        when (intent) {
            is VoteReceiptScreenIntent.LoadReceipt -> loadReceipt(intent.voteId, intent.voterId)
            is VoteReceiptScreenIntent.VerifyVote -> verifyVote(intent.transactionHash)
            is VoteReceiptScreenIntent.ShareReceipt -> shareReceipt()
            is VoteReceiptScreenIntent.NavigateBack -> navigateBack()
        }
    }

    private fun loadReceipt(voteId: String, voterId: String) = intent {
        reduce { state.copy(isLoadingReceipt = true, error = null) }

        getVoteReceiptUseCase(voteId, voterId)
            .onSuccess { receipt ->
                reduce {
                    state.copy(
                        receipt = receipt,
                        isLoadingReceipt = false
                    )
                }
            }
            .onFailure { error ->
                reduce {
                    state.copy(
                        isLoadingReceipt = false,
                        error = error.message ?: "Failed to load receipt"
                    )
                }
            }
    }

    private fun verifyVote(transactionHash: String) = intent {
        reduce { state.copy(isVerifying = true, error = null) }

        verifyVoteUseCase(transactionHash)
            .onSuccess { verification ->
                reduce {
                    state.copy(
                        verification = verification,
                        isVerifying = false
                    )
                }
            }
            .onFailure { error ->
                reduce {
                    state.copy(
                        isVerifying = false,
                        error = error.message ?: "Failed to verify vote"
                    )
                }
            }
    }

    private fun shareReceipt() = intent {
        val receipt = state.receipt
        if (receipt != null) {
            sendIntent(VoteReceiptScreenIntent.ShareReceipt)
        }
    }

    private fun navigateBack() = intent {
        sendIntent(VoteReceiptScreenIntent.NavigateBack)
    }
}
