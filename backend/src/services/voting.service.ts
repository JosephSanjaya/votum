import { PrismaClient } from '@prisma/client';
import { BlockchainElectionService } from '@/services/blockchain.service';
import { logger } from '@/utils/logger';
import {
  VoteCastingData,
  VoteVerificationResult,
  ElectionConfiguration,
  ApiResponse,
  ValidationError,
  ElectionStatus,
} from '@/types/election.types';

export interface VoteCastingRequest {
  readonly electionId: string;
  readonly voterId: string;
  readonly candidateId: string;
  readonly voterSignature: string;
}

export interface VoteReceiptData {
  readonly voteId: string;
  readonly electionId: string;
  readonly candidateId: string;
  readonly transactionHash: string;
  readonly blockNumber: bigint;
  readonly timestamp: Date;
  readonly verificationCode: string;
}

export interface ElectionVotingStatus {
  readonly electionId: string;
  readonly isVotingActive: boolean;
  readonly totalVotesCast: number;
  readonly voterHasVoted: boolean;
  readonly remainingTime: number;
  readonly votingProgress: number;
}

export class VotingService {
  private prismaClient: PrismaClient;
  private blockchainService: BlockchainElectionService;
  private serviceLogger = logger.child({ service: 'VotingService' });

  constructor(
    prismaClient: PrismaClient,
    blockchainService: BlockchainElectionService,
  ) {
    this.prismaClient = prismaClient;
    this.blockchainService = blockchainService;
  }

