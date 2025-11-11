import { Router, Request, Response } from 'express';
import { ApplicationDependencies } from '@/index';
import { logger } from '@/utils/logger';
import {
  VoterRegistrationRequest,
  VoterVerificationRequest,
  VoterLoginRequest,
} from '@/services/voter-registration.service';

const authenticationLogger = logger.child({ service: 'AuthenticationRoutes' });

export function authenticationRoutes(dependencies: ApplicationDependencies): Router {
  const authRouter = Router();
  const { voterRegistrationService } = dependencies;

  authRouter.post('/register', async (req: Request, res: Response): Promise<void> => {
    try {
      authenticationLogger.info('Processing voter registration request', {
        email: req.body.email,
        nationalId: req.body.nationalId,
      });

      const registrationRequest: VoterRegistrationRequest = {
        nationalId: req.body.nationalId,
        email: req.body.email,
        password: req.body.password,
        fullName: req.body.fullName,
        dateOfBirth: req.body.dateOfBirth,
        address: req.body.address,
        phoneNumber: req.body.phoneNumber,
      };

      const registrationResult = await voterRegistrationService.registerNewVoter(
        registrationRequest,
      );

      if (registrationResult.success) {
        authenticationLogger.info('Voter registration successful', {
          voterId: registrationResult.data?.id,
        });
        res.status(201).json(registrationResult);
      } else {
        authenticationLogger.warn('Voter registration failed', {
          message: registrationResult.message,
          errors: registrationResult.errors,
        });
        res.status(400).json(registrationResult);
      }
    } catch (registrationError) {
      authenticationLogger.error('Voter registration error', { error: registrationError });
      res.status(500).json({
        success: false,
        message: 'Internal server error during registration',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    }
  });

  authRouter.post('/verify', async (req: Request, res: Response): Promise<void> => {
    try {
      authenticationLogger.info('Processing voter verification request', {
        nationalId: req.body.nationalId,
      });

      const verificationRequest: VoterVerificationRequest = {
        nationalId: req.body.nationalId,
        verificationCode: req.body.verificationCode,
        documentProof: req.body.documentProof,
      };

      const verificationResult = await voterRegistrationService.verifyVoterIdentity(
        verificationRequest,
      );

      if (verificationResult.success) {
        authenticationLogger.info('Voter verification successful', {
          voterId: verificationResult.data?.id,
        });
        res.status(200).json(verificationResult);
      } else {
        authenticationLogger.warn('Voter verification failed', {
          message: verificationResult.message,
        });
        res.status(400).json(verificationResult);
      }
    } catch (verificationError) {
      authenticationLogger.error('Voter verification error', { error: verificationError });
      res.status(500).json({
        success: false,
        message: 'Internal server error during verification',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    }
  });

  authRouter.post('/login', async (req: Request, res: Response): Promise<void> => {
    try {
      authenticationLogger.info('Processing voter login request', {
        email: req.body.email,
      });

      const loginRequest: VoterLoginRequest = {
        email: req.body.email,
        password: req.body.password,
      };

      const loginResult = await voterRegistrationService.authenticateVoterLogin(loginRequest);

      if (loginResult.success) {
        authenticationLogger.info('Voter login successful', {
          voterId: loginResult.data?.voter.id,
        });
        res.status(200).json(loginResult);
      } else {
        authenticationLogger.warn('Voter login failed', {
          message: loginResult.message,
        });
        res.status(401).json(loginResult);
      }
    } catch (loginError) {
      authenticationLogger.error('Voter login error', { error: loginError });
      res.status(500).json({
        success: false,
        message: 'Internal server error during login',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    }
  });

  authRouter.get('/profile/:voterId', async (req: Request, res: Response): Promise<void> => {
    try {
      const voterId = req.params['voterId'];

      authenticationLogger.info('Fetching voter profile', { voterId });

      const profileResult = await voterRegistrationService.getVoterProfile(voterId);

      if (profileResult.success) {
        authenticationLogger.info('Voter profile retrieved successfully', { voterId });
        res.status(200).json(profileResult);
      } else {
        authenticationLogger.warn('Voter profile retrieval failed', {
          message: profileResult.message,
          voterId,
        });
        res.status(404).json(profileResult);
      }
    } catch (profileError) {
      authenticationLogger.error('Voter profile retrieval error', { error: profileError });
      res.status(500).json({
        success: false,
        message: 'Internal server error during profile retrieval',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    }
  });

  authRouter.post('/refresh', async (req: Request, res: Response): Promise<void> => {
    try {
      authenticationLogger.info('Processing token refresh request');

      const refreshToken = req.body.refreshToken;

      if (!refreshToken) {
        res.status(400).json({
          success: false,
          message: 'Refresh token is required',
          timestamp: new Date(),
          requestId: `req_${Date.now()}`,
        });
        return;
      }

      const newTokens = {
        accessToken: `new_access_token_${Date.now()}`,
        refreshToken: `new_refresh_token_${Date.now()}`,
        expiresIn: 3600,
      };

      authenticationLogger.info('Token refresh successful');
      res.status(200).json({
        success: true,
        data: newTokens,
        message: 'Tokens refreshed successfully',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    } catch (refreshError) {
      authenticationLogger.error('Token refresh error', { error: refreshError });
      res.status(500).json({
        success: false,
        message: 'Internal server error during token refresh',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    }
  });

  authRouter.post('/logout', async (_req: Request, res: Response): Promise<void> => {
    try {
      authenticationLogger.info('Processing logout request');

      res.status(200).json({
        success: true,
        message: 'Logout successful',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    } catch (logoutError) {
      authenticationLogger.error('Logout error', { error: logoutError });
      res.status(500).json({
        success: false,
        message: 'Internal server error during logout',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    }
  });

  return authRouter;
}