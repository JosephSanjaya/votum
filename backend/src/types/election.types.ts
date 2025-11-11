export interface VoterRegistrationData {
  readonly nationalId: string;
  readonly email: string;
  readonly fullName: string;
  readonly dateOfBirth: Date;
  readonly address: string;
  readonly phoneNumber: string;
  readonly publicKey: string;
}

export interface VoterProfile {
  readonly id: string;
  readonly nationalId: string;
  readonly email: string;
  readonly fullName: string;
  readonly isVerified: boolean;
  readonly isEligible: boolean;
  readonly publicKey: string;
  readonly registrationDate: Date;
  readonly lastLoginDate: Date | null;
}

export interface ElectionCandidate {
  readonly id: string;
  readonly electionId: string;
  readonly name: string;
  readonly party: string;
  readonly description: string;
  readonly imageUrl: string;
  readonly blockchainIndex: number;
  readonly manifesto: string;
  readonly qualifications: string[];
}

export interface ElectionConfiguration {
  readonly id: string;
  readonly title: string;
  readonly description: string;
  readonly startTime: Date;
  readonly endTime: Date;
  readonly registrationDeadline: Date;
  readonly electionType: ElectionType;
  readonly status: ElectionStatus;
  readonly blockchainAddress: string;
  readonly minimumAge: number;
  readonly eligibilityCriteria: string[];
  readonly maxVotesPerVoter: number;
  readonly allowAbstention: boolean;
  readonly requiresIdentityVerification: boolean;
  readonly createdBy: string;
  readonly createdAt: Date;
  readonly updatedAt: Date;
}

export interface VoteCastingData {
  readonly electionId: string;
  readonly voterId: string;
  readonly candidateId: string;
  readonly encryptedVote: string;
  readonly voterSignature: string;
  readonly timestamp: Date;
  readonly blockchainTransactionHash: string;
  readonly blockNumber: bigint;
  readonly gasUsed: bigint;
}

export interface VoteVerificationResult {
  readonly isValid: boolean;
  readonly transactionHash: string;
  readonly blockNumber: bigint;
  readonly timestamp: Date;
  readonly voterPublicKey: string;
  readonly candidateIndex: number;
  readonly verificationProof: string;
  readonly errorMessage?: string;
}

export interface ElectionResults {
  readonly electionId: string;
  readonly totalVotesCast: number;
  readonly totalEligibleVoters: number;
  readonly turnoutPercentage: number;
  readonly candidateResults: CandidateResult[];
  readonly winningCandidate: string | null;
  readonly isFinalized: boolean;
  readonly finalizedAt: Date | null;
  readonly blockchainProof: string;
}

export interface CandidateResult {
  readonly candidateId: string;
  readonly candidateName: string;
  readonly voteCount: number;
  readonly votePercentage: number;
  readonly isWinner: boolean;
}

export interface BlockchainTransaction {
  readonly hash: string;
  readonly blockNumber: bigint;
  readonly blockHash: string;
  readonly transactionIndex: number;
  readonly from: string;
  readonly to: string;
  readonly gasUsed: bigint;
  readonly gasPrice: bigint;
  readonly timestamp: Date;
  readonly status: TransactionStatus;
  readonly confirmations: number;
}

export interface AuditTrailEntry {
  readonly id: string;
  readonly electionId: string;
  readonly action: AuditAction;
  readonly performedBy: string;
  readonly timestamp: Date;
  readonly details: Record<string, unknown>;
  readonly blockchainReference: string;
  readonly ipAddress: string;
  readonly userAgent: string;
}

export interface ElectionStatistics {
  readonly electionId: string;
  readonly totalRegisteredVoters: number;
  readonly totalVotesCast: number;
  readonly votingProgress: number;
  readonly hourlyVotingRate: HourlyVotingData[];
  readonly demographicBreakdown: DemographicData[];
  readonly regionWiseResults: RegionResult[];
  readonly lastUpdated: Date;
}

export interface HourlyVotingData {
  readonly hour: number;
  readonly voteCount: number;
  readonly cumulativeVotes: number;
}

export interface DemographicData {
  readonly category: string;
  readonly subcategory: string;
  readonly voterCount: number;
  readonly percentage: number;
}

export interface RegionResult {
  readonly regionId: string;
  readonly regionName: string;
  readonly totalVotes: number;
  readonly candidateResults: CandidateResult[];
  readonly turnoutPercentage: number;
}

export enum ElectionType {
  PRESIDENTIAL = 'PRESIDENTIAL',
  PARLIAMENTARY = 'PARLIAMENTARY',
  LOCAL_GOVERNMENT = 'LOCAL_GOVERNMENT',
  REFERENDUM = 'REFERENDUM',
  PARTY_PRIMARY = 'PARTY_PRIMARY',
}

export enum ElectionStatus {
  DRAFT = 'DRAFT',
  REGISTRATION_OPEN = 'REGISTRATION_OPEN',
  REGISTRATION_CLOSED = 'REGISTRATION_CLOSED',
  VOTING_ACTIVE = 'VOTING_ACTIVE',
  VOTING_ENDED = 'VOTING_ENDED',
  RESULTS_PUBLISHED = 'RESULTS_PUBLISHED',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
}

export enum TransactionStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  FAILED = 'FAILED',
  REVERTED = 'REVERTED',
}

export enum AuditAction {
  VOTER_REGISTRATION = 'VOTER_REGISTRATION',
  VOTER_VERIFICATION = 'VOTER_VERIFICATION',
  ELECTION_CREATION = 'ELECTION_CREATION',
  ELECTION_UPDATE = 'ELECTION_UPDATE',
  VOTE_CAST = 'VOTE_CAST',
  VOTE_VERIFICATION = 'VOTE_VERIFICATION',
  RESULTS_CALCULATION = 'RESULTS_CALCULATION',
  RESULTS_PUBLICATION = 'RESULTS_PUBLICATION',
  SYSTEM_ACCESS = 'SYSTEM_ACCESS',
  SECURITY_EVENT = 'SECURITY_EVENT',
}

export interface ApiResponse<T> {
  readonly success: boolean;
  readonly data?: T;
  readonly message: string;
  readonly timestamp: Date;
  readonly requestId: string;
  readonly errors?: ValidationError[];
}

export interface ValidationError {
  readonly field: string;
  readonly message: string;
  readonly code: string;
}

export interface PaginationParams {
  readonly page: number;
  readonly limit: number;
  readonly sortBy?: string;
  readonly sortOrder?: 'asc' | 'desc';
}

export interface PaginatedResponse<T> {
  readonly data: T[];
  readonly pagination: {
    readonly currentPage: number;
    readonly totalPages: number;
    readonly totalItems: number;
    readonly itemsPerPage: number;
    readonly hasNextPage: boolean;
    readonly hasPreviousPage: boolean;
  };
}