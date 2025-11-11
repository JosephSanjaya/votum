import { ApiPromise } from '@polkadot/api';
import { KeyringPair } from '@polkadot/keyring/types';
import { SubmittableExtrinsic } from '@polkadot/api/types';
import { ISubmittableResult } from '@polkadot/types/types';
import { PolkadotConnectionManager } from '@/config/polkadot.config';
import { logger } from '@/utils/logger';
import {
  VoteCastingData,
  VoteVerificationResult,
  BlockchainTransaction,
  TransactionStatus,
} from '@/types/election.types';

export interface ElectionSmartContractInterface {
  createElection(electionData: ElectionCreationData): Promise<string>;
  registerVoter(voterData: VoterRegistrationData): Promise<string>;
  castVote(voteData: VoteCastingData): Promise<string>;
  verifyVote(transactionHash: string): Promise<VoteVerificationResult>;
  getElectionResults(electionId: string): Promise<ElectionResultsData>;
  finalizeElection(electionId: string): Promise<string>;
}

export interface ElectionCreationData {
  readonly title: string;
  readonly description: string;
  readonly startTime: number;
  readonly endTime: number;
  readonly candidates: CandidateData[];
  readonly eligibilityCriteria: string[];
  readonly creatorAddress: string;
}

export interface VoterRegistrationData {
  readonly nationalId: string;
  readonly publicKey: string;
  readonly eligibilityProof: string;
  readonly voterAddress: string;
}

export interface CandidateData {
  readonly name: string;
  readonly party: string;
  readonly description: string;
  readonly manifestoHash: string;
}

export interface ElectionResultsData {
  readonly electionId: string;
  readonly totalVotes: number;
  readonly candidateVotes: number[];
  readonly isFinalized: boolean;
  readonly finalizedAt: number;
}

export class BlockchainElectionService implements ElectionSmartContractInterface {
  private polkadotConnection: PolkadotConnectionManager;
  private contractAddress: string;
  private serviceLogger = logger.child({ service: 'BlockchainElectionService' });

  constructor(
    polkadotConnection: PolkadotConnectionManager,
    contractAddress: string,
  ) {
    this.polkadotConnection = polkadotConnection;
    this.contractAddress = contractAddress;
  }

  public async createElection(electionData: ElectionCreationData): Promise<string> {
    try {
      this.serviceLogger.info('Creating new election on blockchain', {
        title: electionData.title,
        candidatesCount: electionData.candidates.length,
      });

      const api = await this.polkadotConnection.getApiInstance();
      const keyring = this.polkadotConnection.getKeyringInstance();
      const creatorKeyPair = keyring.addFromUri(electionData.creatorAddress);

      const electionCreationExtrinsic = this.buildElectionCreationExtrinsic(api, electionData);
      const transactionHash = await this.submitAndWaitForTransaction(
        electionCreationExtrinsic,
        creatorKeyPair,
      );

      this.serviceLogger.info('Election created successfully', {
        transactionHash,
        electionTitle: electionData.title,
      });

      return transactionHash;
    } catch (electionCreationError) {
      this.serviceLogger.error('Failed to create election', {
        error: electionCreationError,
        electionData,
      });
      throw new Error(`Election creation failed: ${electionCreationError}`);
    }
  }

  public async registerVoter(voterData: VoterRegistrationData): Promise<string> {
    try {
      this.serviceLogger.info('Registering voter on blockchain', {
        nationalId: voterData.nationalId,
        publicKey: voterData.publicKey,
      });

      const api = await this.polkadotConnection.getApiInstance();
      const keyring = this.polkadotConnection.getKeyringInstance();
      const voterKeyPair = keyring.addFromUri(voterData.voterAddress);

      const voterRegistrationExtrinsic = this.buildVoterRegistrationExtrinsic(api, voterData);
      const transactionHash = await this.submitAndWaitForTransaction(
        voterRegistrationExtrinsic,
        voterKeyPair,
      );

      this.serviceLogger.info('Voter registered successfully', {
        transactionHash,
        nationalId: voterData.nationalId,
      });

      return transactionHash;
    } catch (voterRegistrationError) {
      this.serviceLogger.error('Failed to register voter', {
        error: voterRegistrationError,
        voterData,
      });
      throw new Error(`Voter registration failed: ${voterRegistrationError}`);
    }
  }

  public async castVote(voteData: VoteCastingData): Promise<string> {
    try {
      this.serviceLogger.info('Casting vote on blockchain', {
        electionId: voteData.electionId,
        voterId: voteData.voterId,
      });

      const api = await this.polkadotConnection.getApiInstance();
      const keyring = this.polkadotConnection.getKeyringInstance();
      const voterKeyPair = keyring.addFromUri(voteData.voterId);

      const voteCastingExtrinsic = this.buildVoteCastingExtrinsic(api, voteData);
      const transactionHash = await this.submitAndWaitForTransaction(
        voteCastingExtrinsic,
        voterKeyPair,
      );

      this.serviceLogger.info('Vote cast successfully', {
        transactionHash,
        electionId: voteData.electionId,
      });

      return transactionHash;
    } catch (voteCastingError) {
      this.serviceLogger.error('Failed to cast vote', {
        error: voteCastingError,
        voteData,
      });
      throw new Error(`Vote casting failed: ${voteCastingError}`);
    }
  }

