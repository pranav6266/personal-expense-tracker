#!/usr/bin/env bash
# Runs the JavaFX desktop client in Docker, drawing on the host's X11 display.
# Start the API first with `docker compose up -d`.
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
API_BASE_URL="${EXPENSE_API_BASE_URL:-http://localhost:${API_PORT:-8080}}"
IMAGE="expense-tracker-desktop:local"

for cmd in docker xhost; do
    command -v "$cmd" >/dev/null 2>&1 || { echo "Missing required command: $cmd" >&2; exit 1; }
done
: "${DISPLAY:?DISPLAY is not set; run this from a graphical session}"

xhost +local:docker >/dev/null
trap 'xhost -local:docker >/dev/null 2>&1 || true' EXIT

docker build -q -t "$IMAGE" "$ROOT_DIR/desktop" >/dev/null
docker run --rm \
    --network host \
    -e DISPLAY \
    -v /tmp/.X11-unix:/tmp/.X11-unix \
    -v "$ROOT_DIR/desktop:/workspace" \
    "$IMAGE" \
    ./gradlew run --no-daemon -Dexpense.api.baseUrl="$API_BASE_URL"
