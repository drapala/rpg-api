# 🎮 Neo RPG API

A Spring Boot REST API for managing RPG characters and simulating battles. Built as a take-home assignment for Neo Financial.

## 🚀 Tech Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Web** - RESTful APIs
- **Spring Validation** - Input validation
- **Lombok** - Reduce boilerplate
- **JUnit 5 + Mockito** - Testing
- **Maven** - Dependency management

## 📋 Requirements

- ✅ All state stored in memory (no database)
- ✅ Unit tests for character creation and battle system
- ✅ RESTful API design
- ✅ Input validation
- ✅ Comprehensive battle logging

## 🏗️ Project Structure

```
src/
├── main/java/com/neo/rpg/
│   ├── controller/
│   │   ├── CharacterController.java
│   │   └── BattleController.java
│   ├── service/
│   │   ├── CharacterService.java
│   │   └── BattleService.java
│   ├── model/
│   │   ├── Character.java
│   │   ├── Job.java
│   │   └── Stats.java
│   ├── dto/
│   │   ├── CreateCharacterRequest.java
│   │   ├── CharacterResponse.java
│   │   ├── BattleRequest.java
│   │   └── BattleResponse.java
│   ├── repository/
│   │   └── CharacterRepository.java
│   ├── exception/
│   │   └── GlobalExceptionHandler.java
│   └── RpgApiApplication.java
└── test/java/com/neo/rpg/
    ├── service/
    │   ├── CharacterServiceTest.java
    │   └── BattleServiceTest.java
    └── controller/
        └── CharacterControllerTest.java
```

## 🎯 Features

### Character Classes & Stats

| Job | Life Points | Strength | Dexterity | Intelligence | Attack Formula | Speed Formula |
|-----|------------|----------|-----------|--------------|----------------|---------------|
| **Warrior** | 20 | 10 | 5 | 5 | 80% STR + 20% DEX | 60% DEX + 20% INT |
| **Thief** | 15 | 4 | 10 | 4 | 25% STR + 100% DEX + 25% INT | 80% DEX |
| **Mage** | 12 | 5 | 6 | 10 | 20% STR + 20% DEX + 120% INT | 40% DEX + 10% STR |

### Validation Rules

- **Name**: 4-15 characters, only letters and underscore (`^[a-zA-Z_]+$`)
- **Job**: Must be one of `WARRIOR`, `THIEF`, or `MAGE`

## 🔌 API Endpoints

### Create Character
```http
POST /api/characters
Content-Type: application/json

{
  "name": "Arthur_Hero",
  "job": "WARRIOR"
}

Response: 201 Created
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Arthur_Hero",
  "job": "WARRIOR",
  "alive": true,
  "currentLifePoints": 20,
  "maxLifePoints": 20,
  "stats": {
    "strength": 10,
    "dexterity": 5,
    "intelligence": 5
  },
  "attack": 9,
  "speed": 4
}
```

### List Characters
```http
GET /api/characters

Response: 200 OK
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Arthur_Hero",
    "job": "WARRIOR",
    "alive": true
  },
  {
    "id": "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
    "name": "Shadow_Thief",
    "job": "THIEF",
    "alive": false
  }
]
```

### Get Character Details
```http
GET /api/characters/{id}

Response: 200 OK
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Arthur_Hero",
  "job": "WARRIOR",
  "alive": true,
  "currentLifePoints": 15,
  "maxLifePoints": 20,
  "stats": {
    "strength": 10,
    "dexterity": 5,
    "intelligence": 5
  },
  "attack": 9,
  "speed": 4
}
```

