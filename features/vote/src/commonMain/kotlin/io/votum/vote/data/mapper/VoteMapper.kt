/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.data.mapper

import io.votum.vote.data.model.VoteCastData
import io.votum.vote.data.model.VoteReceiptData
import io.votum.vote.data.model.VoteVerificationData
import io.votum.vote.data.model.VotingStatusData
import io.votum.vote.domain.model.VoteReceipt
import io.votum.vote.domain.model.VoteVerification
import io.votum.vote.domain.model.VotingStatus

fun VotingStatusData.toDomain(): VotingStatus {
    return VotingStatus(
        electionId = electionId,
        isVotingActive = isVotingActive,
        totalVotesCast = totalVotesCast,
        voterHasVoted = voterHasVoted,
        remainingTime = remainingTime,
        votingProgress = votingProgress
    )
}

fun VoteCastData.toDomain(): VoteReceipt {
    return VoteReceipt(
        voteId = voteId,
        electionId = electionId,
        candidateId = candidateId,
        transactionHash = transactionHash,
        blockNumber = blockNumber,
        timestamp = timestamp,
        verificationCode = verificationCode
    )
}

fun VoteReceiptData.toDomain(): VoteReceipt {
    return VoteReceipt(
        voteId = voteId,
        electionId = electionId,
        candidateId = candidateId,
        transactionHash = transactionHash,
        blockNumber = blockNumber,
        timestamp = timestamp,
        verificationCode = verificationCode
    )
}

fun VoteVerificationData.toDomain(): VoteVerification {
    return VoteVerification(
        isValid = isValid,
        transactionHash = transactionHash,
        blockNumber = blockNumber,
        timestamp = timestamp,
        voterPublicKey = voterPublicKey,
        candidateIndex = candidateIndex,
        verificationProof = verificationProof
    )
}
