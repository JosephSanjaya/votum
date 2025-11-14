/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.presentation.screen.model

import io.votum.vote.domain.model.VoteReceipt
import io.votum.vote.domain.model.VoteVerification

data class VoteReceiptScreenState(
    val receipt: VoteReceipt? = null,
    val verification: VoteVerification? = null,
    val isLoadingReceipt: Boolean = false,
    val isVerifying: Boolean = false,
    val error: String? = null,
    val candidateName: String = ""
)
