/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.votum.core.presentation.theme.VotumTheme
import io.votum.vote.domain.model.Candidate
import io.votum.vote.presentation.component.CandidateSelectionCard
import io.votum.vote.presentation.component.VoteConfirmationDialog
import io.votum.vote.presentation.component.VotingStatusCard
import io.votum.vote.presentation.screen.model.VotingScreenIntent
import io.votum.vote.presentation.screen.model.VotingScreenState
import kotlinx.serialization.Serializable
import io.votum.core.presentation.preview.VotumPreview
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun VotingScreen(
    modifier: Modifier = Modifier,
    viewModel: VotingViewModel = koinViewModel()
) {
    val state by viewModel.collectAsState()

    VotingScreenContent(
        state = state,
        onEvent = viewModel::sendIntent,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VotingScreenContent(
    state: VotingScreenState,
    onEvent: (VotingScreenIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "🗳️ ${state.electionTitle}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Cast your vote",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            if (state.selectedCandidateId != null && !state.isLoadingStatus) {
                Button(
                    onClick = { onEvent(VotingScreenIntent.ShowConfirmation) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    enabled = !state.isSubmittingVote
                ) {
                    Text(
                        text = "Submit Vote",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoadingStatus -> {
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
                                text = "Checking voting eligibility...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                state.error != null -> {
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
                        }
                    }
                }

                state.candidates.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                text = "📭",
                                style = MaterialTheme.typography.displayMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No candidates available",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (state.votingStatus != null) {
                            item {
                                VotingStatusCard(votingStatus = state.votingStatus)
                            }
                        }

                        item {
                            Text(
                                text = "Select a Candidate",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        items(
                            items = state.candidates,
                            key = { it.id }
                        ) { candidate ->
                            CandidateSelectionCard(
                                candidate = candidate,
                                isSelected = candidate.id == state.selectedCandidateId,
                                onSelect = {
                                    onEvent(VotingScreenIntent.SelectCandidate(candidate.id))
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }

            if (state.showConfirmationDialog && state.selectedCandidateId != null) {
                val selectedCandidate = state.candidates.find { it.id == state.selectedCandidateId }
                if (selectedCandidate != null) {
                    VoteConfirmationDialog(
                        candidate = selectedCandidate,
                        onConfirm = {
                            onEvent(VotingScreenIntent.ConfirmVote("private-key-placeholder"))
                        },
                        onDismiss = {
                            onEvent(VotingScreenIntent.DismissConfirmation)
                        },
                        isSubmitting = state.isSubmittingVote
                    )
                }
            }
        }
    }
}

@Serializable
data class Voting(val electionId: String)

@VotumPreview
@Composable
private fun VotingScreenPreview() {
    VotumTheme {
        VotingScreenContent(
            state = VotingScreenState(
                electionTitle = "Presidential Election 2024",
                candidates = listOf(
                    Candidate(
                        id = "1",
                        name = "Alice Johnson",
                        party = "Progressive Party",
                        description = "Experienced leader focused on economic reform",
                        imageUrl = null,
                        blockchainIndex = 0,
                        manifesto = null,
                        qualifications = emptyList()
                    ),
                    Candidate(
                        id = "2",
                        name = "Bob Smith",
                        party = "Conservative Alliance",
                        description = "Advocate for traditional values",
                        imageUrl = null,
                        blockchainIndex = 1,
                        manifesto = null,
                        qualifications = emptyList()
                    )
                ),
                selectedCandidateId = "1"
            ),
            onEvent = {}
        )
    }
}
