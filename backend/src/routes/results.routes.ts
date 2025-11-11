import { Router, Request, Response } from 'express';
import { ApplicationDependencies } from '@/index';
import { logger } from '@/utils/logger';
import { ElectionResultsCalculationRequest } from '@/services/election-results.service';

const resultsLogger = logger.child({ service: 'ResultsRoutes' });

export function resultsRoutes(dependencies: ApplicationDependencies): Router {
  const resultsRouter = Router();
  const { electionResultsService } = dependencies;

  resultsRouter.get('/:electionId', async (req: Request, res: Response): Promise<void> => {
    try {
      const electionId = req.params['electionId'];

      resultsLogger.info('Fetching election results', { electionId });

      const resultsResponse = await electionResultsService.getElectionResults(electionId);

      if (resultsResponse.success) {
        resultsLogger.info('Election results retrieved successfully', {
          electionId,
          totalVotesCast: resultsResponse.data?.totalVotesCast,
        });
        res.status(200).json(resultsResponse);
      } else {
        resultsLogger.warn('Election results retrieval failed', {
          message: resultsResponse.message,
          electionId,
        });
        res.status(404).json(resultsResponse);
      }
    } catch (resultsError) {
      resultsLogger.error('Election results retrieval error', { error: resultsError });
      res.status(500).json({
        success: false,
        message: 'Internal server error during results retrieval',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    }
  });

  resultsRouter.post('/:electionId/calculate', async (req: Request, res: Response): Promise<void> => {
    try {
      const electionId = req.params['electionId'];
      const requestedBy = req.body.requestedBy || 'system';

      if (!electionId) {
        res.status(400).json({
          success: false,
          message: 'Election ID is required',
          timestamp: new Date().toISOString(),
          requestId: `req_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
        });
        return;
      }

      resultsLogger.info('Processing election results calculation', {
        electionId,
        requestedBy,
      });

      const calculationRequest: ElectionResultsCalculationRequest = {
        electionId,
        requestedBy,
      };

      const calculationResult = await electionResultsService.calculateElectionResults(
        calculationRequest,
      );

      if (calculationResult.success) {
        resultsLogger.info('Election results calculated successfully', {
          electionId,
          totalVotesCast: calculationResult.data?.totalVotesCast,
          winningCandidate: calculationResult.data?.winningCandidate || null,
        });
        res.status(200).json(calculationResult);
      } else {
        resultsLogger.warn('Election results calculation failed', {
          message: calculationResult.message,
          electionId,
        });
        res.status(400).json(calculationResult);
      }
    } catch (calculationError) {
      resultsLogger.error('Election results calculation error', { error: calculationError });
      res.status(500).json({
        success: false,
        message: 'Internal server error during results calculation',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    }
  });

  resultsRouter.get('/:electionId/live', async (req: Request, res: Response): Promise<void> => {
    try {
      const electionId = req.params['electionId'];

      resultsLogger.info('Fetching live election results', { electionId });

      const liveResultsResponse = await electionResultsService.getLiveElectionResults(electionId);

      if (liveResultsResponse.success) {
        resultsLogger.info('Live election results retrieved successfully', {
          electionId,
          totalVotesCast: liveResultsResponse.data?.currentResults.totalVotesCast,
        });
        res.status(200).json(liveResultsResponse);
      } else {
        resultsLogger.warn('Live election results retrieval failed', {
          message: liveResultsResponse.message,
          electionId,
        });
        res.status(404).json(liveResultsResponse);
      }
    } catch (liveResultsError) {
      resultsLogger.error('Live election results retrieval error', { error: liveResultsError });
      res.status(500).json({
        success: false,
        message: 'Internal server error during live results retrieval',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    }
  });

  resultsRouter.post('/:electionId/finalize', async (req: Request, res: Response): Promise<void> => {
    try {
      const electionId = req.params['electionId'];
      const finalizedBy = req.body.finalizedBy;

      if (!finalizedBy) {
        res.status(400).json({
          success: false,
          message: 'finalizedBy field is required',
          timestamp: new Date(),
          requestId: `req_${Date.now()}`,
        });
        return;
      }

      resultsLogger.info('Processing election results finalization', {
        electionId,
        finalizedBy,
      });

      const finalizationResult = await electionResultsService.finalizeElectionResults(
        electionId,
        finalizedBy,
      );

      if (finalizationResult.success) {
        resultsLogger.info('Election results finalized successfully', {
          electionId,
          finalizedBy,
          blockchainProof: finalizationResult.data?.blockchainProof,
        });
        res.status(200).json(finalizationResult);
      } else {
        resultsLogger.warn('Election results finalization failed', {
          message: finalizationResult.message,
          electionId,
          finalizedBy,
        });
        res.status(400).json(finalizationResult);
      }
    } catch (finalizationError) {
      resultsLogger.error('Election results finalization error', { error: finalizationError });
      res.status(500).json({
        success: false,
        message: 'Internal server error during results finalization',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    }
  });

  resultsRouter.get('/:electionId/analytics', async (req: Request, res: Response): Promise<void> => {
    try {
      const electionId = req.params['electionId'];

      resultsLogger.info('Fetching election analytics', { electionId });

      const analyticsResponse = await electionResultsService.getElectionAnalytics(electionId);

      if (analyticsResponse.success) {
        resultsLogger.info('Election analytics retrieved successfully', {
          electionId,
          totalParticipation: analyticsResponse.data?.totalParticipation,
        });
        res.status(200).json(analyticsResponse);
      } else {
        resultsLogger.warn('Election analytics retrieval failed', {
          message: analyticsResponse.message,
          electionId,
        });
        res.status(404).json(analyticsResponse);
      }
    } catch (analyticsError) {
      resultsLogger.error('Election analytics retrieval error', { error: analyticsError });
      res.status(500).json({
        success: false,
        message: 'Internal server error during analytics retrieval',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    }
  });

  return resultsRouter;
}