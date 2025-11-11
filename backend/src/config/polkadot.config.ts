import { ApiPromise, WsProvider } from '@polkadot/api';
import type { ProviderInterface } from '@polkadot/rpc-provider/types';
import { Keyring } from '@polkadot/keyring';
import { cryptoWaitReady } from '@polkadot/util-crypto';
import { logger } from '@/utils/logger';

export interface PolkadotConnectionConfig {
  readonly wsEndpoint: string;
  readonly networkName: string;
  readonly connectionTimeout: number;
  readonly reconnectAttempts: number;
  readonly reconnectDelay: number;
}

export interface PolkadotConnectionStatus {
  readonly isConnected: boolean;
  readonly networkName: string;
  readonly blockNumber: number;
  readonly nodeVersion: string;
  readonly chainType: string;
  readonly lastConnectionTime: Date;
}

export class PolkadotConnectionManager {
  private apiInstance: ApiPromise | null = null;
  private keyringInstance: Keyring | null = null;
  private wsProvider: WsProvider | null = null;
  private connectionConfig: PolkadotConnectionConfig;
  private reconnectAttempts: number = 0;
  private isReconnecting: boolean = false;
  private connectionListeners: Array<(status: PolkadotConnectionStatus) => void> = [];

  constructor(config: PolkadotConnectionConfig) {
    this.connectionConfig = config;
  }

  public async initializeConnection(): Promise<ApiPromise> {
    try {
      await this.waitForCryptoReady();
      await this.establishWebSocketConnection();
      await this.createApiInstance();
      await this.initializeKeyring();
      await this.setupConnectionEventListeners();
      
      logger.info('Polkadot connection initialized successfully', {
        endpoint: this.connectionConfig.wsEndpoint,
        network: this.connectionConfig.networkName,
      });

      return this.apiInstance as ApiPromise;
    } catch (connectionError) {
      logger.error('Failed to initialize Polkadot connection', {
        error: connectionError,
        endpoint: this.connectionConfig.wsEndpoint,
      });
      throw new Error(`Polkadot connection initialization failed: ${connectionError}`);
    }
  }

  public async getApiInstance(): Promise<ApiPromise> {
    if (!this.apiInstance || !this.apiInstance.isConnected) {
      logger.warn('API instance not available, attempting to reconnect');
      return await this.initializeConnection();
    }
    return this.apiInstance;
  }

  public getKeyringInstance(): Keyring {
    if (!this.keyringInstance) {
      throw new Error('Keyring not initialized. Call initializeConnection first.');
    }
    return this.keyringInstance;
  }

  public async getConnectionStatus(): Promise<PolkadotConnectionStatus> {
    try {
      const api = await this.getApiInstance();
      const [blockNumber, nodeVersion, chainType] = await Promise.all([
        api.query['system']?.['number']?.() || Promise.resolve({ toNumber: () => 0 }),
        api.rpc.system.version(),
        api.rpc.system.chainType(),
      ]);

      return {
        isConnected: api.isConnected,
        networkName: this.connectionConfig.networkName,
        blockNumber: (blockNumber as any).toNumber?.() || 0,
        nodeVersion: nodeVersion.toString(),
        chainType: chainType.toString(),
        lastConnectionTime: new Date(),
      };
    } catch (statusError) {
      logger.error('Failed to get connection status', { error: statusError });
      return {
        isConnected: false,
        networkName: this.connectionConfig.networkName,
        blockNumber: 0,
        nodeVersion: 'unknown',
        chainType: 'unknown',
        lastConnectionTime: new Date(),
      };
    }
  }

  public addConnectionListener(listener: (status: PolkadotConnectionStatus) => void): void {
    this.connectionListeners.push(listener);
  }

  public removeConnectionListener(listener: (status: PolkadotConnectionStatus) => void): void {
    const listenerIndex = this.connectionListeners.indexOf(listener);
    if (listenerIndex > -1) {
      this.connectionListeners.splice(listenerIndex, 1);
    }
  }

  public async disconnectFromNetwork(): Promise<void> {
    try {
      if (this.apiInstance) {
        await this.apiInstance.disconnect();
        this.apiInstance = null;
      }
      
      if (this.wsProvider) {
        await this.wsProvider.disconnect();
        this.wsProvider = null;
      }

      this.keyringInstance = null;
      this.reconnectAttempts = 0;
      this.isReconnecting = false;

      logger.info('Disconnected from Polkadot network successfully');
    } catch (disconnectionError) {
      logger.error('Error during disconnection', { error: disconnectionError });
      throw new Error(`Disconnection failed: ${disconnectionError}`);
    }
  }

  private async waitForCryptoReady(): Promise<void> {
    const cryptoReadyTimeout = setTimeout(() => {
      throw new Error('Crypto initialization timeout');
    }, this.connectionConfig.connectionTimeout);

    try {
      await cryptoWaitReady();
      clearTimeout(cryptoReadyTimeout);
      logger.debug('Crypto libraries initialized successfully');
    } catch (cryptoError) {
      clearTimeout(cryptoReadyTimeout);
      throw new Error(`Crypto initialization failed: ${cryptoError}`);
    }
  }

