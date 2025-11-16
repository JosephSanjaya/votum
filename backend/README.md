# Votum - Polkadot Government Election Voting System

A transparent, corruption-resistant government election voting system built on Polkadot blockchain technology using Node.js backend infrastructure.

## 🌟 Features

### Core Functionality
- **Transparent Voting**: All votes recorded on blockchain for public verification
- **Voter Authentication**: Secure voter registration and identity verification
- **Immutable Records**: Blockchain-based vote storage preventing tampering
- **Real-time Results**: Live election result aggregation and display
- **Audit Trail**: Complete voting history for transparency and accountability
- **Privacy Protection**: Voter anonymity while maintaining vote verifiability

### Technical Features
- **Polkadot Integration**: Built on Polkadot's shared security model
- **RESTful API**: Comprehensive endpoints for frontend integration
- **TypeScript**: Type-safe development with strict validation
- **Database Integration**: PostgreSQL with Prisma ORM
- **Security**: Rate limiting, CORS, helmet, and comprehensive validation
- **Logging**: Structured logging with Winston
- **Error Handling**: Comprehensive error handling and recovery

## 🏗️ Architecture

### Technology Stack
- **Backend**: Node.js with TypeScript
- **Blockchain**: Polkadot.js API for substrate interaction
- **Database**: PostgreSQL with Prisma ORM
- **Caching**: Redis for performance optimization
- **Security**: Helmet, CORS, Rate Limiting, JWT Authentication
- **Logging**: Winston with structured logging
- **Testing**: Jest with comprehensive test coverage

### Project Structure
```
backend/
├── src/
│   ├── config/           # Configuration files
│   │   └── polkadot.config.ts
│   ├── controllers/      # Request handlers (future expansion)
│   ├── middleware/       # Express middleware (future expansion)
│   ├── models/          # Data models (future expansion)
│   ├── routes/          # API route definitions
│   │   ├── authentication.routes.ts
│   │   ├── election.routes.ts
│   │   ├── voting.routes.ts
│   │   ├── results.routes.ts
│   │   └── audit.routes.ts
│   ├── services/        # Business logic services
│   │   ├── blockchain.service.ts
│   │   ├── voter-registration.service.ts
│   │   ├── voting.service.ts
│   │   └── election-results.service.ts
│   ├── types/           # TypeScript type definitions
│   │   └── election.types.ts
│   ├── utils/           # Utility functions
│   │   └── logger.ts
│   └── index.ts         # Application entry point
├── prisma/              # Database schema and migrations
│   └── schema.prisma
├── tests/               # Test files
│   ├── unit/
│   └── integration/
├── docs/                # Documentation
├── scripts/             # Utility scripts
└── docker/              # Docker configuration
```

## 🚀 Quick Start

### Prerequisites
- Node.js 18+ and npm 8+
- PostgreSQL 13+
- Redis 6+
- Git

### Installation

1. **Clone the repository**
```bash
git clone <repository-url>
cd polkadot-hackaton/backend
```

2. **Install dependencies**
```bash
npm install
```

3. **Environment setup**
```bash
cp .env.example .env
# Edit .env with your configuration
```

4. **Database setup**
```bash
# Generate Prisma client
npm run db:generate

# Run database migrations
npm run db:migrate

# Seed database (optional)
npm run db:seed
```

5. **Start development server**
```bash
npm run dev
```

The server will start on `http://localhost:3000`

### Environment Variables

Create a `.env` file based on `.env.example`:

```env
# Server Configuration
NODE_ENV=development
PORT=3000
HOST=localhost

# Database Configuration
DATABASE_URL="postgresql://username:password@localhost:5432/polkadot_election_db"

# Redis Configuration
REDIS_URL="redis://localhost:6379"

# JWT Configuration
JWT_SECRET="your-super-secret-jwt-key"
JWT_REFRESH_SECRET="your-super-secret-refresh-jwt-key"

# Polkadot Network Configuration
POLKADOT_WS_ENDPOINT="wss://rpc.polkadot.io"
POLKADOT_NETWORK="polkadot"

# Security Configuration
BCRYPT_ROUNDS=12
RATE_LIMIT_WINDOW_MS=900000
RATE_LIMIT_MAX_REQUESTS=100
```

## 📚 API Documentation

### Authentication Endpoints

#### Register Voter
```http
POST /api/auth/register
Content-Type: application/json

{
  "nationalId": "1234567890",
  "email": "voter@example.com",
  "password": "securePassword123",
  "fullName": "John Doe",
  "dateOfBirth": "1990-01-01",
  "address": "123 Main St, City, Country",
  "phoneNumber": "+1234567890"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "voter@example.com",
  "password": "securePassword123"
}
```

#### Verify Identity
```http
POST /api/auth/verify
Content-Type: application/json

{
  "nationalId": "1234567890",
  "verificationCode": "ABC123",
  "documentProof": "base64_encoded_document"
}
```

### Election Endpoints

#### Get All Elections
```http
GET /api/elections?page=1&limit=10
```

#### Get Election Details
```http
GET /api/elections/{electionId}
```

#### Create Election
```http
POST /api/elections
Content-Type: application/json

{
  "title": "Presidential Election 2024",
  "description": "National presidential election",
  "startTime": "2024-01-01T00:00:00Z",
  "endTime": "2024-01-02T00:00:00Z",
  "registrationDeadline": "2023-12-31T23:59:59Z",
  "electionType": "PRESIDENTIAL",
  "createdBy": "admin-user-id",
  "candidates": [
    {
      "name": "Candidate A",
      "party": "Party A",
      "description": "Candidate description",
      "manifesto": "Policy platform"
    }
  ]
}
```

