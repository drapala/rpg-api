# RPG API cURL Cookbook

This Markdown provides copy‑paste cURL commands for all endpoints and key edge cases.

Prerequisite
- Base URL: `export BASE_URL=http://localhost:8080`
- Optional pretty printing: install `jq`

## Create Character (F1)

WARRIOR
```bash
curl -sS -X POST "$BASE_URL/api/characters" \
  -H 'Content-Type: application/json' \
  -d '{"name":"Arthur_Hero","job":"WARRIOR"}' | jq .
```

THIEF
```bash
curl -sS -X POST "$BASE_URL/api/characters" \
  -H 'Content-Type: application/json' \
  -d '{"name":"Shadow_Thief","job":"THIEF"}' | jq .
```

MAGE
```bash
curl -sS -X POST "$BASE_URL/api/characters" \
  -H 'Content-Type: application/json' \
  -d '{"name":"Merlin_Wise","job":"MAGE"}' | jq .
```

Capture ID to variable
```bash
CHAR_ID=$(curl -sS "$BASE_URL/api/characters" | jq -r '.[0].id')
echo "$CHAR_ID"
```

## List Characters (F2)
```bash
curl -sS "$BASE_URL/api/characters" | jq .
```

## Get Character Details (F3)
```bash
curl -sS "$BASE_URL/api/characters/$CHAR_ID" | jq .
```

## Battle (F4)

Create two characters and battle
```bash
A=$(curl -sS -X POST "$BASE_URL/api/characters" -H 'Content-Type: application/json' -d '{"name":"Arthur_Hero","job":"WARRIOR"}')
B=$(curl -sS -X POST "$BASE_URL/api/characters" -H 'Content-Type: application/json' -d '{"name":"Shadow_Thief","job":"THIEF"}')
A_ID=$(echo "$A" | jq -r '.id')
B_ID=$(echo "$B" | jq -r '.id')

curl -sS -X POST "$BASE_URL/api/battles" \
  -H 'Content-Type: application/json' \
  -d "{\"attackerId\":\"$A_ID\",\"defenderId\":\"$B_ID\"}" | jq .
```

## Error Scenarios

422 — Validation error (name too short)
```bash
curl -sS -X POST "$BASE_URL/api/characters" \
  -H 'Content-Type: application/json' \
  -d '{"name":"ab","job":"WARRIOR"}' | jq .
```

422 — Validation error (invalid job)
```bash
curl -sS -X POST "$BASE_URL/api/characters" \
  -H 'Content-Type: application/json' \
  -d '{"name":"Bad_Wizard","job":"WIZARD"}' | jq .
```

404 — Character not found
```bash
curl -sS "$BASE_URL/api/characters/00000000-0000-0000-0000-000000000000" | jq .
```

409 — Same attacker/defender
```bash
C=$(curl -sS -X POST "$BASE_URL/api/characters" -H 'Content-Type: application/json' -d '{"name":"Solo_Fighter","job":"WARRIOR"}')
C_ID=$(echo "$C" | jq -r '.id')

curl -sS -X POST "$BASE_URL/api/battles" \
  -H 'Content-Type: application/json' \
  -d "{\"attackerId\":\"$C_ID\",\"defenderId\":\"$C_ID\"}" | jq .
```

409 — Battle with a dead character (example flow)
```bash
# Create two characters
D=$(curl -sS -X POST "$BASE_URL/api/characters" -H 'Content-Type: application/json' -d '{"name":"Tank_Warrior","job":"WARRIOR"}')
E=$(curl -sS -X POST "$BASE_URL/api/characters" -H 'Content-Type: application/json' -d '{"name":"Glass_Mage","job":"MAGE"}')
D_ID=$(echo "$D" | jq -r '.id')
E_ID=$(echo "$E" | jq -r '.id')

# Run a battle until one dies
curl -sS -X POST "$BASE_URL/api/battles" -H 'Content-Type: application/json' \
  -d "{\"attackerId\":\"$D_ID\",\"defenderId\":\"$E_ID\"}" | jq .

# Attempt to battle again with the dead character to trigger 409
curl -sS -X POST "$BASE_URL/api/battles" -H 'Content-Type: application/json' \
  -d "{\"attackerId\":\"$D_ID\",\"defenderId\":\"$E_ID\"}" | jq .
```

## Actuator & Docs (optional)
```bash
curl -sS "$BASE_URL/actuator/health" | jq .
# If springdoc is enabled
curl -sS "$BASE_URL/v3/api-docs" | jq .
```
