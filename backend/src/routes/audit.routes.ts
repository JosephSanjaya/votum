import { Router, Request, Response } from 'express';
import { ApplicationDependencies } from '@/index';
import { logger } from '@/utils/logger';

const auditLogger = logger.child({ service: 'AuditRoutes' });

export function auditRoutes(dependencies: ApplicationDependencies): Router {
  const auditRouter = Router();
  const { prismaClient } = dependencies;

  auditRouter.get('/trail/:electionId', async (req: Request, res: Response): Promise<void> => {
    try {
      const electionId = req.params['electionId'];
      const page = parseInt(req.query['page'] as string) || 1;
      const limit = parseInt(req.query['limit'] as string) || 50;
      const skip = (page - 1) * limit;

      auditLogger.info('Fetching audit trail for election', { electionId, page, limit });

      const [auditEntries, totalCount] = await Promise.all([
        prismaClient.auditTrailEntry.findMany({
          where: electionId ? { electionId } : {},
          skip,
          take: limit,
          include: {
            user: {
              select: {
                id: true,
                email: true,
                fullName: true,
              },
            },
          },
          orderBy: {
            timestamp: 'desc',
          },
        }),
        prismaClient.auditTrailEntry.count({
          where: electionId ? { electionId } : {},
        }),
      ]);

      const totalPages = Math.ceil(totalCount / limit);

      auditLogger.info('Audit trail retrieved successfully', {
        electionId,
        entriesCount: auditEntries.length,
        totalCount,
      });

      res.status(200).json({
        success: true,
        data: auditEntries,
        pagination: {
          currentPage: page,
          totalPages,
          totalItems: totalCount,
          itemsPerPage: limit,
          hasNextPage: page < totalPages,
          hasPreviousPage: page > 1,
        },
        message: 'Audit trail retrieved successfully',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    } catch (auditTrailError) {
      auditLogger.error('Audit trail retrieval error', { error: auditTrailError });
      res.status(500).json({
        success: false,
        message: 'Internal server error during audit trail retrieval',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    }
  });

  auditRouter.get('/trail/user/:userId', async (req: Request, res: Response): Promise<void> => {
    try {
      const userId = req.params['userId'];
      const page = parseInt(req.query['page'] as string) || 1;
      const limit = parseInt(req.query['limit'] as string) || 50;
      const skip = (page - 1) * limit;

      auditLogger.info('Fetching audit trail for user', { userId, page, limit });

      const [auditEntries, totalCount] = await Promise.all([
        prismaClient.auditTrailEntry.findMany({
          where: userId ? { performedBy: userId } : {},
          skip,
          take: limit,
          include: {
            election: {
              select: {
                id: true,
                title: true,
                status: true,
              },
            },
          },
          orderBy: {
            timestamp: 'desc',
          },
        }),
        prismaClient.auditTrailEntry.count({
          where: userId ? { performedBy: userId } : {},
        }),
      ]);

      const totalPages = Math.ceil(totalCount / limit);

      auditLogger.info('User audit trail retrieved successfully', {
        userId,
        entriesCount: auditEntries.length,
        totalCount,
      });

      res.status(200).json({
        success: true,
        data: auditEntries,
        pagination: {
          currentPage: page,
          totalPages,
          totalItems: totalCount,
          itemsPerPage: limit,
          hasNextPage: page < totalPages,
          hasPreviousPage: page > 1,
        },
        message: 'User audit trail retrieved successfully',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    } catch (userAuditError) {
      auditLogger.error('User audit trail retrieval error', { error: userAuditError });
      res.status(500).json({
        success: false,
        message: 'Internal server error during user audit trail retrieval',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    }
  });

  auditRouter.get('/security-events', async (req: Request, res: Response): Promise<void> => {
    try {
      const page = parseInt(req.query['page'] as string) || 1;
      const limit = parseInt(req.query['limit'] as string) || 50;
      const severity = req.query['severity'] as string;
      const eventType = req.query['eventType'] as string;
      const skip = (page - 1) * limit;

      auditLogger.info('Fetching security events', { page, limit, severity, eventType });

      const whereClause: any = {};
      if (severity) whereClause.severity = severity;
      if (eventType) whereClause.eventType = eventType;

      const [securityEvents, totalCount] = await Promise.all([
        prismaClient.securityEvent.findMany({
          where: whereClause,
          skip,
          take: limit,
          orderBy: {
            timestamp: 'desc',
          },
        }),
        prismaClient.securityEvent.count({
          where: whereClause,
        }),
      ]);

      const totalPages = Math.ceil(totalCount / limit);

      auditLogger.info('Security events retrieved successfully', {
        eventsCount: securityEvents.length,
        totalCount,
        severity,
        eventType,
      });

      res.status(200).json({
        success: true,
        data: securityEvents,
        pagination: {
          currentPage: page,
          totalPages,
          totalItems: totalCount,
          itemsPerPage: limit,
          hasNextPage: page < totalPages,
          hasPreviousPage: page > 1,
        },
        message: 'Security events retrieved successfully',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    } catch (securityEventsError) {
      auditLogger.error('Security events retrieval error', { error: securityEventsError });
      res.status(500).json({
        success: false,
        message: 'Internal server error during security events retrieval',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    }
  });

  auditRouter.get('/blockchain-verification/:transactionHash', async (req: Request, res: Response): Promise<void> => {
    try {
      const transactionHash = req.params['transactionHash'];

      auditLogger.info('Fetching blockchain verification data', { transactionHash });

      if (!transactionHash) {
        res.status(400).json({
          success: false,
          message: 'Transaction hash is required',
          timestamp: new Date().toISOString(),
          requestId: `req_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
        });
        return;
      }

      const voteRecord = await prismaClient.voteRecord.findUnique({
        where: { transactionHash },
        include: {
          election: {
            select: {
              id: true,
              title: true,
              status: true,
            },
          },
          voter: {
            select: {
              id: true,
              nationalId: true,
              publicKey: true,
            },
          },
          candidate: {
            select: {
              id: true,
              name: true,
              party: true,
            },
          },
        },
      });

      if (!voteRecord) {
        auditLogger.warn('Vote record not found for blockchain verification', { transactionHash });
        res.status(404).json({
          success: false,
          message: 'Vote record not found',
          timestamp: new Date(),
          requestId: `req_${Date.now()}`,
        });
        return;
      }

      const verificationData = {
        transactionHash: voteRecord.transactionHash,
        blockNumber: voteRecord.blockNumber.toString(),
        blockHash: voteRecord.blockHash,
        timestamp: voteRecord.timestamp,
        isVerified: voteRecord.isVerified,
        verificationAttempts: voteRecord.verificationAttempts,
        lastVerificationDate: voteRecord.lastVerificationDate,
        status: voteRecord.status,
        election: voteRecord.election,
        voter: {
          id: voteRecord.voter.id,
          publicKey: voteRecord.voter.publicKey,
        },
        candidate: voteRecord.candidate,
        gasUsed: voteRecord.gasUsed.toString(),
        gasPrice: voteRecord.gasPrice.toString(),
      };

      auditLogger.info('Blockchain verification data retrieved successfully', {
        transactionHash,
        isVerified: voteRecord.isVerified,
      });

      res.status(200).json({
        success: true,
        data: verificationData,
        message: 'Blockchain verification data retrieved successfully',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    } catch (verificationError) {
      auditLogger.error('Blockchain verification error', { error: verificationError });
      res.status(500).json({
        success: false,
        message: 'Internal server error during blockchain verification',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    }
  });

  auditRouter.get('/system-health', async (_req: Request, res: Response): Promise<void> => {
    try {
      auditLogger.info('Fetching system health metrics');

      const [
        totalUsers,
        totalElections,
        totalVotes,
        recentSecurityEvents,
        systemConfigurations,
      ] = await Promise.all([
        prismaClient.user.count(),
        prismaClient.election.count(),
        prismaClient.voteRecord.count(),
        prismaClient.securityEvent.count({
          where: {
            timestamp: {
              gte: new Date(Date.now() - 24 * 60 * 60 * 1000), // Last 24 hours
            },
          },
        }),
        prismaClient.systemConfiguration.findMany({
          where: { isActive: true },
        }),
      ]);

      const healthMetrics = {
        systemStatus: 'operational',
        timestamp: new Date(),
        metrics: {
          totalUsers,
          totalElections,
          totalVotes,
          recentSecurityEvents,
        },
        configurations: systemConfigurations,
        uptime: process.uptime(),
        memoryUsage: process.memoryUsage(),
        nodeVersion: process.version,
      };

      auditLogger.info('System health metrics retrieved successfully', {
        totalUsers,
        totalElections,
        totalVotes,
      });

      res.status(200).json({
        success: true,
        data: healthMetrics,
        message: 'System health metrics retrieved successfully',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    } catch (healthError) {
      auditLogger.error('System health metrics error', { error: healthError });
      res.status(500).json({
        success: false,
        message: 'Internal server error during health metrics retrieval',
        timestamp: new Date(),
        requestId: `req_${Date.now()}`,
      });
    }
  });

  return auditRouter;
}