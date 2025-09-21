# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Common Development Commands

### Running the Application
```bash
# Run with Spring Boot (default profile)
./mvnw spring-boot:run

# Run with specific profile
./mvnw spring-boot:run -Dspring.profiles.active=dev

# Quick run via Makefile
make run
```

### Testing
```bash
# Run all tests
./mvnw test
make test

# Run a specific test class
./mvnw test -Dtest=BattleServiceTest

# Run a specific test method
./mvnw test -Dtest=BattleServiceTest#testBattleWithFastestAttacker

# Run edge case tests (damage variation tests)
./mvnw test -Dtest=BattleServiceTests#testBattleWithMinimumDamage
./mvnw test -Dtest=BattleServiceTests#testBattleWithMaximumDamage

# Generate coverage report
./mvnw test jacoco:report
make coverage
# View report at: target/site/jacoco/index.html

# Run tests with verification (includes coverage check - min 80%)
./mvnw verify
make verify
```

### Building
```bash
# Build JAR
./mvnw clean package
make build

# Clean build artifacts
./mvnw clean
make clean
```

## Architecture Overview

### Domain Model
The application uses a layered architecture with clear separation of concerns:
- **Controllers** (`/api/characters`, `/api/battles`) - REST endpoints with validation
- **Services** - Business logic for character management and battle simulation
- **Repository** - In-memory storage abstraction with potential for JPA migration
- **DTOs** - Request/Response objects separate from domain models

### Key Design Patterns
1. **Strategy Pattern**: `StatsCalculator` interface with job-specific implementations (WarriorStatsCalculator, ThiefStatsCalculator, MageStatsCalculator)
2. **Factory Pattern**: `StatsFactory` creates appropriate calculator based on job type
3. **Repository Pattern**: `CharacterRepository` interface with InMemoryCharacterRepository implementation
4. **DTO Pattern**: Separate request/response objects from domain models

### Battle System Implementation
The battle system (`BattleService`) implements turn-based combat with:
- Speed-based initiative (higher speed attacks first)
- Random speed tie resolution via `ThreadLocalRandom.nextBoolean()`
- Damage variation: random damage between `attack/2` and `attack` (inclusive)
- Precomputed attack and speed values based on job formulas
- Comprehensive battle logging
- Micrometer metrics for observability
- Protected `calculateDamage` method for testing edge cases

### Character Stats Formulas
Each job class has unique formulas for attack and speed calculations, implemented via the Strategy pattern:
- **Warrior**: Attack = 0.8 * STR + 0.2 * DEX, Speed = 0.6 * DEX + 0.2 * INT
- **Thief**: Attack = 0.25 * STR + 1.0 * DEX + 0.25 * INT, Speed = 0.8 * DEX
- **Mage**: Attack = 0.2 * STR + 0.2 * DEX + 1.2 * INT, Speed = 0.4 * DEX + 0.1 * STR

### Validation
- Name: 4-15 characters, pattern `^[a-zA-Z_]+$`
- Job: Must be WARRIOR, THIEF, or MAGE
- Battle: Characters must be different and both alive

### Configuration
- Default port: 8080
- Profiles: dev (with CORS enabled), prod
- Coverage requirement: 80% line coverage
- Health checks via Spring Actuator at `/actuator/health`