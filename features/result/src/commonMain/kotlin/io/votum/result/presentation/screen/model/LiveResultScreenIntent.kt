/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.result.presentation.screen.model

sealed interface LiveResultScreenIntent {
    data class LoadLiveResults(val electionId: String) : LiveResultScreenIntent
    data object ToggleAutoRefresh : LiveResultScreenIntent
    data object ManualRefresh : LiveResultScreenIntent
    data object DismissError : LiveResultScreenIntent
    data object NavigateBack : LiveResultScreenIntent
}
