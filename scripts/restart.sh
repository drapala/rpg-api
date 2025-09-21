#!/usr/bin/env bash
set -euo pipefail

PORT=8080

echo "Checking for processes listening on port $PORT..."
if PIDS=$(lsof -ti tcp:$PORT 2>/dev/null); then
  if [[ -n "${PIDS}" ]]; then
    echo "Killing processes on port $PORT: ${PIDS}"
    kill -9 ${PIDS} || true
    sleep 1
  fi
fi

echo "Starting Spring Boot app..."
exec ./mvnw spring-boot:run

