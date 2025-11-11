import { PrismaClient } from '@prisma/client';
import { BlockchainElectionService } from '@/services/blockchain.service';
import { logger } from '@/utils/logger';
import {
  ElectionResults,
  CandidateResult,
  HourlyVotingData,
  DemographicData,
  RegionResult,
  ApiResponse,
  ElectionStatus,
} from '@/types/election.types';

export interface ElectionResultsCalculationRequest {
  readonly electionId: string;
  readonly requestedBy: string;
}

export interface LiveElectionResults {
  readonly electionId: string;
  readonly currentResults: ElectionResults;
  readonly lastUpdated: Date;
  readonly isLive: boolean;
  readonly updateInterval: number;
}

export interface ElectionAnalytics {
  readonly electionId: string;
  readonly totalParticipation: number;
  readonly demographicBreakdown: DemographicData[];
  readonly hourlyVotingTrends: HourlyVotingData[];
  readonly regionalResults: RegionResult[];
  readonly voterTurnoutByHour: number[];
  readonly candidatePerformanceMetrics: CandidatePerformanceMetric[];
}

export interface CandidatePerformanceMetric {
  readonly candidateId: string;
  readonly candidateName: string;
  readonly totalVotes: number;
  readonly votePercentage: number;
  readonly leadMargin: number;
  readonly strongholdRegions: string[];
  readonly weakestRegions: string[];
}

export class ElectionResultsService {
  private prismaClient: PrismaClient;
  private blockchainService: BlockchainElectionService;
  private serviceLogger = logger.child({ service: 'ElectionResultsService' });

  constructor(
    prismaClient: PrismaClient,
    blockchainService: BlockchainElectionService,
  ) {
    this.prismaClient = prismaClient;
    this.blockchainService = blockchainService;
  }

