import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import compression from 'compression';
import rateLimit from 'express-rate-limit';
import { PrismaClient } from '@prisma/client';
import dotenv from 'dotenv';
import { logger, loggerMiddleware, logUnhandledRejections } from '@/utils/logger';
import { createPolkadotConnectionManager, getDefaultPolkadotConfig } from '@/config/polkadot.config';
import { createBlockchainElectionService } from '@/services/blockchain.service';
import { createVoterRegistrationService } from '@/services/voter-registration.service';
import { createVotingService } from '@/services/voting.service';
import { createElectionResultsService } from '@/services/election-results.service';
import { authenticationRoutes } from '@/routes/authentication.routes';
import { electionRoutes } from '@/routes/election.routes';
import { votingRoutes } from '@/routes/voting.routes';
import { resultsRoutes } from '@/routes/results.routes';
import { auditRoutes } from '@/routes/audit.routes';

dotenv.config();

export interface ApplicationDependencies {
  readonly prismaClient: PrismaClient;
  readonly voterRegistrationService: any;
  readonly votingService: any;
  readonly electionResultsService: any;
  readonly blockchainService: any;
}

export class PolkadotElectionApplication {
  private expressApplication: express.Application;
  private prismaClient: PrismaClient;
  private polkadotConnectionManager: any;
  private applicationDependencies: ApplicationDependencies;
  private serverPort: number;
  private applicationLogger = logger.child({ service: 'PolkadotElectionApplication' });

  constructor() {
    this.expressApplication = express();
    this.prismaClient = new PrismaClient();
    this.serverPort = parseInt(process.env['PORT'] || '3000', 10);
    this.polkadotConnectionManager = createPolkadotConnectionManager(getDefaultPolkadotConfig());
    this.applicationDependencies = {} as ApplicationDependencies;
  }

  public async initializeApplication(): Promise<void> {
    try {
      this.applicationLogger.info('Starting Polkadot Election Application initialization');

      await this.setupUnhandledRejectionHandlers();
      await this.initializeDatabaseConnection();
      await this.initializeBlockchainConnection();
      await this.initializeApplicationServices();
      await this.configureExpressMiddleware();
      await this.setupApplicationRoutes();
      await this.configureErrorHandling();

      this.applicationLogger.info('Polkadot Election Application initialized successfully');
    } catch (initializationError) {
      this.applicationLogger.error('Application initialization failed', {
        error: initializationError,
      });
      throw new Error(`Application initialization failed: ${initializationError}`);
    }
  }

  public async startServer(): Promise<void> {
    try {
      const httpServer = this.expressApplication.listen(this.serverPort, () => {
        this.applicationLogger.info('Polkadot Election Server started successfully', {
          port: this.serverPort,
          environment: process.env['NODE_ENV'] || 'development',
        });
      });

      httpServer.on('error', (serverError: Error) => {
        this.applicationLogger.error('Server error occurred', { error: serverError });
        throw serverError;
      });

      process.on('SIGTERM', async () => {
        this.applicationLogger.info('SIGTERM received, shutting down gracefully');
        await this.gracefulShutdown();
      });

      process.on('SIGINT', async () => {
        this.applicationLogger.info('SIGINT received, shutting down gracefully');
        await this.gracefulShutdown();
      });
    } catch (serverStartError) {
      this.applicationLogger.error('Server startup failed', { error: serverStartError });
      throw new Error(`Server startup failed: ${serverStartError}`);
    }
  }

  private async setupUnhandledRejectionHandlers(): Promise<void> {
    logUnhandledRejections();
    this.applicationLogger.debug('Unhandled rejection handlers configured');
  }

  private async initializeDatabaseConnection(): Promise<void> {
    try {
      await this.prismaClient.$connect();
      this.applicationLogger.info('Database connection established successfully');
    } catch (databaseConnectionError) {
      this.applicationLogger.error('Database connection failed', {
        error: databaseConnectionError,
      });
      throw new Error(`Database connection failed: ${databaseConnectionError}`);
    }
  }

