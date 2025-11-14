/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.result.presentation.screen.model

sealed interface ResultScreenIntent {
    data class LoadResults(val electionId: String) : ResultScreenIntent
    data object RefreshResults : ResultScreenIntent
    data object DismissError : ResultScreenIntent
    data object NavigateBack : ResultScreenIntent
}
