import winston from 'winston';
import path from 'path';

const logLevel = process.env['LOG_LEVEL'] || 'info';
const logFilePath = process.env['LOG_FILE_PATH'] || './logs/app.log';

const logFormat = winston.format.combine(
  winston.format.timestamp({
    format: 'YYYY-MM-DD HH:mm:ss',
  }),
  winston.format.errors({ stack: true }),
  winston.format.json(),
  winston.format.prettyPrint(),
);

const consoleFormat = winston.format.combine(
  winston.format.colorize(),
  winston.format.timestamp({
    format: 'YYYY-MM-DD HH:mm:ss',
  }),
  winston.format.printf(({ timestamp, level, message, ...meta }) => {
    const metaString = Object.keys(meta).length > 0 ? JSON.stringify(meta, null, 2) : '';
    return `${timestamp} [${level}]: ${message} ${metaString}`;
  }),
);

const createLoggerTransports = (): winston.transport[] => {
  const transports: winston.transport[] = [
    new winston.transports.Console({
      format: consoleFormat,
      level: logLevel,
    }),
  ];

  if (process.env['NODE_ENV'] !== 'test') {
    transports.push(
      new winston.transports.File({
        filename: path.resolve(logFilePath),
        format: logFormat,
        level: logLevel,
        maxsize: 5242880,
        maxFiles: 5,
        tailable: true,
      }),
    );

    transports.push(
      new winston.transports.File({
        filename: path.resolve('./logs/error.log'),
        format: logFormat,
        level: 'error',
        maxsize: 5242880,
        maxFiles: 5,
        tailable: true,
      }),
    );
  }

  return transports;
};

export const logger = winston.createLogger({
  level: logLevel,
  format: logFormat,
  transports: createLoggerTransports(),
  exitOnError: false,
  silent: process.env['NODE_ENV'] === 'test',
});

export const createChildLogger = (service: string): winston.Logger => {
  return logger.child({ service });
};

export const loggerMiddleware = (req: any, res: any, next: any): void => {
  const startTime = Date.now();
  
  res.on('finish', () => {
    const duration = Date.now() - startTime;
    const logData = {
      method: req.method,
      url: req.url,
      statusCode: res.statusCode,
      duration: `${duration}ms`,
      userAgent: req.get('User-Agent'),
      ip: req.ip,
    };

    if (res.statusCode >= 400) {
      logger.warn('HTTP request completed with error', logData);
    } else {
      logger.info('HTTP request completed', logData);
    }
  });

  next();
};

export const logUnhandledRejections = (): void => {
  process.on('unhandledRejection', (reason: unknown, promise: Promise<unknown>) => {
    logger.error('Unhandled Promise Rejection', {
      reason,
      promise: promise.toString(),
      stack: reason instanceof Error ? reason.stack : undefined,
    });
  });

  process.on('uncaughtException', (error: Error) => {
    logger.error('Uncaught Exception', {
      error: error.message,
      stack: error.stack,
    });
    process.exit(1);
  });
};

export default logger;