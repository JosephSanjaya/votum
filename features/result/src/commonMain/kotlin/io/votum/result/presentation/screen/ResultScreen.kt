/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.result.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import io.votum.result.presentation.component.BlockchainProofCard
import io.votum.result.presentation.component.CandidateResultCard
import io.votum.result.presentation.component.ResultCard
import io.votum.result.presentation.component.ResultsChart
import io.votum.result.presentation.screen.model.ResultScreenIntent
import io.votum.result.presentation.screen.model.ResultScreenState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import io.votum.core.presentation.preview.VotumPreview
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun ResultScreen(
    electionId: String,
    modifier: Modifier = Modifier,
    viewModel: ResultViewModel = koinViewModel()
) {
    val state by viewModel.collectAsState()

    LaunchedEffect(electionId) {
        viewModel.sendIntent(ResultScreenIntent.LoadResults(electionId))
    }

    ResultScreenContent(
        state = state,
        onIntent = viewModel::sendIntent,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResultScreenContent(
    state: ResultScreenState,
    onIntent: (ResultScreenIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "📊 Election Results",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        if (state.lastUpdated != null) {
                            Text(
                                text = "Last updated: ${state.lastUpdated}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onIntent(ResultScreenIntent.NavigateBack) }) {
                        Text(
                            text = "⬅️",
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
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onIntent(ResultScreenIntent.RefreshResults) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    state.isLoading && state.results == null -> {
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
                                    text = "Loading results...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    state.error != null && state.results == null -> {
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
                                Button(onClick = { onIntent(ResultScreenIntent.RefreshResults) }) {
                                    Text("🔄 Retry")
                                }
                            }
                        }
                    }

                    state.results != null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ResultCard(results = state.results)

                            ResultsChart(candidateResults = state.results.candidateResults)

                            Text(
                                text = "Candidate Results",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            state.results.candidateResults.forEach { candidate ->
                                CandidateResultCard(candidateResult = candidate)
                            }

                            if (state.results.isFinalized) {
                                BlockchainProofCard(
                                    blockchainProof = state.results.blockchainProof
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Serializable
data class Result(val electionId: String)

@VotumPreview
@Composable
private fun ResultScreenPreview() {
    VotumTheme {
        ResultScreenContent(
            state = ResultScreenState(
                results = ElectionResults(
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
                    isFinalized = true,
                    finalizedAt = "2024-01-15T12:00:00Z",
                    blockchainProof = "0xabcdef1234567890abcdef1234567890"
                ),
                lastUpdated = "2 mins ago"
            ),
            onIntent = {}
        )
    }
}