### Battle
```http
POST /api/battle
Content-Type: application/json

{
  "attackerId": "550e8400-e29b-41d4-a716-446655440000",
  "defenderId": "6ba7b810-9dad-11d1-80b4-00c04fd430c8"
}

Response: 200 OK
{
  "winnerId": "550e8400-e29b-41d4-a716-446655440000",
  "loserId": "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
  "battleLog": [
    "Battle between Arthur_Hero (WARRIOR) - 20 HP and Shadow_Thief (THIEF) - 15 HP begins!",
    "Shadow_Thief 7 speed was faster than Arthur_Hero 3 speed and will begin this round.",
    "Shadow_Thief attacks Arthur_Hero for 8 damage, Arthur_Hero has 12 HP remaining.",
    "Arthur_Hero attacks Shadow_Thief for 6 damage, Shadow_Thief has 9 HP remaining.",
    "...",
    "Arthur_Hero wins the battle! Arthur_Hero still has 5 HP remaining!"
  ]
}
```

### Error Response Example
```http
POST /api/characters
Content-Type: application/json

{
  "name": "ab",
  "job": "WIZARD"
}

Response: 422 Unprocessable Entity
{
  "errors": {
    "name": "Name must be between 4 and 15 characters",
    "job": "Job must be one of: WARRIOR, THIEF, MAGE"
  }
}
```

## 🏃‍♂️ Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Running the Application

```bash
# Clone the repository
git clone <repository-url>
cd neo-rpg-api

# Run the application
./mvnw spring-boot:run

# The API will be available at
http://localhost:8080
```

### Running Tests

```bash
# Run all tests
./mvnw test

# Run with coverage report
./mvnw test jacoco:report
# Coverage report available at: target/site/jacoco/index.html
```

### Curl Examples

```bash
# Create a character
curl -sS -X POST http://localhost:8080/api/characters \
  -H 'Content-Type: application/json' \
  -d '{"name":"Arthur_Hero","job":"WARRIOR"}' | jq .

# List characters
curl -sS http://localhost:8080/api/characters | jq .

# Get character details
curl -sS http://localhost:8080/api/characters/<UUID> | jq .

# Start a battle
curl -sS -X POST http://localhost:8080/api/battles \
  -H 'Content-Type: application/json' \
  -d '{"attackerId":"<UUID>","defenderId":"<UUID>"}' | jq .
```

### Building for Production

```bash
# Create executable JAR
./mvnw clean package

# Run the JAR
java -jar target/rpg-api-1.0.0.jar
```

## 🧪 Testing Strategy

### Unit Tests
- **CharacterService**: Character creation validation, stats calculation
- **BattleService**: Battle mechanics, speed ties, damage calculation
- **Validation**: Name patterns, job types, edge cases

### Integration Tests
- **API Endpoints**: HTTP status codes, request/response validation
- **Error Handling**: Invalid inputs, not found scenarios

### Test Coverage
- Character Creation (F1): 100% coverage ✅
- Battle System (F4): 100% coverage ✅
- Overall: >90% line coverage

## 📊 Implementation Status

- ✅ F1 - Character Creation (with validation and tests)
- ✅ F2 - Character Listing
- ✅ F3 - Character Details
- ✅ F4 - Battle System (with comprehensive tests)
- ✅ Global exception handling
- ✅ Input validation
- ✅ In-memory repository
- ✅ Battle logging
- ✅ Unit & Integration tests
- 🔄 Swagger/OpenAPI documentation (optional)

## 💡 Design Decisions

1. **Repository Pattern**: Even with in-memory storage, maintains clean separation and enables easy migration to a database
2. **DTO Pattern**: Separates API contracts from domain models
3. **Service Layer**: Business logic isolated from controllers
4. **Builder Pattern**: For constructing complex battle logs
5. **UUID for IDs**: Production-ready approach for unique identifiers
6. **Comprehensive Validation**: Using Spring Validation annotations for robust input handling

## 📝 Submission

Generate the bundle for submission:

```bash
# Ensure all changes are committed
git add .
git commit -m "Complete Neo RPG API implementation"

# Create the bundle
git bundle create submission-yourname.bundle --all

# Verify the bundle
git bundle verify submission-yourname.bundle
```

## 📚 Additional Documentation

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring REST Docs](https://spring.io/guides/tutorials/rest/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)

---

**Author**: Drapala
**Date**: September 2025
**Assignment**: Neo Financial Take-Home (via VanHack)