  private async initializeBlockchainConnection(): Promise<void> {
    try {
      await this.polkadotConnectionManager.initializeConnection();
      this.applicationLogger.info('Blockchain connection established successfully');
    } catch (blockchainConnectionError) {
      this.applicationLogger.error('Blockchain connection failed', {
        error: blockchainConnectionError,
      });
      throw new Error(`Blockchain connection failed: ${blockchainConnectionError}`);
    }
  }

  private async initializeApplicationServices(): Promise<void> {
    try {
      const blockchainService = createBlockchainElectionService(
        this.polkadotConnectionManager,
        process.env['ELECTION_CONTRACT_ADDRESS'] || '',
      );

      const voterRegistrationService = createVoterRegistrationService(
        this.prismaClient,
        blockchainService,
      );

      const votingService = createVotingService(this.prismaClient, blockchainService);

      const electionResultsService = createElectionResultsService(
        this.prismaClient,
        blockchainService,
      );

      await voterRegistrationService.initializeService();

      this.applicationDependencies = {
        prismaClient: this.prismaClient,
        voterRegistrationService,
        votingService,
        electionResultsService,
        blockchainService,
      };

      this.applicationLogger.info('Application services initialized successfully');
    } catch (serviceInitializationError) {
      this.applicationLogger.error('Service initialization failed', {
        error: serviceInitializationError,
      });
      throw new Error(`Service initialization failed: ${serviceInitializationError}`);
    }
  }

  private async configureExpressMiddleware(): Promise<void> {
    const rateLimitConfiguration = rateLimit({
      windowMs: parseInt(process.env['RATE_LIMIT_WINDOW_MS'] || '900000', 10),
      max: parseInt(process.env['RATE_LIMIT_MAX_REQUESTS'] || '100', 10),
      message: {
        error: 'Too many requests from this IP, please try again later',
        retryAfter: '15 minutes',
      },
      standardHeaders: true,
      legacyHeaders: false,
    });

    const corsConfiguration = {
      origin: process.env['CORS_ORIGIN'] || 'http://localhost:3001',
      credentials: process.env['CORS_CREDENTIALS'] === 'true',
      methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
      allowedHeaders: ['Content-Type', 'Authorization', 'X-Requested-With'],
    };

    this.expressApplication.use(helmet());
    this.expressApplication.use(compression());
    this.expressApplication.use(cors(corsConfiguration));
    this.expressApplication.use(rateLimitConfiguration);
    this.expressApplication.use(express.json({ limit: '10mb' }));
    this.expressApplication.use(express.urlencoded({ extended: true, limit: '10mb' }));
    this.expressApplication.use(loggerMiddleware);

    this.applicationLogger.debug('Express middleware configured successfully');
  }

  private async setupApplicationRoutes(): Promise<void> {
    this.expressApplication.get('/health', this.createHealthCheckHandler());
    this.expressApplication.get('/api/status', this.createStatusCheckHandler());

    this.expressApplication.use('/api/auth', authenticationRoutes(this.applicationDependencies));
    this.expressApplication.use('/api/elections', electionRoutes(this.applicationDependencies));
    this.expressApplication.use('/api/voting', votingRoutes(this.applicationDependencies));
    this.expressApplication.use('/api/results', resultsRoutes(this.applicationDependencies));
    this.expressApplication.use('/api/audit', auditRoutes(this.applicationDependencies));

    this.expressApplication.use(this.createNotFoundHandler());

    this.applicationLogger.debug('Application routes configured successfully');
  }

  private async configureErrorHandling(): Promise<void> {
    this.expressApplication.use(this.createGlobalErrorHandler());
    this.applicationLogger.debug('Error handling configured successfully');
  }

  private createHealthCheckHandler() {
    return async (_req: express.Request, res: express.Response): Promise<void> => {
      try {
        const databaseHealthCheck = await this.checkDatabaseHealth();
        const blockchainHealthCheck = await this.checkBlockchainHealth();

        const healthStatus = {
          status: 'healthy',
          timestamp: new Date().toISOString(),
          version: '1.0.0',
          services: {
            database: databaseHealthCheck,
            blockchain: blockchainHealthCheck,
          },
          uptime: process.uptime(),
          memory: process.memoryUsage(),
        };

        res.status(200).json(healthStatus);
      } catch (healthCheckError) {
        this.applicationLogger.error('Health check failed', { error: healthCheckError });
        res.status(503).json({
          status: 'unhealthy',
          timestamp: new Date().toISOString(),
          error: 'Health check failed',
        });
      }
    };
  }