### Voting Endpoints

#### Cast Vote
```http
POST /api/voting/cast
Content-Type: application/json

{
  "electionId": "election-uuid",
  "voterId": "voter-uuid",
  "candidateId": "candidate-uuid",
  "voterSignature": "cryptographic_signature"
}
```

#### Verify Vote
```http
GET /api/voting/verify/{transactionHash}
```

#### Get Voting Status
```http
GET /api/voting/status/{electionId}/{voterId}
```

### Results Endpoints

#### Get Election Results
```http
GET /api/results/{electionId}
```

#### Get Live Results
```http
GET /api/results/{electionId}/live
```

#### Calculate Results
```http
POST /api/results/{electionId}/calculate
Content-Type: application/json

{
  "requestedBy": "admin-user-id"
}
```

#### Finalize Results
```http
POST /api/results/{electionId}/finalize
Content-Type: application/json

{
  "finalizedBy": "admin-user-id"
}
```

### Audit Endpoints

#### Get Audit Trail
```http
GET /api/audit/trail/{electionId}?page=1&limit=50
```

#### Get Security Events
```http
GET /api/audit/security-events?severity=HIGH&eventType=FAILED_LOGIN
```

#### Blockchain Verification
```http
GET /api/audit/blockchain-verification/{transactionHash}
```

## 🔒 Security Features

### Authentication & Authorization
- JWT-based authentication with refresh tokens
- Password hashing with bcrypt (configurable rounds)
- Rate limiting to prevent abuse
- CORS protection with configurable origins

### Input Validation
- Comprehensive input validation using Joi
- SQL injection prevention with Prisma ORM
- XSS protection with helmet middleware
- Request size limiting

### Blockchain Security
- Cryptographic vote signatures
- Immutable blockchain records
- Transaction verification
- Multi-signature support (future enhancement)

### Privacy Protection
- Voter anonymity preservation
- Encrypted vote data
- Secure key management
- GDPR compliance considerations

## 🧪 Testing

### Run Tests
```bash
# Run all tests
npm test

# Run tests in watch mode
npm run test:watch

# Run tests with coverage
npm run test:coverage
```

### Test Structure
- **Unit Tests**: Individual service and utility testing
- **Integration Tests**: API endpoint testing
- **Security Tests**: Authentication and authorization testing

## 📊 Monitoring & Logging

### Health Checks
```http
GET /health
GET /api/status
```

### Logging
- Structured logging with Winston
- Request/response logging
- Error tracking and alerting
- Performance metrics

### Audit Trail
- Complete action logging
- Blockchain transaction tracking
- Security event monitoring
- User activity tracking

## 🚀 Deployment

### Development
```bash
npm run dev
```

### Production Build
```bash
npm run build
npm start
```

### Docker Deployment
```bash
# Build Docker image
npm run docker:build

# Run Docker container
npm run docker:run
```

### Database Migration
```bash
# Production migration
npm run db:deploy
```

## 🔧 Configuration

### Database Configuration
- PostgreSQL with connection pooling
- Prisma ORM with type safety
- Migration management
- Backup and recovery procedures

### Blockchain Configuration
- Polkadot network connection
- WebSocket provider setup
- Connection retry logic
- Network status monitoring

### Security Configuration
- Rate limiting configuration
- CORS policy setup
- JWT token management
- Password policy enforcement

## 📈 Performance Optimization

### Caching Strategy
- Redis for session management
- Query result caching
- Connection pooling
- CDN integration (future)

### Database Optimization
- Indexed queries
- Connection pooling
- Read replicas (future)
- Query optimization

### Blockchain Optimization
- Connection pooling
- Batch operations
- Gas optimization
- Network monitoring

## 🤝 Contributing

### Development Workflow
1. Fork the repository
2. Create a feature branch
3. Make changes with tests
4. Run linting and tests
5. Submit pull request

### Code Standards
- TypeScript strict mode
- ESLint configuration
- Prettier formatting
- Comprehensive testing

### Git Hooks
- Pre-commit linting
- Pre-push testing
- Commit message validation

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

### Documentation
- API documentation: `/docs/api`
- Architecture guide: `/docs/architecture`
- Deployment guide: `/docs/deployment`

### Community
- GitHub Issues for bug reports
- GitHub Discussions for questions
- Contributing guidelines in CONTRIBUTING.md

## 🗺️ Roadmap

### Phase 1 (Current - MVP)
- [x] Basic voting functionality
- [x] Blockchain integration
- [x] REST API endpoints
- [x] Security implementation

### Phase 2 (Future)
- [ ] Advanced analytics dashboard
- [ ] Multi-language support
- [ ] Mobile application
- [ ] Advanced security features

### Phase 3 (Future)
- [ ] Cross-chain compatibility
- [ ] Advanced governance features
- [ ] AI-powered fraud detection
- [ ] Scalability improvements

## 🏆 Acknowledgments

- Polkadot ecosystem for blockchain infrastructure
- Open source community for tools and libraries
- Security researchers for best practices
- Election transparency advocates for requirements

---

**Built with ❤️ for transparent democracy**