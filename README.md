# Neo RPG API (Spring Boot 3, Java 17)

## 1) Project Overview
A clean, production‑minded REST API for managing RPG characters and simulating battles. Built with Spring Boot 3 and Java 17, the service stores all state in memory and emphasizes testability, clear architecture, and recruiter‑friendly verification. The codebase demonstrates mature engineering patterns (layered design, DTO isolation, strategy/template patterns), robust error handling, and enforced code coverage.

## 2) Features Implemented
- F1: Create character with validation (name regex/length, job enum) + unit tests
- F2: List all characters
- F3: Get character details by ID
- F4: Simulate battle with detailed turn logs (deterministic order; tested)
- Extras:
  - Global error handling: 422 (validation), 404 (not found), 409 (invalid state)
  - Clear validation messages on DTOs
  - Logging with MDC correlation IDs for battles
  - Health/actuator endpoints; optional OpenAPI docs
  - JaCoCo coverage gate ≥ 80% (build fails if below)

## 3) Architecture & Design
Layered architecture: Controller → Service → Repository. The API surface uses DTOs only (no domain leakage). Job‑specific stat formulas use the Strategy pattern; battle flow uses a Template Method‑style orchestration with clear logging and state updates. Rationale and trade‑offs are documented in ADRs (docs/), reflecting production‑ready decision making even for an in‑memory assignment.

## 4) How to Run
No database or external services required.

```bash
# Run locally (default profile)
./mvnw spring-boot:run

# Run tests (JUnit 5 + MockMvc)
./mvnw test

# Make targets (optional)
make run         # spring-boot:run
make test        # mvnw test
make coverage    # test + open coverage path
```

Service starts on http://localhost:8080.

## 5) API Endpoints
- POST /api/characters
- GET /api/characters
- GET /api/characters/{id}
- POST /api/battles

Examples:

```bash
# Create a character
curl -sS -X POST http://localhost:8080/api/characters \
  -H 'Content-Type: application/json' \
  -d '{"name":"Arthur_Hero","job":"WARRIOR"}'
```

```bash
# Start a battle
curl -sS -X POST http://localhost:8080/api/battles \
  -H 'Content-Type: application/json' \
  -d '{"attackerId":"<UUID>","defenderId":"<UUID>"}'
```

Notes:
- JSON only (controllers set produces=application/json)
- Common errors: 422 (bad input), 404 (unknown ID), 409 (dead character or same attacker/defender)

## 6) Testing & Coverage
- Test stack: JUnit 5, Spring Boot Test, MockMvc
- Coverage: JaCoCo gate at 80% (build fails if below)
- Report: target/site/jacoco/index.html
- Coverage includes unit tests for character creation, strategy formulas, battle flow, and integration tests for controllers (422/404/409, e.g., dead character and same attacker/defender)

## 7) Submission Notes
Reviewers can validate quickly with:

```bash
# Run tests
./mvnw test

# Or run the API locally
./mvnw spring-boot:run
```

Create a submission bundle:

```bash
git bundle create submission-drapala.bundle --all
```

This project is intentionally in‑memory but structured for real‑world evolution (profiles, health, logs, optional OpenAPI), showcasing clean boundaries, clarity, and reliability expected in production services.