  public async calculateElectionResults(
    calculationRequest: ElectionResultsCalculationRequest,
  ): Promise<ApiResponse<ElectionResults>> {
    try {
      this.serviceLogger.info('Starting election results calculation', {
        electionId: calculationRequest.electionId,
        requestedBy: calculationRequest.requestedBy,
      });

      const electionConfiguration = await this.getElectionConfiguration(
        calculationRequest.electionId,
      );
      if (!electionConfiguration) {
        return {
          success: false,
          message: 'Election not found',
          timestamp: new Date(),
          requestId: this.generateRequestId(),
        };
      }

      const allVoteRecords = await this.getAllVerifiedVoteRecords(calculationRequest.electionId);
      const candidateVoteCounts = await this.aggregateVotesByCandidate(
        calculationRequest.electionId,
        allVoteRecords,
      );

      const totalVotesCast = allVoteRecords.length;
      const totalEligibleVoters = await this.getTotalEligibleVoters();
      const turnoutPercentage = totalEligibleVoters > 0 ? (totalVotesCast / totalEligibleVoters) * 100 : 0;

      const candidateResults = await this.calculateCandidateResults(
        candidateVoteCounts,
        totalVotesCast,
      );

      const winningCandidate = this.determineWinningCandidate(candidateResults);

      await this.blockchainService.getElectionResults(
        calculationRequest.electionId,
      );

      const blockchainProof = await this.generateBlockchainProof(
        calculationRequest.electionId,
        candidateResults,
      );

      const electionResults: ElectionResults = {
        electionId: calculationRequest.electionId,
        totalVotesCast,
        totalEligibleVoters,
        turnoutPercentage,
        candidateResults,
        winningCandidate: winningCandidate?.candidateId || null,
        isFinalized: false,
        finalizedAt: null,
        blockchainProof,
      };

      await this.storeElectionResults(electionResults);

      await this.createAuditTrailEntry({
        electionId: calculationRequest.electionId,
        action: 'RESULTS_CALCULATION',
        performedBy: calculationRequest.requestedBy,
        details: {
          totalVotesCast,
          winningCandidate: winningCandidate?.candidateId,
          turnoutPercentage,
        },
        blockchainReference: blockchainProof,
      });

      this.serviceLogger.info('Election results calculated successfully', {
        electionId: calculationRequest.electionId,
        totalVotesCast,
        winningCandidate: winningCandidate?.candidateId,
      });

      return {
        success: true,
        data: electionResults,
        message: 'Election results calculated successfully',
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    } catch (calculationError) {
      this.serviceLogger.error('Election results calculation failed', {
        error: calculationError,
        calculationRequest,
      });

      return {
        success: false,
        message: `Results calculation failed: ${calculationError}`,
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    }
  }

  public async getLiveElectionResults(electionId: string): Promise<ApiResponse<LiveElectionResults>> {
    try {
      this.serviceLogger.info('Fetching live election results', { electionId });

      const currentResults = await this.calculateElectionResults({
        electionId,
        requestedBy: 'system',
      });

      if (!currentResults.success || !currentResults.data) {
        return {
          success: false,
          message: 'Failed to calculate current results',
          timestamp: new Date(),
          requestId: this.generateRequestId(),
        };
      }

      const liveResults: LiveElectionResults = {
        electionId,
        currentResults: currentResults.data,
        lastUpdated: new Date(),
        isLive: true,
        updateInterval: 30000,
      };

      return {
        success: true,
        data: liveResults,
        message: 'Live election results retrieved successfully',
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    } catch (liveResultsError) {
      this.serviceLogger.error('Failed to get live election results', {
        error: liveResultsError,
        electionId,
      });

      return {
        success: false,
        message: `Live results retrieval failed: ${liveResultsError}`,
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    }
  }

  public async finalizeElectionResults(
    electionId: string,
    finalizedBy: string,
  ): Promise<ApiResponse<ElectionResults>> {
    try {
      this.serviceLogger.info('Finalizing election results', {
        electionId,
        finalizedBy,
      });

      const existingResults = await this.prismaClient.electionResult.findUnique({
        where: { electionId },
        include: { candidateResults: true },
      });

      if (!existingResults) {
        return {
          success: false,
          message: 'Election results not found',
          timestamp: new Date(),
          requestId: this.generateRequestId(),
        };
      }

      if (existingResults.isFinalized) {
        return {
          success: false,
          message: 'Election results are already finalized',
          timestamp: new Date(),
          requestId: this.generateRequestId(),
        };
      }

      const blockchainFinalizationHash = await this.blockchainService.finalizeElection(electionId);

      const finalizedResults = await this.prismaClient.electionResult.update({
        where: { electionId },
        data: {
          isFinalized: true,
          finalizedAt: new Date(),
          blockchainProof: blockchainFinalizationHash,
        },
        include: { candidateResults: true },
      });

      await this.prismaClient.election.update({
        where: { id: electionId },
        data: { status: ElectionStatus.RESULTS_PUBLISHED },
      });

      await this.createAuditTrailEntry({
        electionId,
        action: 'RESULTS_PUBLICATION',
        performedBy: finalizedBy,
        details: {
          finalizationTime: new Date(),
          blockchainHash: blockchainFinalizationHash,
        },
        blockchainReference: blockchainFinalizationHash,
      });

      const mappedResults = this.mapDatabaseResultsToElectionResults(finalizedResults);

      this.serviceLogger.info('Election results finalized successfully', {
        electionId,
        blockchainHash: blockchainFinalizationHash,
      });

      return {
        success: true,
        data: mappedResults,
        message: 'Election results finalized successfully',
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    } catch (finalizationError) {
      this.serviceLogger.error('Election results finalization failed', {
        error: finalizationError,
        electionId,
        finalizedBy,
      });

      return {
        success: false,
        message: `Results finalization failed: ${finalizationError}`,
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    }
  }

  public async getElectionAnalytics(electionId: string): Promise<ApiResponse<ElectionAnalytics>> {
    try {
      this.serviceLogger.info('Generating election analytics', { electionId });

      const electionStatistics = await this.prismaClient.electionStatistics.findUnique({
        where: { electionId },
        include: {
          hourlyVotingData: true,
          demographicData: true,
          regionResults: true,
        },
      });

      if (!electionStatistics) {
        return {
          success: false,
          message: 'Election statistics not found',
          timestamp: new Date(),
          requestId: this.generateRequestId(),
        };
      }

      const candidatePerformanceMetrics = await this.calculateCandidatePerformanceMetrics(
        electionId,
      );

      const voterTurnoutByHour = await this.calculateHourlyTurnoutTrends(electionId);

      const electionAnalytics: ElectionAnalytics = {
        electionId,
        totalParticipation: electionStatistics.votingProgress,
        demographicBreakdown: electionStatistics.demographicData.map(this.mapDemographicData),
        hourlyVotingTrends: electionStatistics.hourlyVotingData.map(this.mapHourlyVotingData),
        regionalResults: electionStatistics.regionResults.map(this.mapRegionResult),
        voterTurnoutByHour,
        candidatePerformanceMetrics,
      };

      return {
        success: true,
        data: electionAnalytics,
        message: 'Election analytics generated successfully',
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    } catch (analyticsError) {
      this.serviceLogger.error('Election analytics generation failed', {
        error: analyticsError,
        electionId,
      });

      return {
        success: false,
        message: `Analytics generation failed: ${analyticsError}`,
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    }
  }

  public async getElectionResults(electionId: string): Promise<ApiResponse<ElectionResults>> {
    try {
      const electionResults = await this.prismaClient.electionResult.findUnique({
        where: { electionId },
        include: { candidateResults: true },
      });

      if (!electionResults) {
        return {
          success: false,
          message: 'Election results not found',
          timestamp: new Date(),
          requestId: this.generateRequestId(),
        };
      }

      const mappedResults = this.mapDatabaseResultsToElectionResults(electionResults);

      return {
        success: true,
        data: mappedResults,
        message: 'Election results retrieved successfully',
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    } catch (retrievalError) {
      this.serviceLogger.error('Election results retrieval failed', {
        error: retrievalError,
        electionId,
      });

      return {
        success: false,
        message: `Results retrieval failed: ${retrievalError}`,
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    }
  }

  private async getElectionConfiguration(electionId: string): Promise<any> {
    return await this.prismaClient.election.findUnique({
      where: { id: electionId },
    });
  }

  private async getAllVerifiedVoteRecords(electionId: string): Promise<any[]> {
    return await this.prismaClient.voteRecord.findMany({
      where: {
        electionId,
        isVerified: true,
      },
      include: {
        candidate: true,
        voter: true,
      },
    });
  }

  private async aggregateVotesByCandidate(
    _electionId: string,
    voteRecords: any[],
  ): Promise<Map<string, number>> {
    const candidateVoteCounts = new Map<string, number>();

    for (const voteRecord of voteRecords) {
      const candidateId = voteRecord.candidateId;
      const currentCount = candidateVoteCounts.get(candidateId) || 0;
      candidateVoteCounts.set(candidateId, currentCount + 1);
    }

    return candidateVoteCounts;
  }

  private async getTotalEligibleVoters(): Promise<number> {
    return await this.prismaClient.user.count({
      where: { isEligible: true },
    });
  }

  private async calculateCandidateResults(
    candidateVoteCounts: Map<string, number>,
    totalVotesCast: number,
  ): Promise<CandidateResult[]> {
    const candidateResults: CandidateResult[] = [];

    for (const [candidateId, voteCount] of candidateVoteCounts.entries()) {
      const candidate = await this.prismaClient.candidate.findUnique({
        where: { id: candidateId },
      });

      if (candidate) {
        const votePercentage = totalVotesCast > 0 ? (voteCount / totalVotesCast) * 100 : 0;

        candidateResults.push({
          candidateId,
          candidateName: candidate.name,
          voteCount,
          votePercentage,
          isWinner: false,
        });
      }
    }

    const sortedResults = candidateResults.sort((a, b) => b.voteCount - a.voteCount);
    if (sortedResults.length > 0 && sortedResults[0]) {
      const winnerResult = sortedResults[0];
      const updatedWinner: CandidateResult = {
        ...winnerResult,
        isWinner: true,
      };
      sortedResults[0] = updatedWinner;
    }

    return sortedResults;
  }

  private determineWinningCandidate(candidateResults: CandidateResult[]): CandidateResult | null {
    if (candidateResults.length === 0) {
      return null;
    }

    return candidateResults.reduce((winner, candidate) =>
      candidate.voteCount > winner.voteCount ? candidate : winner,
    );
  }

  private async generateBlockchainProof(
    electionId: string,
    candidateResults: CandidateResult[],
  ): Promise<string> {
    const proofData = {
      electionId,
      timestamp: Date.now(),
      candidateResults: candidateResults.map(result => ({
        candidateId: result.candidateId,
        voteCount: result.voteCount,
      })),
    };

    return `proof_${electionId}_${Date.now()}_${JSON.stringify(proofData).length}`;
  }

  private async storeElectionResults(electionResults: ElectionResults): Promise<void> {
    await this.prismaClient.electionResult.upsert({
      where: { electionId: electionResults.electionId },
      update: {
        totalVotesCast: electionResults.totalVotesCast,
        totalEligibleVoters: electionResults.totalEligibleVoters,
        turnoutPercentage: electionResults.turnoutPercentage,
        winningCandidateId: electionResults.winningCandidate,
        blockchainProof: electionResults.blockchainProof,
        calculatedAt: new Date(),
      },
      create: {
        electionId: electionResults.electionId,
        totalVotesCast: electionResults.totalVotesCast,
        totalEligibleVoters: electionResults.totalEligibleVoters,
        turnoutPercentage: electionResults.turnoutPercentage,
        winningCandidateId: electionResults.winningCandidate,
        blockchainProof: electionResults.blockchainProof,
      },
    });

    for (const candidateResult of electionResults.candidateResults) {
      await this.prismaClient.candidateResult.upsert({
        where: {
          electionResultId_candidateId: {
            electionResultId: electionResults.electionId,
            candidateId: candidateResult.candidateId,
          },
        },
        update: {
          voteCount: candidateResult.voteCount,
          votePercentage: candidateResult.votePercentage,
          isWinner: candidateResult.isWinner,
        },
        create: {
          electionResultId: electionResults.electionId,
          candidateId: candidateResult.candidateId,
          voteCount: candidateResult.voteCount,
          votePercentage: candidateResult.votePercentage,
          isWinner: candidateResult.isWinner,
        },
      });
    }
  }

  private async createAuditTrailEntry(auditData: any): Promise<void> {
    await this.prismaClient.auditTrailEntry.create({
      data: {
        ...auditData,
        ipAddress: '127.0.0.1',
        userAgent: 'ElectionResultsService',
      },
    });
  }

  private async calculateCandidatePerformanceMetrics(
    electionId: string,
  ): Promise<CandidatePerformanceMetric[]> {
    const candidates = await this.prismaClient.candidate.findMany({
      where: { electionId },
      include: {
        voteRecords: true,
      },
    });

    return candidates.map((candidate: any) => ({
      candidateId: candidate.id,
      candidateName: candidate.name,
      totalVotes: candidate.voteRecords.length,
      votePercentage: 0,
      leadMargin: 0,
      strongholdRegions: [],
      weakestRegions: [],
    }));
  }

  private async calculateHourlyTurnoutTrends(electionId: string): Promise<number[]> {
    const hourlyData = await this.prismaClient.hourlyVotingData.findMany({
      where: {
        electionStatistics: {
          electionId,
        },
      },
      orderBy: { hour: 'asc' },
    });

    return hourlyData.map((data: any) => data.voteCount);
  }

  private mapDatabaseResultsToElectionResults(databaseResults: any): ElectionResults {
    return {
      electionId: databaseResults.electionId,
      totalVotesCast: databaseResults.totalVotesCast,
      totalEligibleVoters: databaseResults.totalEligibleVoters,
      turnoutPercentage: databaseResults.turnoutPercentage,
      candidateResults: databaseResults.candidateResults.map((result: any) => ({
        candidateId: result.candidateId,
        candidateName: result.candidate?.name || 'Unknown',
        voteCount: result.voteCount,
        votePercentage: result.votePercentage,
        isWinner: result.isWinner,
      })),
      winningCandidate: databaseResults.winningCandidateId,
      isFinalized: databaseResults.isFinalized,
      finalizedAt: databaseResults.finalizedAt,
      blockchainProof: databaseResults.blockchainProof,
    };
  }

  private mapDemographicData = (data: any): DemographicData => ({
    category: data.category,
    subcategory: data.subcategory,
    voterCount: data.voterCount,
    percentage: data.percentage,
  });

  private mapHourlyVotingData = (data: any): HourlyVotingData => ({
    hour: data.hour,
    voteCount: data.voteCount,
    cumulativeVotes: data.cumulativeVotes,
  });

  private mapRegionResult = (data: any): RegionResult => ({
    regionId: data.regionId,
    regionName: data.regionName,
    totalVotes: data.totalVotes,
    candidateResults: [],
    turnoutPercentage: data.turnoutPercentage,
  });

  private generateRequestId(): string {
    return `req_${Date.now()}_${Math.random().toString(36).substring(2, 15)}`;
  }
}

export const createElectionResultsService = (
  prismaClient: PrismaClient,
  blockchainService: BlockchainElectionService,
): ElectionResultsService => {
  return new ElectionResultsService(prismaClient, blockchainService);
};