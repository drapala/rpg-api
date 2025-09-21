# 🏗️ Architecture & Design Decisions

## Overview
Although the assignment is in-memory, the solution demonstrates how it could evolve into a production-grade architecture.
This document outlines the architectural decisions, design patterns, and engineering best practices applied to the Neo RPG API. While the requirements specify a simple in-memory solution, this implementation demonstrates production-ready thinking and scalability considerations.

## 🎯 Core Principles

### 1. **Separation of Concerns**
Each layer has a single, well-defined responsibility:
- **Controllers**: HTTP handling, request/response mapping
- **Services**: Business logic and orchestration
- **Repositories**: Data persistence abstraction
- **DTOs**: API contracts, decoupled from domain

### 2. **SOLID Principles**
- **Single Responsibility**: Each class has one reason to change
- **Open/Closed**: Extensible for new job types without modifying existing code
- **Liskov Substitution**: Interfaces allow swapping implementations
- **Interface Segregation**: Focused interfaces (e.g., `CharacterRepository` vs `BattleService`)
- **Dependency Inversion**: Depend on abstractions, not concretions

### 3. **Domain-Driven Design**
- Rich domain models with behavior, not just data
- Ubiquitous language: `Character`, `Job`, `Battle` match business terminology
- Value objects for immutable concepts (`Stats`, `BattleResult`)

## 📐 Architecture Layers

```
┌─────────────────────────────────────────────────────────┐
│                    API Gateway (Future)                  │
├─────────────────────────────────────────────────────────┤
│                   Controller Layer                       │
│         @RestController, @Valid, @RequestMapping        │
├─────────────────────────────────────────────────────────┤
│                    Service Layer                         │
│           Business Logic, Transactions, Rules           │
├─────────────────────────────────────────────────────────┤
│                  Repository Layer                        │
│          Data Access Abstraction (In-Memory)            │
├─────────────────────────────────────────────────────────┤
│                   Domain Models                          │
│              Character, Job, Stats, Battle              │
└─────────────────────────────────────────────────────────┘
```

## 🔄 Design Patterns Applied

### 1. **Repository Pattern**
```java
public interface CharacterRepository {
    Character save(Character character);
    Optional<Character> findById(UUID id);
    List<Character> findAll();
}
```
**Why**: Abstracts data access, making it trivial to switch from in-memory to database.

### 2. **Builder Pattern**
```java
Character warrior = Character.builder()
    .name("Arthur")
    .job(Job.WARRIOR)
    .stats(Stats.forJob(Job.WARRIOR))
    .build();
```
**Why**: Complex object construction with validation and computed fields.

### 3. **Strategy Pattern (Job-Specific Calculations)**
```java
public interface StatsCalculator {
    int calculateAttack(Stats stats);
    int calculateSpeed(Stats stats);
}

@Component
public class WarriorStatsCalculator implements StatsCalculator {
    // Warrior-specific formulas
}
```
**Why**: Each job has different formulas; extensible for new jobs without modifying existing code.

### 4. **Factory Pattern**
```java
@Component
public class CharacterFactory {
    public Character createCharacter(String name, Job job) {
        Stats initialStats = statsFactory.createForJob(job);
        return Character.builder()
            .id(UUID.randomUUID())
            .name(name)
            .job(job)
            .stats(initialStats)
            .currentLifePoints(initialStats.getLifePoints())
            .build();
    }
}
```
**Why**: Centralizes complex creation logic with proper initialization.

### 5. **Template Method (Battle Flow)**
```java
public abstract class BattleEngine {
    public final BattleResult executeBattle(Character attacker, Character defender) {
        BattleLog log = new BattleLog();
        log.recordStart(attacker, defender);
        
        while (bothAlive(attacker, defender)) {
            Character first = determineFirstAttacker(attacker, defender);
            Character second = (first == attacker) ? defender : attacker;
            
            executeRound(first, second, log);
        }
        
        return finalizeBattle(attacker, defender, log);
    }
    
    protected abstract Character determineFirstAttacker(Character a, Character b);
    protected abstract void executeRound(Character first, Character second, BattleLog log);
}
```
**Why**: Defines battle skeleton while allowing customization of specific steps.

## 🛡️ API Design Best Practices

### 1. **RESTful Resource Naming**
```
POST   /api/characters          # Create (noun, plural)
GET    /api/characters          # List all
GET    /api/characters/{id}     # Get one
POST   /api/battles             # Create battle (action as resource)
```

### 2. **Consistent Response Structure**
```json
// Success
{
  "data": { ... },
  "timestamp": "2024-11-14T10:30:00Z"
}

// Error
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid input",
    "details": { ... }
  },
  "timestamp": "2024-11-14T10:30:00Z"
}
```

### 3. **Proper HTTP Status Codes**
- `201 Created` - Character successfully created
- `200 OK` - Successful retrieval/battle
- `404 Not Found` - Character doesn't exist
- `422 Unprocessable Entity` - Validation failed
- `409 Conflict` - Character already in battle/dead

### 4. **Idempotency Considerations**
- GET requests are naturally idempotent
- Character creation uses generated UUIDs (client could provide for idempotency)
- Battle outcomes are deterministic given the same random seed

## 🧪 Testing Strategy

### 1. **Test Pyramid**
```
         /\
        /  \    E2E Tests (5%)
       /────\   
      /      \  Integration Tests (25%)
     /────────\
    /          \ Unit Tests (70%)
   /────────────\
```

### 2. **Test Coverage Goals**
- **Critical Path**: 100% (character creation, battles)
- **Business Logic**: >90%
- **Controllers**: >80%
- **Overall**: >85%

### 3. **Test Types**

