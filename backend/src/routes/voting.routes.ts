import { Router, Request, Response } from 'express';
import { ApplicationDependencies } from '@/index';
import { logger } from '@/utils/logger';
import { VoteCastingRequest } from '@/services/voting.service';

const votingLogger = logger.child({ service: 'VotingRoutes' });

export function votingRoutes(dependencies: ApplicationDependencies): Router {
  const votingRouter = Router();
  const { votingService } = dependencies;

  votingRouter.post('/cast', async (req: Request, res: Response): Promise<void> => {
    try {
      votingLogger.info('Processing vote casting request', {
        electionId: req.body.electionId,
        voterId: req.body.voterId,
      });

      const voteCastingRequest: VoteCastingRequest = {
        electionId: req.body.electionId,
        voterId: req.body.voterId,
        candidateId: req.body.candidateId,
        voterSignature: req.body.voterSignature,
      };

      const voteCastingResult = await votingService.castVoteInElection(voteCastingRequest);

      if (voteCastingResult.success) {
        votingLogger.info('Vote cast successfully', {
          voteId: voteCastingResult.data?.voteId,
          transactionHash: voteCastingResult.data?.transactionHash,
        });
        res.status(201).json(voteCastingResult);
      } else {
        votingLogger.warn('Vote casting failed', {
          message: voteCastingResult.message,
          errors: voteCastingResult.errors,
        });
        res.status(400).json(voteCastingResult);
      }
    } catch (voteCastingError) {
      votingLogger.error('Vote casting error', { error: voteCastingError });
      res.status(500).json({
        success: false,
        message: 'Internal server error during vote casting',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    }
  });

  votingRouter.get('/verify/:transactionHash', async (req: Request, res: Response): Promise<void> => {
    try {
      const transactionHash = req.params['transactionHash'];

      votingLogger.info('Processing vote verification request', { transactionHash });

      const verificationResult = await votingService.verifyVoteTransparency(transactionHash);

      if (verificationResult.success) {
        votingLogger.info('Vote verification completed', {
          transactionHash,
          isValid: verificationResult.data?.isValid,
        });
        res.status(200).json(verificationResult);
      } else {
        votingLogger.warn('Vote verification failed', {
          message: verificationResult.message,
          transactionHash,
        });
        res.status(404).json(verificationResult);
      }
    } catch (verificationError) {
      votingLogger.error('Vote verification error', { error: verificationError });
      res.status(500).json({
        success: false,
        message: 'Internal server error during vote verification',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    }
  });

  votingRouter.get('/status/:electionId/:voterId', async (req: Request, res: Response): Promise<void> => {
    try {
      const electionId = req.params['electionId'];
      const voterId = req.params['voterId'];

      votingLogger.info('Fetching election voting status', { electionId, voterId });

      const votingStatusResult = await votingService.getElectionVotingStatus(electionId, voterId);

      if (votingStatusResult.success) {
        votingLogger.info('Election voting status retrieved successfully', {
          electionId,
          voterId,
          isVotingActive: votingStatusResult.data?.isVotingActive,
        });
        res.status(200).json(votingStatusResult);
      } else {
        votingLogger.warn('Election voting status retrieval failed', {
          message: votingStatusResult.message,
          electionId,
          voterId,
        });
        res.status(404).json(votingStatusResult);
      }
    } catch (statusError) {
      votingLogger.error('Election voting status error', { error: statusError });
      res.status(500).json({
        success: false,
        message: 'Internal server error during status retrieval',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    }
  });

  votingRouter.get('/receipt/:voteId/:voterId', async (req: Request, res: Response): Promise<void> => {
    try {
      const voteId = req.params['voteId'];
      const voterId = req.params['voterId'];

      votingLogger.info('Fetching vote receipt', { voteId, voterId });

      const receiptResult = await votingService.getVoteReceipt(voteId, voterId);

      if (receiptResult.success) {
        votingLogger.info('Vote receipt retrieved successfully', {
          voteId,
          voterId,
          transactionHash: receiptResult.data?.transactionHash,
        });
        res.status(200).json(receiptResult);
      } else {
        votingLogger.warn('Vote receipt retrieval failed', {
          message: receiptResult.message,
          voteId,
          voterId,
        });
        res.status(404).json(receiptResult);
      }
    } catch (receiptError) {
      votingLogger.error('Vote receipt error', { error: receiptError });
      res.status(500).json({
        success: false,
        message: 'Internal server error during receipt retrieval',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    }
  });

  return votingRouter;
}