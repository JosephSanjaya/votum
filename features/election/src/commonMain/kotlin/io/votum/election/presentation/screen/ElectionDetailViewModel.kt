/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.election.presentation.screen

import io.votum.core.presentation.utils.BaseViewModel
import io.votum.election.domain.usecase.GetElectionDetailsUseCase
import io.votum.election.presentation.screen.model.ElectionDetailScreenIntent
import io.votum.election.presentation.screen.model.ElectionDetailScreenState
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ElectionDetailViewModel(
    private val getElectionDetailsUseCase: GetElectionDetailsUseCase
) : BaseViewModel<ElectionDetailScreenState, Unit>(
    initialState = ElectionDetailScreenState()
) {

    override fun onIntent(intent: Any) {
        when (intent) {
            is ElectionDetailScreenIntent.LoadElectionDetails -> loadElectionDetails(intent.electionId)
            is ElectionDetailScreenIntent.NavigateBack -> navigateBack()
            is ElectionDetailScreenIntent.NavigateToVoting -> navigateToVoting(intent.electionId)
        }
    }

    private fun loadElectionDetails(electionId: String) = intent {
        reduce { state.copy(isLoading = true, error = null) }

        getElectionDetailsUseCase(electionId)
            .onSuccess { election ->
                reduce {
                    state.copy(
                        election = election,
                        isLoading = false
                    )
                }
            }
            .onFailure { error ->
                reduce {
                    state.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load election details"
                    )
                }
            }
    }

    private fun navigateBack() = intent {
        sendIntent(ElectionDetailScreenIntent.NavigateBack)
    }

    private fun navigateToVoting(electionId: String) = intent {
        sendIntent(ElectionDetailScreenIntent.NavigateToVoting(electionId))
    }
}
