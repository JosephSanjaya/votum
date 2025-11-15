/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.result.presentation.screen

import androidx.lifecycle.viewModelScope
import io.votum.core.presentation.utils.BaseViewModel
import io.votum.result.domain.usecase.GetLiveResultsUseCase
import io.votum.result.presentation.screen.model.LiveResultScreenIntent
import io.votum.result.presentation.screen.model.LiveResultScreenState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class LiveResultViewModel(
    private val getLiveResultsUseCase: GetLiveResultsUseCase
) : BaseViewModel<LiveResultScreenState, Unit>(
    initialState = LiveResultScreenState()
) {

    private var currentElectionId: String? = null
    private var refreshJob: Job? = null

    override fun onIntent(intent: Any) {
        when (intent) {
            is LiveResultScreenIntent.LoadLiveResults -> loadLiveResults(intent.electionId)
            is LiveResultScreenIntent.ToggleAutoRefresh -> toggleAutoRefresh()
            is LiveResultScreenIntent.ManualRefresh -> manualRefresh()
            is LiveResultScreenIntent.DismissError -> dismissError()
            is LiveResultScreenIntent.NavigateBack -> {
                stopAutoRefresh()
                sendIntent(intent)
            }
        }
    }

    private fun loadLiveResults(electionId: String) = intent {
        currentElectionId = electionId
        reduce { state.copy(isLoading = true, error = null) }

        getLiveResultsUseCase(electionId)
            .onSuccess { liveResults ->
                reduce {
                    state.copy(
                        isLoading = false,
                        liveResults = liveResults,
                        lastRefreshTime = "Just now"
                    )
                }

                if (state.autoRefreshEnabled && liveResults.isLive) {
                    startAutoRefresh(electionId, liveResults.updateInterval)
                }
            }
            .onFailure { exception ->
                reduce {
                    state.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to load live results"
                    )
                }
            }
    }

    private fun toggleAutoRefresh() = intent {
        val newState = !state.autoRefreshEnabled
        reduce { state.copy(autoRefreshEnabled = newState) }

        if (newState) {
            val electionId = currentElectionId
            val updateInterval = state.liveResults?.updateInterval
            if (electionId != null && updateInterval != null) {
                startAutoRefresh(electionId, updateInterval)
            }
        } else {
            stopAutoRefresh()
        }
    }

    private fun manualRefresh() = intent {
        val electionId = currentElectionId ?: return@intent

        reduce { state.copy(isLoading = true, error = null) }

        getLiveResultsUseCase(electionId)
            .onSuccess { liveResults ->
                reduce {
                    state.copy(
                        isLoading = false,
                        liveResults = liveResults,
                        lastRefreshTime = "Just now"
                    )
                }
            }
            .onFailure { exception ->
                reduce {
                    state.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to refresh results"
                    )
                }
            }
    }

    private fun startAutoRefresh(electionId: String, intervalSeconds: Int) {
        stopAutoRefresh()
        refreshJob = viewModelScope.launch {
            while (isActive && container.stateFlow.value.autoRefreshEnabled) {
                delay(intervalSeconds * 1000L)
                if (container.stateFlow.value.autoRefreshEnabled) {
                    loadLiveResults(electionId)
                }
            }
        }
    }

    private fun stopAutoRefresh() {
        refreshJob?.cancel()
        refreshJob = null
    }

    private fun dismissError() = intent {
        reduce { state.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        stopAutoRefresh()
    }
}