  private createStatusCheckHandler() {
    return async (_req: express.Request, res: express.Response): Promise<void> => {
      try {
        const connectionStatus = await this.polkadotConnectionManager.getConnectionStatus();
        
        const systemStatus = {
          application: 'Polkadot Election System',
          version: '1.0.0',
          environment: process.env['NODE_ENV'] || 'development',
          timestamp: new Date().toISOString(),
          blockchain: connectionStatus,
          features: {
            voterRegistration: true,
            voting: true,
            resultsAggregation: true,
            auditTrail: true,
            realTimeResults: true,
          },
        };

        res.status(200).json(systemStatus);
      } catch (statusCheckError) {
        this.applicationLogger.error('Status check failed', { error: statusCheckError });
        res.status(500).json({
          error: 'Status check failed',
          timestamp: new Date().toISOString(),
        });
      }
    };
  }

  private createNotFoundHandler() {
    return (req: express.Request, res: express.Response, next: express.NextFunction): void => {
      if (res.headersSent) {
        return next();
      }
      res.status(404).json({
        error: 'Endpoint not found',
        message: `The requested endpoint ${req.method} ${req.path} does not exist`,
        timestamp: new Date().toISOString(),
      });
    };
  }

  private createGlobalErrorHandler() {
    return (
      error: Error,
      req: express.Request,
      res: express.Response,
      _next: express.NextFunction,
    ): void => {
      this.applicationLogger.error('Unhandled application error', {
        error: error.message,
        stack: error.stack,
        url: req.url,
        method: req.method,
      });

      const isDevelopment = process.env['NODE_ENV'] === 'development';

      res.status(500).json({
        error: 'Internal server error',
        message: isDevelopment ? error.message : 'An unexpected error occurred',
        timestamp: new Date().toISOString(),
        ...(isDevelopment && { stack: error.stack }),
      });
    };
  }

  private async checkDatabaseHealth(): Promise<{ status: string; responseTime: number }> {
    const startTime = Date.now();
    try {
      await this.prismaClient.$queryRaw`SELECT 1`;
      return {
        status: 'healthy',
        responseTime: Date.now() - startTime,
      };
    } catch (databaseError) {
      return {
        status: 'unhealthy',
        responseTime: Date.now() - startTime,
      };
    }
  }

  private async checkBlockchainHealth(): Promise<{ status: string; responseTime: number }> {
    const startTime = Date.now();
    try {
      const connectionStatus = await this.polkadotConnectionManager.getConnectionStatus();
      return {
        status: connectionStatus.isConnected ? 'healthy' : 'unhealthy',
        responseTime: Date.now() - startTime,
      };
    } catch (blockchainError) {
      return {
        status: 'unhealthy',
        responseTime: Date.now() - startTime,
      };
    }
  }

  private async gracefulShutdown(): Promise<void> {
    try {
      this.applicationLogger.info('Starting graceful shutdown');

      await this.polkadotConnectionManager.disconnectFromNetwork();
      await this.prismaClient.$disconnect();

      this.applicationLogger.info('Graceful shutdown completed');
      process.exit(0);
    } catch (shutdownError) {
      this.applicationLogger.error('Graceful shutdown failed', { error: shutdownError });
      process.exit(1);
    }
  }
}

async function startPolkadotElectionApplication(): Promise<void> {
  try {
    const electionApplication = new PolkadotElectionApplication();
    await electionApplication.initializeApplication();
    await electionApplication.startServer();
  } catch (applicationStartupError) {
    logger.error('Application startup failed', { error: applicationStartupError });
    process.exit(1);
  }
}

if (import.meta.url === `file://${process.argv[1]}`) {
  startPolkadotElectionApplication().catch((startupError: Error) => {
    logger.error('Failed to start Polkadot Election Application', { error: startupError });
    process.exit(1);
  });
}

export default PolkadotElectionApplication;