  public async verifyVote(transactionHash: string): Promise<VoteVerificationResult> {
    try {
      this.serviceLogger.info('Verifying vote on blockchain', { transactionHash });

      const api = await this.polkadotConnection.getApiInstance();
      const blockchainTransaction = await this.getTransactionDetails(api, transactionHash);

      if (!blockchainTransaction) {
        return {
          isValid: false,
          transactionHash,
          blockNumber: BigInt(0),
          timestamp: new Date(),
          voterPublicKey: '',
          candidateIndex: 0,
          verificationProof: '',
          errorMessage: 'Transaction not found on blockchain',
        };
      }

      const voteVerificationResult = await this.extractVoteDataFromTransaction(
        api,
        blockchainTransaction,
      );

      this.serviceLogger.info('Vote verification completed', {
        transactionHash,
        isValid: voteVerificationResult.isValid,
      });

      return voteVerificationResult;
    } catch (voteVerificationError) {
      this.serviceLogger.error('Failed to verify vote', {
        error: voteVerificationError,
        transactionHash,
      });

      return {
        isValid: false,
        transactionHash,
        blockNumber: BigInt(0),
        timestamp: new Date(),
        voterPublicKey: '',
        candidateIndex: 0,
        verificationProof: '',
        errorMessage: `Verification failed: ${voteVerificationError}`,
      };
    }
  }

  public async getElectionResults(electionId: string): Promise<ElectionResultsData> {
    try {
      this.serviceLogger.info('Fetching election results from blockchain', { electionId });

      const api = await this.polkadotConnection.getApiInstance();
      const electionResultsQuery = await this.queryElectionResults(api, electionId);

      this.serviceLogger.info('Election results fetched successfully', {
        electionId,
        totalVotes: electionResultsQuery.totalVotes,
      });

      return electionResultsQuery;
    } catch (resultsRetrievalError) {
      this.serviceLogger.error('Failed to fetch election results', {
        error: resultsRetrievalError,
        electionId,
      });
      throw new Error(`Election results retrieval failed: ${resultsRetrievalError}`);
    }
  }

  public async finalizeElection(electionId: string): Promise<string> {
    try {
      this.serviceLogger.info('Finalizing election on blockchain', { electionId });

      const api = await this.polkadotConnection.getApiInstance();
      const keyring = this.polkadotConnection.getKeyringInstance();
      const adminKeyPair = keyring.addFromUri('//Alice');

      const electionFinalizationExtrinsic = this.buildElectionFinalizationExtrinsic(
        api,
        electionId,
      );
      const transactionHash = await this.submitAndWaitForTransaction(
        electionFinalizationExtrinsic,
        adminKeyPair,
      );

      this.serviceLogger.info('Election finalized successfully', {
        transactionHash,
        electionId,
      });

      return transactionHash;
    } catch (electionFinalizationError) {
      this.serviceLogger.error('Failed to finalize election', {
        error: electionFinalizationError,
        electionId,
      });
      throw new Error(`Election finalization failed: ${electionFinalizationError}`);
    }
  }

  private buildElectionCreationExtrinsic(
    api: ApiPromise,
    electionData: ElectionCreationData,
  ): SubmittableExtrinsic<'promise'> {
    return (api.tx as any)['electionPallet']['createElection'](
      electionData.title,
      electionData.description,
      electionData.startTime,
      electionData.endTime,
      electionData.candidates.map(candidate => ({
        name: candidate.name,
        party: candidate.party,
        description: candidate.description,
        manifestoHash: candidate.manifestoHash,
      })),
      electionData.eligibilityCriteria,
    );
  }

  private buildVoterRegistrationExtrinsic(
    api: ApiPromise,
    voterData: VoterRegistrationData,
  ): SubmittableExtrinsic<'promise'> {
    return (api.tx as any)['electionPallet']['registerVoter'](
      voterData.nationalId,
      voterData.publicKey,
      voterData.eligibilityProof,
    );
  }

  private buildVoteCastingExtrinsic(
    api: ApiPromise,
    voteData: VoteCastingData,
  ): SubmittableExtrinsic<'promise'> {
    return (api.tx as any)['electionPallet']['castVote'](
      voteData.electionId,
      voteData.candidateId,
      voteData.encryptedVote,
      voteData.voterSignature,
    );
  }

  private buildElectionFinalizationExtrinsic(
    api: ApiPromise,
    electionId: string,
  ): SubmittableExtrinsic<'promise'> {
    return (api.tx as any)['electionPallet']['finalizeElection'](electionId);
  }

