# Postman Collection Guide (RPG API)

This guide shows two easy ways to create a Postman collection for the Neo RPG API.

## Option A — Import OpenAPI (recommended)
1) Start the API: `./mvnw spring-boot:run` (default http://localhost:8080)
2) In Postman: Import → Link → paste `http://localhost:8080/v3/api-docs` → Import
3) Postman generates a collection with requests for each endpoint.

Notes
- If springdoc isn’t enabled in your env, use Option B.
- Set a Postman environment with `baseUrl = http://localhost:8080`.

## Option B — Manual Collection
1) Create a collection: “Neo RPG API”
2) Add a variable: `baseUrl = http://localhost:8080`
3) Add requests:
- POST `{{baseUrl}}/api/characters` (body: JSON)
- GET `{{baseUrl}}/api/characters`
- GET `{{baseUrl}}/api/characters/{{id}}`
- POST `{{baseUrl}}/api/battles` (body: JSON)

Example bodies
- Create character
```json
{
  "name": "Arthur_Hero",
  "job": "WARRIOR"
}
```
- Battle
```json
{
  "attackerId": "{{attackerId}}",
  "defenderId": "{{defenderId}}"
}
```

## Suggested Environments
- local: `baseUrl = http://localhost:8080`
- dev: `baseUrl = https://dev.example.com`

Common Variables
- `id` — UUID for GET /api/characters/{id}
- `attackerId`, `defenderId` — UUIDs for battles

## Tips
- Save typical headers at collection level: `Content-Type: application/json`
- Use Tests tab to persist IDs:
```js
const json = pm.response.json();
if (json && json.id) pm.environment.set('id', json.id);
```