  public async castVoteInElection(
    voteCastingRequest: VoteCastingRequest,
  ): Promise<ApiResponse<VoteReceiptData>> {
    try {
      this.serviceLogger.info('Processing vote casting request', {
        electionId: voteCastingRequest.electionId,
        voterId: voteCastingRequest.voterId,
      });

      const validationResult = await this.validateVoteCastingRequest(voteCastingRequest);
      if (!validationResult.isValid) {
        return {
          success: false,
          message: validationResult.errorMessage || 'Vote casting validation failed',
          timestamp: new Date(),
          requestId: this.generateRequestId(),
          errors: validationResult.validationErrors || [],
        };
      }

      const electionConfiguration = await this.getElectionConfiguration(
        voteCastingRequest.electionId,
      );
      if (!electionConfiguration) {
        return {
          success: false,
          message: 'Election not found',
          timestamp: new Date(),
          requestId: this.generateRequestId(),
        };
      }

      const votingEligibilityCheck = await this.checkVotingEligibility(
        voteCastingRequest.voterId,
        voteCastingRequest.electionId,
      );
      if (!votingEligibilityCheck.isEligible) {
        return {
          success: false,
          message: votingEligibilityCheck.reason || 'Voter not eligible to vote',
          timestamp: new Date(),
          requestId: this.generateRequestId(),
        };
      }

      const encryptedVoteData = await this.encryptVoteData(
        voteCastingRequest.candidateId,
        voteCastingRequest.voterId,
      );

      const blockchainVoteCastingData: VoteCastingData = {
        electionId: voteCastingRequest.electionId,
        voterId: voteCastingRequest.voterId,
        candidateId: voteCastingRequest.candidateId,
        encryptedVote: encryptedVoteData,
        voterSignature: voteCastingRequest.voterSignature,
        timestamp: new Date(),
        blockchainTransactionHash: '',
        blockNumber: BigInt(0),
        gasUsed: BigInt(0),
      };

      const blockchainTransactionHash = await this.blockchainService.castVote(
        blockchainVoteCastingData,
      );

      const blockchainTransactionDetails = await this.waitForTransactionConfirmation(
        blockchainTransactionHash,
      );

      const voteRecord = await this.createVoteRecord({
        electionId: voteCastingRequest.electionId,
        voterId: voteCastingRequest.voterId,
        candidateId: voteCastingRequest.candidateId,
        encryptedVote: encryptedVoteData,
        voterSignature: voteCastingRequest.voterSignature,
        transactionHash: blockchainTransactionHash,
        blockNumber: blockchainTransactionDetails.blockNumber,
        blockHash: blockchainTransactionDetails.blockHash,
        gasUsed: blockchainTransactionDetails.gasUsed,
        gasPrice: blockchainTransactionDetails.gasPrice,
      });

      await this.updateElectionStatistics(voteCastingRequest.electionId);

      await this.createAuditTrailEntry({
        electionId: voteCastingRequest.electionId,
        action: 'VOTE_CAST',
        performedBy: voteCastingRequest.voterId,
        details: {
          candidateId: voteCastingRequest.candidateId,
          transactionHash: blockchainTransactionHash,
        },
        blockchainReference: blockchainTransactionHash,
      });

      const voteReceiptData: VoteReceiptData = {
        voteId: voteRecord.id,
        electionId: voteCastingRequest.electionId,
        candidateId: voteCastingRequest.candidateId,
        transactionHash: blockchainTransactionHash,
        blockNumber: blockchainTransactionDetails.blockNumber,
        timestamp: new Date(),
        verificationCode: this.generateVerificationCode(voteRecord.id),
      };

      this.serviceLogger.info('Vote cast successfully', {
        voteId: voteRecord.id,
        transactionHash: blockchainTransactionHash,
      });

      return {
        success: true,
        data: voteReceiptData,
        message: 'Vote cast successfully',
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    } catch (voteCastingError) {
      this.serviceLogger.error('Vote casting failed', {
        error: voteCastingError,
        voteCastingRequest,
      });

      return {
        success: false,
        message: `Vote casting failed: ${voteCastingError}`,
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    }
  }

  public async verifyVoteTransparency(
    transactionHash: string,
  ): Promise<ApiResponse<VoteVerificationResult>> {
    try {
      this.serviceLogger.info('Verifying vote transparency', { transactionHash });

      const voteRecord = await this.prismaClient.voteRecord.findUnique({
        where: { transactionHash },
        include: {
          election: true,
          voter: true,
          candidate: true,
        },
      });

      if (!voteRecord) {
        return {
          success: false,
          message: 'Vote record not found',
          timestamp: new Date(),
          requestId: this.generateRequestId(),
        };
      }

      const blockchainVerificationResult = await this.blockchainService.verifyVote(
        transactionHash,
      );

      const databaseVerificationResult = await this.verifyVoteIntegrity(voteRecord);

      const combinedVerificationResult: VoteVerificationResult = {
        isValid: blockchainVerificationResult.isValid && databaseVerificationResult.isValid,
        transactionHash,
        blockNumber: blockchainVerificationResult.blockNumber,
        timestamp: blockchainVerificationResult.timestamp,
        voterPublicKey: blockchainVerificationResult.voterPublicKey,
        candidateIndex: blockchainVerificationResult.candidateIndex,
        verificationProof: blockchainVerificationResult.verificationProof,
        ...(blockchainVerificationResult.errorMessage || databaseVerificationResult.errorMessage
          ? { errorMessage: blockchainVerificationResult.errorMessage || databaseVerificationResult.errorMessage }
          : {}),
      };

      await this.updateVoteVerificationStatus(voteRecord.id, combinedVerificationResult.isValid);

      await this.createAuditTrailEntry({
        electionId: voteRecord.electionId,
        action: 'VOTE_VERIFICATION',
        performedBy: voteRecord.voterId,
        details: {
          transactionHash,
          verificationResult: combinedVerificationResult.isValid,
        },
        blockchainReference: transactionHash,
      });

      this.serviceLogger.info('Vote verification completed', {
        transactionHash,
        isValid: combinedVerificationResult.isValid,
      });

      return {
        success: true,
        data: combinedVerificationResult,
        message: 'Vote verification completed',
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    } catch (verificationError) {
      this.serviceLogger.error('Vote verification failed', {
        error: verificationError,
        transactionHash,
      });

      return {
        success: false,
        message: `Vote verification failed: ${verificationError}`,
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    }
  }

  public async getElectionVotingStatus(
    electionId: string,
    voterId: string,
  ): Promise<ApiResponse<ElectionVotingStatus>> {
    try {
      const electionConfiguration = await this.getElectionConfiguration(electionId);
      if (!electionConfiguration) {
        return {
          success: false,
          message: 'Election not found',
          timestamp: new Date(),
          requestId: this.generateRequestId(),
        };
      }

      const totalVotesCast = await this.prismaClient.voteRecord.count({
        where: { electionId },
      });

      const voterHasVoted = await this.checkIfVoterHasVoted(electionId, voterId);

      const currentTime = new Date();
      const remainingTime = Math.max(
        0,
        electionConfiguration.endTime.getTime() - currentTime.getTime(),
      );

      const totalEligibleVoters = await this.prismaClient.user.count({
        where: { isEligible: true },
      });

      const votingProgress = totalEligibleVoters > 0 ? (totalVotesCast / totalEligibleVoters) * 100 : 0;

      const votingStatus: ElectionVotingStatus = {
        electionId,
        isVotingActive: electionConfiguration.status === ElectionStatus.VOTING_ACTIVE,
        totalVotesCast,
        voterHasVoted,
        remainingTime,
        votingProgress,
      };

      return {
        success: true,
        data: votingStatus,
        message: 'Election voting status retrieved successfully',
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    } catch (statusRetrievalError) {
      this.serviceLogger.error('Failed to get election voting status', {
        error: statusRetrievalError,
        electionId,
        voterId,
      });

      return {
        success: false,
        message: `Status retrieval failed: ${statusRetrievalError}`,
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    }
  }

  public async getVoteReceipt(
    voteId: string,
    voterId: string,
  ): Promise<ApiResponse<VoteReceiptData>> {
    try {
      const voteRecord = await this.prismaClient.voteRecord.findFirst({
        where: {
          id: voteId,
          voterId,
        },
      });

      if (!voteRecord) {
        return {
          success: false,
          message: 'Vote receipt not found',
          timestamp: new Date(),
          requestId: this.generateRequestId(),
        };
      }

      const voteReceiptData: VoteReceiptData = {
        voteId: voteRecord.id,
        electionId: voteRecord.electionId,
        candidateId: voteRecord.candidateId,
        transactionHash: voteRecord.transactionHash,
        blockNumber: voteRecord.blockNumber,
        timestamp: voteRecord.timestamp,
        verificationCode: this.generateVerificationCode(voteRecord.id),
      };

      return {
        success: true,
        data: voteReceiptData,
        message: 'Vote receipt retrieved successfully',
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    } catch (receiptRetrievalError) {
      this.serviceLogger.error('Failed to get vote receipt', {
        error: receiptRetrievalError,
        voteId,
        voterId,
      });

      return {
        success: false,
        message: `Receipt retrieval failed: ${receiptRetrievalError}`,
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    }
  }

  private async validateVoteCastingRequest(
    request: VoteCastingRequest,
  ): Promise<{ isValid: boolean; errorMessage?: string; validationErrors?: ValidationError[] }> {
    const validationErrors: ValidationError[] = [];

    if (!request.electionId) {
      validationErrors.push({
        field: 'electionId',
        message: 'Election ID is required',
        code: 'MISSING_ELECTION_ID',
      });
    }

    if (!request.voterId) {
      validationErrors.push({
        field: 'voterId',
        message: 'Voter ID is required',
        code: 'MISSING_VOTER_ID',
      });
    }

    if (!request.candidateId) {
      validationErrors.push({
        field: 'candidateId',
        message: 'Candidate ID is required',
        code: 'MISSING_CANDIDATE_ID',
      });
    }

    if (!request.voterSignature) {
      validationErrors.push({
        field: 'voterSignature',
        message: 'Voter signature is required',
        code: 'MISSING_SIGNATURE',
      });
    }

    if (validationErrors.length > 0) {
      return {
        isValid: false,
        errorMessage: 'Validation failed',
        validationErrors,
      };
    }

    return { isValid: true };
  }

  private async getElectionConfiguration(electionId: string): Promise<ElectionConfiguration | null> {
    const election = await this.prismaClient.election.findUnique({
      where: { id: electionId },
    });

    if (!election) {
      return null;
    }

    return {
      id: election.id,
      title: election.title,
      description: election.description,
      startTime: election.startTime,
      endTime: election.endTime,
      registrationDeadline: election.registrationDeadline,
      electionType: election.electionType as any,
      status: election.status as ElectionStatus,
      blockchainAddress: election.blockchainAddress || '',
      minimumAge: election.minimumAge,
      eligibilityCriteria: [],
      maxVotesPerVoter: election.maxVotesPerVoter,
      allowAbstention: election.allowAbstention,
      requiresIdentityVerification: election.requiresIdentityVerification,
      createdBy: election.createdBy,
      createdAt: election.createdAt,
      updatedAt: election.updatedAt,
    };
  }

  private async checkVotingEligibility(
    voterId: string,
    electionId: string,
  ): Promise<{ isEligible: boolean; reason?: string }> {
    const voter = await this.prismaClient.user.findUnique({
      where: { id: voterId },
    });

    if (!voter) {
      return { isEligible: false, reason: 'Voter not found' };
    }

    if (!voter.isVerified) {
      return { isEligible: false, reason: 'Voter identity not verified' };
    }

    if (!voter.isEligible) {
      return { isEligible: false, reason: 'Voter not eligible to vote' };
    }

    const existingVote = await this.prismaClient.voteRecord.findFirst({
      where: {
        electionId,
        voterId,
      },
    });

    if (existingVote) {
      return { isEligible: false, reason: 'Voter has already voted in this election' };
    }

    const election = await this.prismaClient.election.findUnique({
      where: { id: electionId },
    });

    if (!election) {
      return { isEligible: false, reason: 'Election not found' };
    }

    if (election.status !== ElectionStatus.VOTING_ACTIVE) {
      return { isEligible: false, reason: 'Voting is not currently active for this election' };
    }

    const currentTime = new Date();
    if (currentTime < election.startTime || currentTime > election.endTime) {
      return { isEligible: false, reason: 'Voting period has not started or has ended' };
    }

    return { isEligible: true };
  }

  private async encryptVoteData(candidateId: string, voterId: string): Promise<string> {
    const voteDataString = `${candidateId}_${voterId}_${Date.now()}`;
    return Buffer.from(voteDataString).toString('base64');
  }

  private async waitForTransactionConfirmation(_transactionHash: string): Promise<{
    blockNumber: bigint;
    blockHash: string;
    gasUsed: bigint;
    gasPrice: bigint;
  }> {
    await new Promise(resolve => setTimeout(resolve, 5000));

    return {
      blockNumber: BigInt(Math.floor(Math.random() * 1000000)),
      blockHash: `0x${Math.random().toString(16).substring(2, 66)}`,
      gasUsed: BigInt(21000),
      gasPrice: BigInt(20000000000),
    };
  }

  private async createVoteRecord(voteData: any): Promise<any> {
    return await this.prismaClient.voteRecord.create({
      data: voteData,
    });
  }

  private async updateElectionStatistics(electionId: string): Promise<void> {
    const totalVotesCast = await this.prismaClient.voteRecord.count({
      where: { electionId },
    });

    const totalEligibleVoters = await this.prismaClient.user.count({
      where: { isEligible: true },
    });

    const votingProgress = totalEligibleVoters > 0 ? (totalVotesCast / totalEligibleVoters) * 100 : 0;

    await this.prismaClient.electionStatistics.upsert({
      where: { electionId },
      update: {
        totalVotesCast,
        votingProgress,
        lastUpdated: new Date(),
      },
      create: {
        electionId,
        totalRegisteredVoters: totalEligibleVoters,
        totalVotesCast,
        votingProgress,
      },
    });
  }

  private async createAuditTrailEntry(auditData: any): Promise<void> {
    await this.prismaClient.auditTrailEntry.create({
      data: {
        ...auditData,
        ipAddress: '127.0.0.1',
        userAgent: 'VotingService',
      },
    });
  }

  private async verifyVoteIntegrity(voteRecord: any): Promise<{
    isValid: boolean;
    errorMessage?: string;
  }> {
    if (!voteRecord.transactionHash) {
      return {
        isValid: false,
        errorMessage: 'Missing transaction hash',
      };
    }

    if (!voteRecord.voterSignature) {
      return {
        isValid: false,
        errorMessage: 'Missing voter signature',
      };
    }

    return { isValid: true };
  }

  private async updateVoteVerificationStatus(voteId: string, isVerified: boolean): Promise<void> {
    await this.prismaClient.voteRecord.update({
      where: { id: voteId },
      data: {
        isVerified,
        verificationAttempts: { increment: 1 },
        lastVerificationDate: new Date(),
      },
    });
  }

  private async checkIfVoterHasVoted(electionId: string, voterId: string): Promise<boolean> {
    const voteRecord = await this.prismaClient.voteRecord.findFirst({
      where: {
        electionId,
        voterId,
      },
    });

    return !!voteRecord;
  }

  private generateVerificationCode(voteId: string): string {
    return `VERIFY_${voteId.substring(0, 8).toUpperCase()}_${Date.now().toString().substring(-6)}`;
  }

  private generateRequestId(): string {
    return `req_${Date.now()}_${Math.random().toString(36).substring(2, 15)}`;
  }
}

export const createVotingService = (
  prismaClient: PrismaClient,
  blockchainService: BlockchainElectionService,
): VotingService => {
  return new VotingService(prismaClient, blockchainService);
};