#### Unit Tests
```java
@Test
void shouldCalculateWarriorAttackCorrectly() {
    // Given
    Stats stats = Stats.builder()
        .strength(10)
        .dexterity(5)
        .build();
    
    // When
    int attack = warriorCalculator.calculateAttack(stats);
    
    // Then
    assertThat(attack).isEqualTo(9); // 80% * 10 + 20% * 5
}
```

#### Integration Tests
```java
@SpringBootTest
@AutoConfigureMockMvc
class CharacterControllerIntegrationTest {
    @Test
    void shouldCreateCharacterAndRetrieve() {
        // Full request/response cycle
    }
}
```

#### Edge Cases
- Empty name, too short, too long, invalid characters
- Unknown job type
- Battle with non-existent characters
- Battle with dead characters
- Speed tie resolution
- Exact HP to 0 scenarios

## 🔐 Security Considerations (Future)

While not required for this assignment, production would include:

1. **Authentication/Authorization**
   ```java
   @PreAuthorize("hasRole('PLAYER')")
   @PostMapping("/characters")
   ```

2. **Rate Limiting**
   ```java
   @RateLimited(value = 10, duration = 1, unit = TimeUnit.MINUTES)
   ```

3. **Input Sanitization**
    - XSS prevention in character names
    - SQL injection prevention (when DB added)

4. **Audit Logging**
   ```java
   @Audited
   public Character createCharacter(CreateCharacterRequest request) {
       // Automatically logs: who, what, when
   }
   ```

## 📊 Performance & Scalability

### Current Implementation (In-Memory)
- **Time Complexity**:
    - Create: O(1)
    - Get by ID: O(1) using HashMap
    - List all: O(n)
    - Battle: O(r) where r = rounds

- **Space Complexity**: O(n) where n = number of characters

### Production Scaling Strategy

1. **Horizontal Scaling**
   ```
   Load Balancer
        │
   ┌────┴────┬────────┬────────┐
   │         │        │        │
   API-1   API-2    API-3    API-4
   │         │        │        │
   └────┬────┴────────┴────────┘
        │
   Redis Cache / PostgreSQL
   ```

2. **Caching Strategy**
    - Character details: Cache for 5 minutes
    - Battle results: Cache for integrity
    - Use Redis for distributed cache

3. **Database Migration Path**
   ```java
   // Current
   @Repository
   public class InMemoryCharacterRepository implements CharacterRepository {
   
   // Future (no controller/service changes needed!)
   @Repository
   @Profile("production")
   public class JpaCharacterRepository implements CharacterRepository {
   ```

## 🚀 Future Enhancements

### Phase 1: Database Integration
- PostgreSQL for persistence
- Flyway for migrations
- Connection pooling with HikariCP

### Phase 2: Enhanced Features
- Character levels and experience
- Items and equipment
- Party battles (multiple characters)
- Tournament mode

### Phase 3: Distributed System
- Microservices architecture
- Event sourcing for battle history
- CQRS for read/write optimization
- WebSocket for real-time battles

## 📈 Monitoring & Observability

### Metrics (Micrometer/Prometheus)
```java
@Timed("character.creation")
public Character createCharacter(...) {
    counter.increment("character.created");
}
```

### Logging Strategy
```java
@Slf4j
public class BattleService {
    public BattleResult battle(UUID attackerId, UUID defenderId) {
        log.info("Battle started: {} vs {}", attackerId, defenderId);
        MDC.put("battleId", UUID.randomUUID().toString());
        // Structured logging with correlation ID
    }
}
```

### Health Checks
```java
@Component
public class CharacterRepositoryHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        return Health.up()
            .withDetail("characters", repository.count())
            .build();
    }
}
```

## 🔄 CI/CD Pipeline (Suggested)

```yaml
pipeline:
  - stage: build
    - compile
    - unit-tests
    
  - stage: quality
    - integration-tests
    - sonarqube-analysis
    - security-scan
    
  - stage: package
    - docker-build
    - docker-push
    
  - stage: deploy
    - deploy-staging
    - smoke-tests
    - deploy-production
```

## 📝 Code Quality Standards

### 1. **Clean Code Principles**
- Methods < 20 lines
- Classes < 200 lines
- Cyclomatic complexity < 10
- Clear, intention-revealing names

### 2. **Documentation**
```java
/**
 * Executes a battle between two characters until one is defeated.
 * 
 * @param attackerId UUID of the attacking character
 * @param defenderId UUID of the defending character
 * @return BattleResult containing winner, loser, and detailed battle log
 * @throws CharacterNotFoundException if either character doesn't exist
 * @throws IllegalStateException if either character is already dead
 */
public BattleResult executeBattle(UUID attackerId, UUID defenderId) {
```

### 3. **Error Handling**
```java
public class CharacterNotFoundException extends RuntimeException {
    public CharacterNotFoundException(UUID id) {
        super(String.format("Character with ID %s not found", id));
    }
}
```

## 💡 Key Takeaways

1. **Over-engineering wisely**: The solution is simple but demonstrates knowledge of complex patterns
2. **Production mindset**: Every decision considers future scalability
3. **Test-first approach**: High coverage ensures reliability
4. **Clean boundaries**: Each layer has clear responsibilities
5. **Documentation as code**: This document is part of the deliverable

## 📚 References

- [Spring Boot Best Practices](https://www.baeldung.com/spring-boot-best-practices)
- [RESTful API Design](https://restfulapi.net/)
- [Domain-Driven Design](https://martinfowler.com/bliki/DomainDrivenDesign.html)
- [Effective Java](https://www.oreilly.com/library/view/effective-java/9780134686097/)
- [Clean Code](https://www.oreilly.com/library/view/clean-code-a/9780136083238/)

---

**Note**: This architecture demonstrates production-ready thinking while maintaining the simplicity required by the assignment. The patterns and practices shown here scale from proof-of-concept to enterprise systems.