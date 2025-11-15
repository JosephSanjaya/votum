/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.result.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.votum.core.presentation.theme.VotumTheme
import io.votum.result.domain.model.CandidateResult
import io.votum.result.domain.model.ElectionResults
import io.votum.result.domain.model.LiveResults
import io.votum.result.presentation.component.CandidateResultCard
import io.votum.result.presentation.component.RefreshIndicator
import io.votum.result.presentation.component.ResultCard
import io.votum.result.presentation.component.ResultsChart
import io.votum.result.presentation.screen.model.LiveResultScreenIntent
import io.votum.result.presentation.screen.model.LiveResultScreenState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import io.votum.core.presentation.preview.VotumPreview
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun LiveResultScreen(
    electionId: String,
    modifier: Modifier = Modifier,
    viewModel: LiveResultViewModel = koinViewModel()
) {
    val state by viewModel.collectAsState()

    LaunchedEffect(electionId) {
        viewModel.sendIntent(LiveResultScreenIntent.LoadLiveResults(electionId))
    }

    LiveResultScreenContent(
        state = state,
        onIntent = viewModel::sendIntent,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LiveResultScreenContent(
    state: LiveResultScreenState,
    onIntent: (LiveResultScreenIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔴 Live Results",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (state.lastRefreshTime != null) {
                            Text(
                                text = "Last refresh: ${state.lastRefreshTime}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onIntent(LiveResultScreenIntent.NavigateBack) }) {
                        Text(
                            text = "⬅️",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onIntent(LiveResultScreenIntent.ManualRefresh) }) {
                        Text(
                            text = "🔄",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading && state.liveResults == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading live results...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                state.error != null && state.liveResults == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                text = "❌",
                                style = MaterialTheme.typography.displayMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = state.error,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { onIntent(LiveResultScreenIntent.ManualRefresh) }) {
                                Text("🔄 Retry")
                            }
                        }
                    }
                }

                state.liveResults != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "⏱️ Auto-refresh",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = if (state.autoRefreshEnabled) {
                                        "Updates every" +
                                            " ${state.liveResults.updateInterval}" +
                                            "s"
                                    } else {
                                        "Disabled"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = state.autoRefreshEnabled,
                                onCheckedChange = { onIntent(LiveResultScreenIntent.ToggleAutoRefresh) }
                            )
                        }

                        if (state.lastRefreshTime != null) {
                            RefreshIndicator(lastRefreshTime = state.lastRefreshTime)
                        }

                        ResultCard(results = state.liveResults.currentResults)

                        ResultsChart(candidateResults = state.liveResults.currentResults.candidateResults)

                        Text(
                            text = "Candidate Results",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        state.liveResults.currentResults.candidateResults.forEach { candidate ->
                            CandidateResultCard(candidateResult = candidate)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Serializable
data class LiveResult(val electionId: String)

@VotumPreview
@Composable
private fun LiveResultScreenPreview() {
    VotumTheme {
        LiveResultScreenContent(
            state = LiveResultScreenState(
                liveResults = LiveResults(
                    electionId = "election-1",
                    currentResults = ElectionResults(
                        electionId = "election-1",
                        totalVotesCast = 15420,
                        totalEligibleVoters = 25000,
                        turnoutPercentage = 61.68,
                        candidateResults = persistentListOf(
                            CandidateResult(
                                candidateId = "candidate-1",
                                candidateName = "Alice Johnson",
                                voteCount = 8234,
                                votePercentage = 53.4,
                                isWinner = true
                            ),
                            CandidateResult(
                                candidateId = "candidate-2",
                                candidateName = "Bob Smith",
                                voteCount = 7186,
                                votePercentage = 46.6,
                                isWinner = false
                            )
                        ),
                        winner = CandidateResult(
                            candidateId = "candidate-1",
                            candidateName = "Alice Johnson",
                            voteCount = 8234,
                            votePercentage = 53.4,
                            isWinner = true
                        ),
                        isFinalized = false,
                        finalizedAt = null,
                        blockchainProof = "0xabcdef1234567890"
                    ),
                    lastUpdated = "2024-01-15T11:25:00Z",
                    isLive = true,
                    updateInterval = 30
                ),
                autoRefreshEnabled = true,
                lastRefreshTime = "2 mins ago"
            ),
            onIntent = {}
        )
    }
}
