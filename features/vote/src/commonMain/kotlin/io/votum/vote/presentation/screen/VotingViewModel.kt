/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.presentation.screen

import io.votum.core.presentation.utils.BaseViewModel
import io.votum.vote.domain.usecase.CastVoteUseCase
import io.votum.vote.domain.usecase.CheckVotingEligibilityUseCase
import io.votum.vote.presentation.screen.model.VotingScreenIntent
import io.votum.vote.presentation.screen.model.VotingScreenState
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class VotingViewModel(
    private val checkVotingEligibilityUseCase: CheckVotingEligibilityUseCase,
    private val castVoteUseCase: CastVoteUseCase
) : BaseViewModel<VotingScreenState, Unit>(
    initialState = VotingScreenState()
) {

    override fun onIntent(intent: Any) {
        when (intent) {
            is VotingScreenIntent.LoadVotingStatus -> loadVotingStatus(intent.electionId, intent.voterId)
            is VotingScreenIntent.SelectCandidate -> selectCandidate(intent.candidateId)
            is VotingScreenIntent.ShowConfirmation -> showConfirmation()
            is VotingScreenIntent.DismissConfirmation -> dismissConfirmation()
            is VotingScreenIntent.ConfirmVote -> confirmVote(intent.privateKey)
            is VotingScreenIntent.NavigateToReceipt -> navigateToReceipt(intent.voteId, intent.candidateName)
            is VotingScreenIntent.DismissError -> dismissError()
        }
    }

    private fun loadVotingStatus(electionId: String, voterId: String) = intent {
        reduce { state.copy(isLoadingStatus = true, error = null) }

        checkVotingEligibilityUseCase(electionId, voterId)
            .onSuccess { votingStatus ->
                if (votingStatus.voterHasVoted) {
                    reduce {
                        state.copy(
                            isLoadingStatus = false,
                            error = "You have already voted in this election"
                        )
                    }
                } else if (!votingStatus.isVotingActive) {
                    reduce {
                        state.copy(
                            isLoadingStatus = false,
                            error = "Voting is not currently active for this election"
                        )
                    }
                } else {
                    reduce {
                        state.copy(
                            votingStatus = votingStatus,
                            candidates = votingStatus.candidates,
                            isLoadingStatus = false
                        )
                    }
                }
            }
            .onFailure { error ->
                reduce {
                    state.copy(
                        isLoadingStatus = false,
                        error = error.message ?: "Failed to check voting eligibility"
                    )
                }
            }
    }

    private fun selectCandidate(candidateId: String) = intent {
        reduce {
            state.copy(
                selectedCandidateId = candidateId,
                error = null
            )
        }
    }

    private fun showConfirmation() = intent {
        if (state.selectedCandidateId != null) {
            reduce { state.copy(showConfirmationDialog = true) }
        } else {
            reduce { state.copy(error = "Please select a candidate") }
        }
    }

    private fun dismissConfirmation() = intent {
        reduce { state.copy(showConfirmationDialog = false) }
    }

    private fun confirmVote(privateKey: String) = intent {
        val selectedCandidateId = state.selectedCandidateId
        val electionId = state.electionId

        if (selectedCandidateId == null) {
            reduce { state.copy(error = "No candidate selected") }
            return@intent
        }

        reduce {
            state.copy(
                isSubmittingVote = true,
                showConfirmationDialog = false,
                error = null
            )
        }

        val voterId = "current-voter-id"

        castVoteUseCase(
            electionId = electionId,
            voterId = voterId,
            candidateId = selectedCandidateId,
            privateKey = privateKey
        )
            .onSuccess { receipt ->
                reduce {
                    state.copy(
                        isSubmittingVote = false,
                        voteReceipt = receipt
                    )
                }

                val candidateName = state.candidates
                    .find { it.id == selectedCandidateId }?.name ?: "Unknown"

                sendIntent(
                    VotingScreenIntent.NavigateToReceipt(
                        voteId = receipt.voteId,
                        candidateName = candidateName
                    )
                )
            }
            .onFailure { error ->
                reduce {
                    state.copy(
                        isSubmittingVote = false,
                        error = error.message ?: "Failed to cast vote"
                    )
                }
            }
    }

    private fun navigateToReceipt(voteId: String, candidateName: String) = intent {
        sendIntent(VotingScreenIntent.NavigateToReceipt(voteId, candidateName))
    }

    private fun dismissError() = intent {
        reduce { state.copy(error = null) }
    }
}
