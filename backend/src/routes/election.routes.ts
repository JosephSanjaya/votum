import { Router, Request, Response } from 'express';
import { ApplicationDependencies } from '@/index';
import { logger } from '@/utils/logger';

const electionLogger = logger.child({ service: 'ElectionRoutes' });

export function electionRoutes(dependencies: ApplicationDependencies): Router {
  const electionRouter = Router();
  const { prismaClient } = dependencies;

  electionRouter.get('/', async (req: Request, res: Response): Promise<void> => {
    try {
      electionLogger.info('Fetching all elections');

      const page = parseInt(req.query['page'] as string) || 1;
      const limit = parseInt(req.query['limit'] as string) || 10;
      const skip = (page - 1) * limit;

      const [elections, totalCount] = await Promise.all([
        prismaClient.election.findMany({
          skip,
          take: limit,
          include: {
            candidates: true,
            _count: {
              select: {
                voteRecords: true,
              },
            },
          },
          orderBy: {
            createdAt: 'desc',
          },
        }),
        prismaClient.election.count(),
      ]);

      const totalPages = Math.ceil(totalCount / limit);

      electionLogger.info('Elections retrieved successfully', {
        count: elections.length,
        totalCount,
        page,
      });

      res.status(200).json({
        success: true,
        data: elections,
        pagination: {
          currentPage: page,
          totalPages,
          totalItems: totalCount,
          itemsPerPage: limit,
          hasNextPage: page < totalPages,
          hasPreviousPage: page > 1,
        },
        message: 'Elections retrieved successfully',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    } catch (electionsError) {
      electionLogger.error('Elections retrieval error', { error: electionsError });
      res.status(500).json({
        success: false,
        message: 'Internal server error during elections retrieval',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    }
  });

  electionRouter.get('/:electionId', async (req: Request, res: Response): Promise<void> => {
    try {
      const electionId = req.params['electionId'];

      if (!electionId) {
        res.status(400).json({
          success: false,
          message: 'Election ID is required',
          timestamp: new Date().toISOString(),
          requestId: `req_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
        });
        return;
      }

      electionLogger.info('Fetching election details', { electionId });

      const election = await prismaClient.election.findUnique({
        where: { id: electionId },
        include: {
          candidates: true,
          electionResults: {
            include: {
              candidateResults: true,
            },
          },
          electionStatistics: true,
          eligibilityCriteria: true,
          _count: {
            select: {
              voteRecords: true,
            },
          },
        },
      });

      if (!election) {
        electionLogger.warn('Election not found', { electionId });
        res.status(404).json({
          success: false,
          message: 'Election not found',
          timestamp: new Date(),
          requestId: `req_${Date.now()}`,
        });
        return;
      }

      electionLogger.info('Election details retrieved successfully', { electionId });

      res.status(200).json({
        success: true,
        data: election,
        message: 'Election details retrieved successfully',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    } catch (electionError) {
      electionLogger.error('Election details retrieval error', { error: electionError });
      res.status(500).json({
        success: false,
        message: 'Internal server error during election retrieval',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    }
  });

  electionRouter.post('/', async (req: Request, res: Response): Promise<void> => {
    try {
      electionLogger.info('Creating new election', {
        title: req.body.title,
        createdBy: req.body.createdBy,
      });

      const electionData = {
        title: req.body.title,
        description: req.body.description,
        startTime: new Date(req.body.startTime),
        endTime: new Date(req.body.endTime),
        registrationDeadline: new Date(req.body.registrationDeadline),
        electionType: req.body.electionType,
        minimumAge: req.body.minimumAge || 18,
        maxVotesPerVoter: req.body.maxVotesPerVoter || 1,
        allowAbstention: req.body.allowAbstention || true,
        requiresIdentityVerification: req.body.requiresIdentityVerification || true,
        createdBy: req.body.createdBy,
      };

      const newElection = await prismaClient.election.create({
        data: electionData,
        include: {
          candidates: true,
        },
      });

      if (req.body.candidates && Array.isArray(req.body.candidates)) {
        const candidatesData = req.body.candidates.map((candidate: any, index: number) => ({
          electionId: newElection.id,
          name: candidate.name,
          party: candidate.party,
          description: candidate.description,
          imageUrl: candidate.imageUrl,
          blockchainIndex: index,
          manifesto: candidate.manifesto || '',
          qualifications: candidate.qualifications || [],
        }));

        await prismaClient.candidate.createMany({
          data: candidatesData,
        });
      }

      if (req.body.eligibilityCriteria && Array.isArray(req.body.eligibilityCriteria)) {
        const criteriaData = req.body.eligibilityCriteria.map((criterion: any) => ({
          electionId: newElection.id,
          criterion: criterion.criterion,
          description: criterion.description,
          isRequired: criterion.isRequired || true,
        }));

        await prismaClient.eligibilityCriteria.createMany({
          data: criteriaData,
        });
      }

      const completeElection = await prismaClient.election.findUnique({
        where: { id: newElection.id },
        include: {
          candidates: true,
          eligibilityCriteria: true,
        },
      });

      electionLogger.info('Election created successfully', {
        electionId: newElection.id,
        candidatesCount: req.body.candidates?.length || 0,
      });

      res.status(201).json({
        success: true,
        data: completeElection,
        message: 'Election created successfully',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    } catch (electionCreationError) {
      electionLogger.error('Election creation error', { error: electionCreationError });
      res.status(500).json({
        success: false,
        message: 'Internal server error during election creation',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    }
  });

  electionRouter.put('/:electionId', async (req: Request, res: Response): Promise<void> => {
    try {
      const electionId = req.params['electionId'];

      if (!electionId) {
        res.status(400).json({
          success: false,
          message: 'Election ID is required',
          timestamp: new Date().toISOString(),
          requestId: `req_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
        });
        return;
      }

      electionLogger.info('Updating election', { electionId });

      const existingElection = await prismaClient.election.findUnique({
        where: { id: electionId },
      });

      if (!existingElection) {
        electionLogger.warn('Election not found for update', { electionId });
        res.status(404).json({
          success: false,
          message: 'Election not found',
          timestamp: new Date(),
          requestId: `req_${Date.now()}`,
        });
        return;
      }

      const updateData: any = {};
      if (req.body.title) updateData.title = req.body.title;
      if (req.body.description) updateData.description = req.body.description;
      if (req.body.startTime) updateData.startTime = new Date(req.body.startTime);
      if (req.body.endTime) updateData.endTime = new Date(req.body.endTime);
      if (req.body.registrationDeadline) updateData.registrationDeadline = new Date(req.body.registrationDeadline);
      if (req.body.status) updateData.status = req.body.status;
      if (req.body.blockchainAddress) updateData.blockchainAddress = req.body.blockchainAddress;

      const updatedElection = await prismaClient.election.update({
        where: { id: electionId },
        data: updateData,
        include: {
          candidates: true,
          eligibilityCriteria: true,
        },
      });

      electionLogger.info('Election updated successfully', { electionId });

      res.status(200).json({
        success: true,
        data: updatedElection,
        message: 'Election updated successfully',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    } catch (electionUpdateError) {
      electionLogger.error('Election update error', { error: electionUpdateError });
      res.status(500).json({
        success: false,
        message: 'Internal server error during election update',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    }
  });

  electionRouter.get('/:electionId/candidates', async (req: Request, res: Response): Promise<void> => {
    try {
      const electionId = req.params['electionId'];

      if (!electionId) {
        res.status(400).json({
          success: false,
          message: 'Election ID is required',
          timestamp: new Date().toISOString(),
          requestId: `req_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
        });
        return;
      }

      electionLogger.info('Fetching election candidates', { electionId });

      const candidates = await prismaClient.candidate.findMany({
        where: { electionId },
        include: {
          _count: {
            select: {
              voteRecords: true,
            },
          },
        },
        orderBy: {
          blockchainIndex: 'asc',
        },
      });

      electionLogger.info('Election candidates retrieved successfully', {
        electionId,
        candidatesCount: candidates.length,
      });

      res.status(200).json({
        success: true,
        data: candidates,
        message: 'Election candidates retrieved successfully',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    } catch (candidatesError) {
      electionLogger.error('Election candidates retrieval error', { error: candidatesError });
      res.status(500).json({
        success: false,
        message: 'Internal server error during candidates retrieval',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    }
  });

  return electionRouter;
}