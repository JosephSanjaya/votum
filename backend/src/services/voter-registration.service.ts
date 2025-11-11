import { PrismaClient } from '@prisma/client';
import bcrypt from 'bcrypt';
import { Keyring } from '@polkadot/keyring';
import { cryptoWaitReady } from '@polkadot/util-crypto';
import { logger } from '@/utils/logger';
import { BlockchainElectionService } from '@/services/blockchain.service';
import {
  VoterProfile,
  ApiResponse,
  ValidationError,
} from '@/types/election.types';

export interface VoterRegistrationRequest {
  readonly nationalId: string;
  readonly email: string;
  readonly password: string;
  readonly fullName: string;
  readonly dateOfBirth: string;
  readonly address: string;
  readonly phoneNumber: string;
}

export interface VoterVerificationRequest {
  readonly nationalId: string;
  readonly verificationCode: string;
  readonly documentProof: string;
}

export interface VoterLoginRequest {
  readonly email: string;
  readonly password: string;
}

export interface VoterLoginResponse {
  readonly voter: VoterProfile;
  readonly accessToken: string;
  readonly refreshToken: string;
  readonly expiresIn: number;
}

export class VoterRegistrationService {
  private prismaClient: PrismaClient;
  private blockchainService: BlockchainElectionService;
  private keyring: Keyring;
  private serviceLogger = logger.child({ service: 'VoterRegistrationService' });

  constructor(
    prismaClient: PrismaClient,
    blockchainService: BlockchainElectionService,
  ) {
    this.prismaClient = prismaClient;
    this.blockchainService = blockchainService;
    this.keyring = new Keyring({ type: 'sr25519' });
  }

  public async initializeService(): Promise<void> {
    try {
      await cryptoWaitReady();
      this.serviceLogger.info('Voter registration service initialized successfully');
    } catch (initializationError) {
      this.serviceLogger.error('Failed to initialize voter registration service', {
        error: initializationError,
      });
      throw new Error(`Service initialization failed: ${initializationError}`);
    }
  }

