/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
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
import io.votum.vote.domain.model.VoteReceipt
import io.votum.vote.domain.model.VoteVerification
import io.votum.vote.presentation.component.VerificationResultCard
import io.votum.vote.presentation.component.VoteReceiptCard
import io.votum.vote.presentation.screen.model.VoteReceiptScreenIntent
import io.votum.vote.presentation.screen.model.VoteReceiptScreenState
import kotlinx.serialization.Serializable
import io.votum.core.presentation.preview.VotumPreview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun VoteReceiptScreen(
    voteId: String,
    voterId: String,
    modifier: Modifier = Modifier,
    viewModel: VoteReceiptViewModel = koinViewModel {
        parametersOf(voteId, voterId)
    }
) {
    val state by viewModel.collectAsState()

    VoteReceiptScreenContent(
        state = state,
        onEvent = viewModel::sendIntent,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VoteReceiptScreenContent(
    state: VoteReceiptScreenState,
    onEvent: (VoteReceiptScreenIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "📄 Vote Receipt",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Your voting confirmation",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(VoteReceiptScreenIntent.NavigateBack) }) {
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoadingReceipt -> {
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
                                text = "Loading receipt...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                state.error != null && state.receipt == null -> {
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

                state.receipt != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        VoteReceiptCard(
                            receipt = state.receipt,
                            candidateName = state.candidateName,
                            onVerify = {
                                onEvent(
                                    VoteReceiptScreenIntent.VerifyVote(
                                        state.receipt.transactionHash
                                    )
                                )
                            },
                            onShare = {
                                onEvent(VoteReceiptScreenIntent.ShareReceipt)
                            }
                        )

                        if (state.isVerifying) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator()
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Verifying on blockchain...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        if (state.verification != null) {
                            VerificationResultCard(verification = state.verification)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Serializable
data class VoteReceipt(
    val voteId: String,
    val voterId: String
)

@VotumPreview
@Composable
private fun VoteReceiptScreenPreview() {
    VotumTheme {
        VoteReceiptScreenContent(
            state = VoteReceiptScreenState(
                receipt = io.votum.vote.domain.model.VoteReceipt(
                    voteId = "vote-uuid-1",
                    electionId = "election-uuid-1",
                    candidateId = "candidate-uuid-1",
                    transactionHash = "0xabcdef1234567890",
                    blockNumber = "1234567",
                    timestamp = "2024-01-15T11:15:00Z",
                    verificationCode = "VERIFY_ABCD1234"
                ),
                candidateName = "Alice Johnson",
                verification = VoteVerification(
                    isValid = true,
                    transactionHash = "0xabcdef1234567890",
                    blockNumber = "1234567",
                    timestamp = "2024-01-15T11:15:00Z",
                    voterPublicKey = "0x1234567890abcdef",
                    candidateIndex = 0,
                    verificationProof = "0xabcdef1234567890"
                )
            ),
            onEvent = {}
        )
    }
}
