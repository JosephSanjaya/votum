/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.election.presentation.screen

import io.votum.core.presentation.utils.BaseViewModel
import io.votum.election.domain.model.ElectionStatus
import io.votum.election.domain.usecase.GetElectionsUseCase
import io.votum.election.domain.usecase.RefreshElectionsUseCase
import io.votum.election.domain.usecase.SearchElectionsUseCase
import io.votum.election.presentation.screen.model.ElectionListScreenIntent
import io.votum.election.presentation.screen.model.ElectionListScreenState
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ElectionListViewModel(
    private val getElectionsUseCase: GetElectionsUseCase,
    private val searchElectionsUseCase: SearchElectionsUseCase,
    private val refreshElectionsUseCase: RefreshElectionsUseCase
) : BaseViewModel<ElectionListScreenState, Unit>(
    initialState = ElectionListScreenState()
) {

    init {
        loadElections()
    }

    override fun onIntent(intent: Any) {
        when (intent) {
            is ElectionListScreenIntent.LoadElections -> loadElections()
            is ElectionListScreenIntent.LoadNextPage -> loadNextPage()
            is ElectionListScreenIntent.RefreshElections -> refreshElections()
            is ElectionListScreenIntent.SearchElections -> searchElections(intent.query)
            is ElectionListScreenIntent.FilterByStatus -> filterByStatus(intent.status)
            is ElectionListScreenIntent.NavigateToElectionDetail -> navigateToDetail(intent.electionId)
        }
    }

    private fun loadElections() = intent {
        reduce { state.copy(isLoading = true, error = null) }

        getElectionsUseCase(page = 1)
            .onSuccess { result ->
                reduce {
                    state.copy(
                        elections = result.elections,
                        pagination = result.pagination,
                        isLoading = false
                    )
                }
            }
            .onFailure { error ->
                reduce {
                    state.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load elections"
                    )
                }
            }
    }

    private fun loadNextPage() = intent {
        val currentPage = state.pagination?.currentPage ?: 1
        if (state.pagination?.hasNextPage == true && !state.isLoadingMore) {
            reduce { state.copy(isLoadingMore = true) }

            getElectionsUseCase(page = currentPage + 1)
                .onSuccess { result ->
                    reduce {
                        state.copy(
                            elections = state.elections + result.elections,
                            pagination = result.pagination,
                            isLoadingMore = false
                        )
                    }
                }
                .onFailure {
                    reduce { state.copy(isLoadingMore = false) }
                }
        }
    }

    private fun refreshElections() = intent {
        reduce { state.copy(isRefreshing = true) }

        refreshElectionsUseCase()
            .onSuccess { result ->
                reduce {
                    state.copy(
                        elections = result.elections,
                        pagination = result.pagination,
                        isRefreshing = false
                    )
                }
            }
            .onFailure {
                reduce { state.copy(isRefreshing = false) }
            }
    }

    private fun searchElections(query: String) = intent {
        reduce { state.copy(searchQuery = query, isLoading = true) }

        searchElectionsUseCase(query)
            .onSuccess { result ->
                reduce {
                    state.copy(
                        elections = result.elections,
                        pagination = result.pagination,
                        isLoading = false
                    )
                }
            }
            .onFailure { error ->
                reduce {
                    state.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
            }
    }

    private fun filterByStatus(status: ElectionStatus?) = intent {
        reduce {
            state.copy(
                selectedStatusFilter = status,
                elections = if (status == null) {
                    state.elections
                } else {
                    state.elections.filter { it.status == status }
                }
            )
        }
    }

    private fun navigateToDetail(electionId: String) = intent {
        sendIntent(ElectionListScreenIntent.NavigateToElectionDetail(electionId))
    }
}