  private async submitAndWaitForTransaction(
    extrinsic: SubmittableExtrinsic<'promise'>,
    signerKeyPair: KeyringPair,
  ): Promise<string> {
    return new Promise((resolve, reject) => {
      let transactionHash = '';

      extrinsic
        .signAndSend(signerKeyPair, (result: ISubmittableResult) => {
          transactionHash = result.txHash.toHex();

          if (result.status.isInBlock) {
            this.serviceLogger.debug('Transaction included in block', {
              transactionHash,
              blockHash: result.status.asInBlock.toHex(),
            });
          }

          if (result.status.isFinalized) {
            this.serviceLogger.debug('Transaction finalized', {
              transactionHash,
              blockHash: result.status.asFinalized.toHex(),
            });

            if (result.dispatchError) {
              const errorMessage = this.extractDispatchError(result.dispatchError);
              reject(new Error(`Transaction failed: ${errorMessage}`));
            } else {
              resolve(transactionHash);
            }
          }

          if (result.status.isDropped || result.status.isInvalid) {
            reject(new Error(`Transaction ${result.status.type.toLowerCase()}`));
          }
        })
        .catch((submissionError: Error) => {
          this.serviceLogger.error('Transaction submission failed', {
            error: submissionError,
            transactionHash,
          });
          reject(submissionError);
        });
    });
  }

  private async getTransactionDetails(
    api: ApiPromise,
    transactionHash: string,
  ): Promise<BlockchainTransaction | null> {
    try {
      const blockHash = await api.rpc.chain.getBlockHash();
      const signedBlock = await api.rpc.chain.getBlock(blockHash);
      
      const transactionIndex = signedBlock.block.extrinsics.findIndex(
        extrinsic => extrinsic.hash.toHex() === transactionHash,
      );

      if (transactionIndex === -1) {
        return null;
      }

      const blockHeader = await api.rpc.chain.getHeader(blockHash);
      const blockNumber = blockHeader.number.toBigInt();

      return {
        hash: transactionHash,
        blockNumber,
        blockHash: blockHash.toHex(),
        transactionIndex,
        from: '',
        to: this.contractAddress,
        gasUsed: BigInt(0),
        gasPrice: BigInt(0),
        timestamp: new Date(),
        status: TransactionStatus.CONFIRMED,
        confirmations: 1,
      };
    } catch (transactionDetailsError) {
      this.serviceLogger.error('Failed to get transaction details', {
        error: transactionDetailsError,
        transactionHash,
      });
      return null;
    }
  }

  private async extractVoteDataFromTransaction(
    api: ApiPromise,
    transaction: BlockchainTransaction,
  ): Promise<VoteVerificationResult> {
    try {
      const blockHash = await api.rpc.chain.getBlockHash(transaction.blockNumber);
      const events = await (api.query as any)['system']['events'].at(blockHash);

      const voteEvent = (events as any[]).find((event: any) =>
        event.event.section === 'electionPallet' && event.event.method === 'VoteCast',
      );

      if (!voteEvent) {
        return {
          isValid: false,
          transactionHash: transaction.hash,
          blockNumber: transaction.blockNumber,
          timestamp: transaction.timestamp,
          voterPublicKey: '',
          candidateIndex: 0,
          verificationProof: '',
          errorMessage: 'Vote event not found in transaction',
        };
      }

      const eventData = voteEvent.event.data;
      
      return {
        isValid: true,
        transactionHash: transaction.hash,
        blockNumber: transaction.blockNumber,
        timestamp: transaction.timestamp,
        voterPublicKey: eventData[0]?.toString() || '',
        candidateIndex: parseInt(eventData[2]?.toString() || '0', 10),
        verificationProof: transaction.hash,
      };
    } catch (extractionError) {
      this.serviceLogger.error('Failed to extract vote data from transaction', {
        error: extractionError,
        transactionHash: transaction.hash,
      });

      return {
        isValid: false,
        transactionHash: transaction.hash,
        blockNumber: transaction.blockNumber,
        timestamp: transaction.timestamp,
        voterPublicKey: '',
        candidateIndex: 0,
        verificationProof: '',
        errorMessage: `Data extraction failed: ${extractionError}`,
      };
    }
  }

  private async queryElectionResults(api: ApiPromise, electionId: string): Promise<ElectionResultsData> {
    try {
      const electionResults = await (api.query as any)['electionPallet']['electionResults'](electionId);
      const resultsData = electionResults.toJSON() as any;

      return {
        electionId,
        totalVotes: resultsData.totalVotes || 0,
        candidateVotes: resultsData.candidateVotes || [],
        isFinalized: resultsData.isFinalized || false,
        finalizedAt: resultsData.finalizedAt || 0,
      };
    } catch (queryError) {
      this.serviceLogger.error('Failed to query election results', {
        error: queryError,
        electionId,
      });
      throw new Error(`Election results query failed: ${queryError}`);
    }
  }

  private extractDispatchError(dispatchError: any): string {
    if (dispatchError.isModule) {
      const decoded = dispatchError.asModule;
      const { docs, name, section } = decoded.registry.findMetaError(decoded);
      return `${section}.${name}: ${docs.join(' ')}`;
    } else {
      return dispatchError.toString();
    }
  }
}

export const createBlockchainElectionService = (
  polkadotConnection: PolkadotConnectionManager,
  contractAddress: string,
): BlockchainElectionService => {
  return new BlockchainElectionService(polkadotConnection, contractAddress);
};