# Votum 🗳️

> Blockchain-powered election system that makes vote fraud technically impossible. Built on Polkadot with Compose Multiplatform

## What's This About?

Election corruption destroys democracies. Votum fixes that with blockchain technology every vote is cryptographically signed, recorded on Polkadot's immutable ledger, and publicly verifiable. No more vote manipulation. No more rigged elections. Just transparent, trustworthy democracy.

**The Problem**: Traditional elections are vulnerable to fraud at every step from vote casting to counting to result reporting.

**The Solution**: Blockchain makes fraud mathematically impossible. Votes are cryptographically signed, stored immutably, and verifiable by anyone.

## Why Polkadot?

We built on Polkadot because it's the only blockchain that gives us:
- **Shared Security**: Every vote protected by the entire validator network
- **Scalability**: Handle millions of votes without congestion
- **Interoperability**: Connect with other government systems seamlessly

## Tech Stack

**Mobile**: Kotlin Multiplatform + Compose Multiplatform (Android & iOS from one codebase)  
**Backend**: Node.js + TypeScript + Express  
**Blockchain**: Polkadot.js API with SR25519 cryptography  
**Database**: PostgreSQL + Prisma ORM  
**Cache**: Redis  
**Architecture**: Clean Architecture with feature modules

## Features

- 🔐 **Cryptographic Voting**: SR25519 signatures ensure vote authenticity
- 🔗 **Blockchain Recording**: Immutable vote storage on Polkadot
- ✅ **Public Verification**: Anyone can verify votes on-chain
- 📱 **Cross-Platform**: Single codebase for Android and iOS
- 🎨 **Modern UI**: Beautiful Compose Multiplatform interface
- 🔒 **Secure Identity**: Document verification with biometric support
- 📊 **Live Results**: Real-time vote counting with blockchain proof
- 🌐 **Offline Support**: Queue votes when offline, sync when connected

## Quick Start

### Prerequisites

- JDK 17+
- Android Studio Hedgehog or later
- Xcode 15+ (for iOS)
- Node.js 18+ (for backend)

### Setup

1. **Clone the repo**
```bash
git clone https://github.com/yourusername/votum.git
cd votum
```

2. **Configure API endpoint**

Create `local.properties` in the project root:
```properties
api.base.url=http://your-backend-url:3000
```

3. **Install dependencies**
```bash
# The Gradle wrapper handles everything
gradle build
```

### Run on Android

```bash
gradle :composeApp:assembleDebug
```

Or use the run configuration in Android Studio.

### Run on iOS

Open `/iosApp` in Xcode and hit Run, or use the IDE run configuration.

## Project Structure

```
votum/
├── composeApp/          # Main app with navigation
├── core/                # Shared infrastructure
│   ├── networking       # Ktor HTTP client
│   ├── crypto          # SR25519 signing
│   ├── storage         # Secure local storage
│   └── ui              # Design system
├── features/
│   ├── auth/           # Login/logout
│   ├── registration/   # Voter registration
│   ├── identity/       # Document verification
│   ├── election/       # Browse elections
│   ├── vote/           # Cast & verify votes
│   └── result/         # Live results
└── iosApp/             # iOS entry point
```

Each feature module is independent with its own:
- Data layer (API, repository)
- Domain layer (use cases, models)
- Presentation layer (ViewModels, screens)

## How It Works

1. **Register**: User creates account with national ID
2. **Verify**: Upload ID document for verification
3. **Browse**: See available elections and candidates
4. **Vote**: Select candidate, sign with private key
5. **Record**: Vote submitted to Polkadot blockchain
6. **Verify**: Get receipt with transaction hash
7. **Check**: Verify vote on blockchain anytime

Every vote is:
- Signed with voter's private key (never leaves device)
- Validated by backend
- Recorded on Polkadot blockchain
- Publicly verifiable
- Mathematically impossible to forge or change

## Key Technologies

### Compose Multiplatform
Write UI once, run on Android and iOS. No more maintaining separate codebases.

### Kotlin Multiplatform
Share business logic across platforms. Type-safe, modern, and concise.

### Polkadot Blockchain
- **SR25519 Signatures**: Military-grade cryptography
- **Substrate Framework**: Customizable blockchain runtime
- **Shared Security**: Protected by 1000+ validators
- **6-second Finality**: Fast vote confirmation

### Clean Architecture
- Testable: Each layer tested independently
- Maintainable: Clear separation of concerns
- Scalable: Add features without breaking existing code

## Dependencies

**Shared (Kotlin Multiplatform)**:
- `org.jetbrains.compose.multiplatform` - UI framework
- `io.ktor:ktor-client` - HTTP networking
- `org.jetbrains.kotlinx:kotlinx-serialization` - JSON parsing
- `org.jetbrains.kotlinx:kotlinx-coroutines` - Async operations
- `io.insert-koin:koin-core` - Dependency injection

**Android Specific**:
- `androidx.security:security-crypto` - Secure storage
- `androidx.biometric:biometric` - Fingerprint/Face ID

**iOS Specific**:
- Native keychain integration
- Native biometric authentication

## Backend Setup

The backend handles authentication, election management, and blockchain coordination.

```bash
cd backend
npm install
cp .env.example .env
# Edit .env with your Polkadot endpoint
npm run dev
```

See [backend/README.md](backend/README.md) for detailed setup.

## Development

### Adding a Feature Module

1. Create module: `features/your-feature/`
2. Add to `settings.gradle.kts`
3. Follow the structure:
```
your-feature/
├── data/           # API, repository
├── domain/         # Use cases, models
├── presentation/   # ViewModels, screens
└── di/            # Koin module
```

### Code Style

- Write expressive code that doesn't need comments
- Use Kotlin conventions
- Follow Clean Architecture principles


**Built with ❤️ for transparent democracy**