  public async registerNewVoter(
    registrationRequest: VoterRegistrationRequest,
  ): Promise<ApiResponse<VoterProfile>> {
    try {
      this.serviceLogger.info('Starting voter registration process', {
        nationalId: registrationRequest.nationalId,
        email: registrationRequest.email,
      });

      const validationErrors = await this.validateRegistrationRequest(registrationRequest);
      if (validationErrors.length > 0) {
        return {
          success: false,
          message: 'Registration validation failed',
          timestamp: new Date(),
          requestId: this.generateRequestId(),
          errors: validationErrors,
        };
      }

      const existingVoterCheck = await this.checkExistingVoter(
        registrationRequest.nationalId,
        registrationRequest.email,
      );
      if (!existingVoterCheck.success) {
        return {
          success: false,
          message: existingVoterCheck.message,
          timestamp: new Date(),
          requestId: this.generateRequestId(),
        };
      }

      const cryptographicKeyPair = await this.generateVoterKeyPair();
      const hashedPassword = await this.hashVoterPassword(registrationRequest.password);

      const databaseVoterRecord = await this.createVoterDatabaseRecord({
        nationalId: registrationRequest.nationalId,
        email: registrationRequest.email,
        passwordHash: hashedPassword,
        fullName: registrationRequest.fullName,
        dateOfBirth: new Date(registrationRequest.dateOfBirth),
        address: registrationRequest.address,
        phoneNumber: registrationRequest.phoneNumber,
        publicKey: cryptographicKeyPair.publicKey,
      });

      const blockchainRegistrationResult = await this.registerVoterOnBlockchain({
        nationalId: registrationRequest.nationalId,
        publicKey: cryptographicKeyPair.publicKey,
        eligibilityProof: this.generateEligibilityProof(registrationRequest),
        voterAddress: cryptographicKeyPair.address,
      });

      await this.createAuditTrailEntry({
        action: 'VOTER_REGISTRATION',
        performedBy: databaseVoterRecord.id,
        details: {
          nationalId: registrationRequest.nationalId,
          blockchainTransactionHash: blockchainRegistrationResult,
        },
        blockchainReference: blockchainRegistrationResult,
      });

      const voterProfile = this.mapDatabaseRecordToVoterProfile(databaseVoterRecord);

      this.serviceLogger.info('Voter registration completed successfully', {
        voterId: databaseVoterRecord.id,
        blockchainTransactionHash: blockchainRegistrationResult,
      });

      return {
        success: true,
        data: voterProfile,
        message: 'Voter registration completed successfully',
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    } catch (registrationError) {
      this.serviceLogger.error('Voter registration failed', {
        error: registrationError,
        registrationRequest,
      });

      return {
        success: false,
        message: `Registration failed: ${registrationError}`,
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    }
  }

  public async verifyVoterIdentity(
    verificationRequest: VoterVerificationRequest,
  ): Promise<ApiResponse<VoterProfile>> {
    try {
      this.serviceLogger.info('Starting voter identity verification', {
        nationalId: verificationRequest.nationalId,
      });

      const voterRecord = await this.prismaClient.user.findUnique({
        where: { nationalId: verificationRequest.nationalId },
      });

      if (!voterRecord) {
        return {
          success: false,
          message: 'Voter not found',
          timestamp: new Date(),
          requestId: this.generateRequestId(),
        };
      }

      const verificationResult = await this.performIdentityVerification(
        voterRecord,
        verificationRequest,
      );

      if (!verificationResult.isValid) {
        return {
          success: false,
          message: verificationResult.errorMessage || 'Identity verification failed',
          timestamp: new Date(),
          requestId: this.generateRequestId(),
        };
      }

      const updatedVoterRecord = await this.prismaClient.user.update({
        where: { id: voterRecord.id },
        data: {
          isVerified: true,
          isEligible: true,
          updatedAt: new Date(),
        },
      });

      await this.createAuditTrailEntry({
        action: 'VOTER_VERIFICATION',
        performedBy: voterRecord.id,
        details: {
          nationalId: verificationRequest.nationalId,
          verificationMethod: 'document_proof',
        },
      });

      const verifiedVoterProfile = this.mapDatabaseRecordToVoterProfile(updatedVoterRecord);

      this.serviceLogger.info('Voter identity verification completed', {
        voterId: voterRecord.id,
      });

      return {
        success: true,
        data: verifiedVoterProfile,
        message: 'Identity verification completed successfully',
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    } catch (verificationError) {
      this.serviceLogger.error('Voter identity verification failed', {
        error: verificationError,
        verificationRequest,
      });

      return {
        success: false,
        message: `Verification failed: ${verificationError}`,
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    }
  }

  public async authenticateVoterLogin(
    loginRequest: VoterLoginRequest,
  ): Promise<ApiResponse<VoterLoginResponse>> {
    try {
      this.serviceLogger.info('Processing voter login', {
        email: loginRequest.email,
      });

      const voterRecord = await this.prismaClient.user.findUnique({
        where: { email: loginRequest.email },
      });

      if (!voterRecord) {
        return {
          success: false,
          message: 'Invalid email or password',
          timestamp: new Date(),
          requestId: this.generateRequestId(),
        };
      }

      const passwordValidationResult = await this.validateVoterPassword(
        loginRequest.password,
        voterRecord.passwordHash,
      );

      if (!passwordValidationResult) {
        await this.createSecurityEvent({
          eventType: 'FAILED_LOGIN',
          severity: 'MEDIUM',
          description: 'Failed login attempt with invalid password',
          userId: voterRecord.id,
        });

        return {
          success: false,
          message: 'Invalid email or password',
          timestamp: new Date(),
          requestId: this.generateRequestId(),
        };
      }

      const authenticationTokens = await this.generateAuthenticationTokens(voterRecord);

      await this.prismaClient.user.update({
        where: { id: voterRecord.id },
        data: { lastLoginDate: new Date() },
      });

      await this.createAuditTrailEntry({
        action: 'SYSTEM_ACCESS',
        performedBy: voterRecord.id,
        details: {
          loginMethod: 'email_password',
          loginTime: new Date(),
        },
      });

      const voterProfile = this.mapDatabaseRecordToVoterProfile(voterRecord);

      this.serviceLogger.info('Voter login successful', {
        voterId: voterRecord.id,
      });

      return {
        success: true,
        data: {
          voter: voterProfile,
          accessToken: authenticationTokens.accessToken,
          refreshToken: authenticationTokens.refreshToken,
          expiresIn: authenticationTokens.expiresIn,
        },
        message: 'Login successful',
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    } catch (loginError) {
      this.serviceLogger.error('Voter login failed', {
        error: loginError,
        email: loginRequest.email,
      });

      return {
        success: false,
        message: `Login failed: ${loginError}`,
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    }
  }

  public async getVoterProfile(voterId: string): Promise<ApiResponse<VoterProfile>> {
    try {
      const voterRecord = await this.prismaClient.user.findUnique({
        where: { id: voterId },
      });

      if (!voterRecord) {
        return {
          success: false,
          message: 'Voter not found',
          timestamp: new Date(),
          requestId: this.generateRequestId(),
        };
      }

      const voterProfile = this.mapDatabaseRecordToVoterProfile(voterRecord);

      return {
        success: true,
        data: voterProfile,
        message: 'Voter profile retrieved successfully',
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    } catch (profileRetrievalError) {
      this.serviceLogger.error('Failed to retrieve voter profile', {
        error: profileRetrievalError,
        voterId,
      });

      return {
        success: false,
        message: `Profile retrieval failed: ${profileRetrievalError}`,
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    }
  }

  private async validateRegistrationRequest(
    request: VoterRegistrationRequest,
  ): Promise<ValidationError[]> {
    const validationErrors: ValidationError[] = [];

    if (!request.nationalId || request.nationalId.length < 10) {
      validationErrors.push({
        field: 'nationalId',
        message: 'National ID must be at least 10 characters long',
        code: 'INVALID_NATIONAL_ID',
      });
    }

    if (!request.email || !this.isValidEmailFormat(request.email)) {
      validationErrors.push({
        field: 'email',
        message: 'Valid email address is required',
        code: 'INVALID_EMAIL',
      });
    }

    if (!request.password || request.password.length < 8) {
      validationErrors.push({
        field: 'password',
        message: 'Password must be at least 8 characters long',
        code: 'WEAK_PASSWORD',
      });
    }

    if (!request.fullName || request.fullName.trim().length < 2) {
      validationErrors.push({
        field: 'fullName',
        message: 'Full name is required',
        code: 'INVALID_NAME',
      });
    }

    const dateOfBirth = new Date(request.dateOfBirth);
    const minimumAge = 18;
    const currentDate = new Date();
    const ageInYears = currentDate.getFullYear() - dateOfBirth.getFullYear();

    if (ageInYears < minimumAge) {
      validationErrors.push({
        field: 'dateOfBirth',
        message: `Voter must be at least ${minimumAge} years old`,
        code: 'UNDERAGE_VOTER',
      });
    }

    return validationErrors;
  }

  private async checkExistingVoter(
    nationalId: string,
    email: string,
  ): Promise<ApiResponse<null>> {
    const existingVoterByNationalId = await this.prismaClient.user.findUnique({
      where: { nationalId },
    });

    if (existingVoterByNationalId) {
      return {
        success: false,
        message: 'Voter with this National ID already exists',
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    }

    const existingVoterByEmail = await this.prismaClient.user.findUnique({
      where: { email },
    });

    if (existingVoterByEmail) {
      return {
        success: false,
        message: 'Voter with this email already exists',
        timestamp: new Date(),
        requestId: this.generateRequestId(),
      };
    }

    return {
      success: true,
      message: 'No existing voter found',
      timestamp: new Date(),
      requestId: this.generateRequestId(),
    };
  }

  private async generateVoterKeyPair(): Promise<{ publicKey: string; address: string }> {
    const keyPair = this.keyring.addFromUri(`//voter_${Date.now()}_${Math.random()}`);
    return {
      publicKey: keyPair.publicKey.toString(),
      address: keyPair.address,
    };
  }

  private async hashVoterPassword(password: string): Promise<string> {
    const saltRounds = parseInt(process.env['BCRYPT_ROUNDS'] || '12', 10);
    return await bcrypt.hash(password, saltRounds);
  }

  private async createVoterDatabaseRecord(voterData: any): Promise<any> {
    return await this.prismaClient.user.create({
      data: voterData,
    });
  }

  private async registerVoterOnBlockchain(voterData: any): Promise<string> {
    return await this.blockchainService.registerVoter(voterData);
  }

  private generateEligibilityProof(registrationRequest: VoterRegistrationRequest): string {
    return `eligibility_proof_${registrationRequest.nationalId}_${Date.now()}`;
  }

  private async createAuditTrailEntry(auditData: any): Promise<void> {
    await this.prismaClient.auditTrailEntry.create({
      data: {
        ...auditData,
        ipAddress: '127.0.0.1',
        userAgent: 'VoterRegistrationService',
      },
    });
  }

  private async createSecurityEvent(securityEventData: any): Promise<void> {
    await this.prismaClient.securityEvent.create({
      data: {
        ...securityEventData,
        ipAddress: '127.0.0.1',
        userAgent: 'VoterRegistrationService',
        metadata: {},
      },
    });
  }

  private mapDatabaseRecordToVoterProfile(databaseRecord: any): VoterProfile {
    return {
      id: databaseRecord.id,
      nationalId: databaseRecord.nationalId,
      email: databaseRecord.email,
      fullName: databaseRecord.fullName,
      isVerified: databaseRecord.isVerified,
      isEligible: databaseRecord.isEligible,
      publicKey: databaseRecord.publicKey,
      registrationDate: databaseRecord.registrationDate,
      lastLoginDate: databaseRecord.lastLoginDate,
    };
  }

  private async performIdentityVerification(
    _voterRecord: any,
    verificationRequest: VoterVerificationRequest,
  ): Promise<{ isValid: boolean; errorMessage?: string }> {
    if (verificationRequest.verificationCode.length < 6) {
      return {
        isValid: false,
        errorMessage: 'Invalid verification code',
      };
    }

    if (!verificationRequest.documentProof) {
      return {
        isValid: false,
        errorMessage: 'Document proof is required',
      };
    }

    return { isValid: true };
  }

  private async validateVoterPassword(
    plainPassword: string,
    hashedPassword: string,
  ): Promise<boolean> {
    return await bcrypt.compare(plainPassword, hashedPassword);
  }

  private async generateAuthenticationTokens(voterRecord: any): Promise<{
    accessToken: string;
    refreshToken: string;
    expiresIn: number;
  }> {
    return {
      accessToken: `access_token_${voterRecord.id}_${Date.now()}`,
      refreshToken: `refresh_token_${voterRecord.id}_${Date.now()}`,
      expiresIn: 3600,
    };
  }

  private isValidEmailFormat(email: string): boolean {
    const emailRegexPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegexPattern.test(email);
  }

  private generateRequestId(): string {
    return `req_${Date.now()}_${Math.random().toString(36).substring(2, 15)}`;
  }
}

export const createVoterRegistrationService = (
  prismaClient: PrismaClient,
  blockchainService: BlockchainElectionService,
): VoterRegistrationService => {
  return new VoterRegistrationService(prismaClient, blockchainService);
};