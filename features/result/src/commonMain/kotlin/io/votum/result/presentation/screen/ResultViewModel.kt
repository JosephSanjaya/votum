/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.result.presentation.screen

import io.votum.core.presentation.utils.BaseViewModel
import io.votum.result.domain.usecase.GetElectionResultsUseCase
import io.votum.result.presentation.screen.model.ResultScreenIntent
import io.votum.result.presentation.screen.model.ResultScreenState
import kotlinx.datetime.Clock
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ResultViewModel(
    private val getElectionResultsUseCase: GetElectionResultsUseCase
) : BaseViewModel<ResultScreenState, Unit>(
    initialState = ResultScreenState()
) {

    private var currentElectionId: String? = null

    override fun onIntent(intent: Any) {
        when (intent) {
            is ResultScreenIntent.LoadResults -> loadResults(intent.electionId)
            is ResultScreenIntent.RefreshResults -> refreshResults()
            is ResultScreenIntent.DismissError -> dismissError()
            is ResultScreenIntent.NavigateBack -> sendIntent(intent)
        }
    }

    private fun loadResults(electionId: String) = intent {
        currentElectionId = electionId
        reduce { state.copy(isLoading = true, error = null) }

        getElectionResultsUseCase(electionId)
            .onSuccess { results ->
                reduce {
                    state.copy(
                        isLoading = false,
                        results = results,
                        lastUpdated = Clock.System.now().toString()
                    )
                }
            }
            .onFailure { exception ->
                reduce {
                    state.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to load results"
                    )
                }
            }
    }

    private fun refreshResults() = intent {
        val electionId = currentElectionId ?: return@intent

        reduce { state.copy(isRefreshing = true, error = null) }

        getElectionResultsUseCase(electionId)
            .onSuccess { results ->
                reduce {
                    state.copy(
                        isRefreshing = false,
                        results = results,
                        lastUpdated = Clock.System.now().toString()
                    )
                }
            }
            .onFailure { exception ->
                reduce {
                    state.copy(
                        isRefreshing = false,
                        error = exception.message ?: "Failed to refresh results"
                    )
                }
            }
    }

    private fun dismissError() = intent {
        reduce { state.copy(error = null) }
    }
}