  private async establishWebSocketConnection(): Promise<void> {
    this.wsProvider = new WsProvider(
      this.connectionConfig.wsEndpoint,
      this.connectionConfig.connectionTimeout,
    );

    const connectionPromise = new Promise<void>((resolve, reject) => {
      const connectionTimeout = setTimeout(() => {
        reject(new Error('WebSocket connection timeout'));
      }, this.connectionConfig.connectionTimeout);

      this.wsProvider?.on('connected', () => {
        clearTimeout(connectionTimeout);
        logger.debug('WebSocket connection established');
        resolve();
      });

      this.wsProvider?.on('error', (error) => {
        clearTimeout(connectionTimeout);
        logger.error('WebSocket connection error', { error });
        reject(error);
      });
    });

    await connectionPromise;
  }

  private async createApiInstance(): Promise<void> {
    if (!this.wsProvider) {
      throw new Error('WebSocket provider not initialized');
    }

    this.apiInstance = await ApiPromise.create({
      provider: this.wsProvider as ProviderInterface,
      throwOnConnect: true,
    });

    await this.apiInstance.isReady;
    logger.debug('API instance created and ready');
  }

  private async initializeKeyring(): Promise<void> {
    this.keyringInstance = new Keyring({ type: 'sr25519' });
    logger.debug('Keyring initialized with sr25519 type');
  }

  private async setupConnectionEventListeners(): Promise<void> {
    if (!this.apiInstance || !this.wsProvider) {
      throw new Error('API instance or WebSocket provider not available');
    }

    this.wsProvider.on('connected', async () => {
      logger.info('WebSocket reconnected successfully');
      this.reconnectAttempts = 0;
      this.isReconnecting = false;
      await this.notifyConnectionListeners();
    });

    this.wsProvider.on('disconnected', async () => {
      logger.warn('WebSocket disconnected');
      await this.handleConnectionLoss();
    });

    this.wsProvider.on('error', async (error) => {
      logger.error('WebSocket error occurred', { error });
      await this.handleConnectionError(error);
    });

    this.apiInstance.on('connected', async () => {
      logger.info('API connected successfully');
      await this.notifyConnectionListeners();
    });

    this.apiInstance.on('disconnected', async () => {
      logger.warn('API disconnected');
      await this.handleConnectionLoss();
    });

    this.apiInstance.on('error', async (error) => {
      logger.error('API error occurred', { error });
      await this.handleConnectionError(error);
    });
  }

  private async handleConnectionLoss(): Promise<void> {
    if (this.isReconnecting) {
      return;
    }

    this.isReconnecting = true;
    logger.info('Attempting to reconnect to Polkadot network');

    while (this.reconnectAttempts < this.connectionConfig.reconnectAttempts) {
      try {
        await this.waitForReconnectDelay();
        await this.initializeConnection();
        logger.info('Reconnection successful');
        return;
      } catch (reconnectionError) {
        this.reconnectAttempts++;
        logger.warn('Reconnection attempt failed', {
          attempt: this.reconnectAttempts,
          maxAttempts: this.connectionConfig.reconnectAttempts,
          error: reconnectionError,
        });
      }
    }

    this.isReconnecting = false;
    logger.error('All reconnection attempts exhausted');
    throw new Error('Failed to reconnect to Polkadot network');
  }

  private async handleConnectionError(error: Error): Promise<void> {
    logger.error('Connection error detected', { error });
    
    if (!this.isReconnecting) {
      await this.handleConnectionLoss();
    }
  }

  private async waitForReconnectDelay(): Promise<void> {
    return new Promise(resolve => {
      setTimeout(resolve, this.connectionConfig.reconnectDelay);
    });
  }

  private async notifyConnectionListeners(): Promise<void> {
    try {
      const connectionStatus = await this.getConnectionStatus();
      this.connectionListeners.forEach(listener => {
        try {
          listener(connectionStatus);
        } catch (listenerError) {
          logger.error('Connection listener error', { error: listenerError });
        }
      });
    } catch (notificationError) {
      logger.error('Failed to notify connection listeners', { error: notificationError });
    }
  }
}

export const createPolkadotConnectionManager = (config: PolkadotConnectionConfig): PolkadotConnectionManager => {
  return new PolkadotConnectionManager(config);
};

export const getDefaultPolkadotConfig = (): PolkadotConnectionConfig => {
  return {
    wsEndpoint: process.env['POLKADOT_WS_ENDPOINT'] || 'wss://rpc.polkadot.io',
    networkName: process.env['POLKADOT_NETWORK'] || 'polkadot',
    connectionTimeout: 30000,
    reconnectAttempts: 5,
    reconnectDelay: 5000,
  };
};