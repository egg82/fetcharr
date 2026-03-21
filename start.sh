#!/usr/bin/env bash
set -Eeuo pipefail

# ChatGPT cleaned this original script up a bit

TERM_GRACE_PERIOD="${TERM_GRACE_PERIOD:-10}"

_term() {
  printf "\nCaught SIGTERM, forwarding to app..\n" >&2
  kill -TERM "$child" 2>/dev/null || true

  (
    sleep "$TERM_GRACE_PERIOD"
    if kill -0 "$child" 2>/dev/null; then
      echo "App didn't exit in ${TERM_GRACE_PERIOD}s - force-killing.." >&2
      kill -KILL "$child" 2>/dev/null || true
    fi
  ) &
}

_int() {
  printf "\nCaught SIGINT, forwarding to app..\n" >&2
  kill -INT "$child" 2>/dev/null || true

  (
    sleep "$TERM_GRACE_PERIOD"
    if kill -0 "$child" 2>/dev/null; then
      echo "App didn't exit in ${TERM_GRACE_PERIOD}s - force-killing.." >&2
      kill -KILL "$child" 2>/dev/null || true
    fi
  ) &
}

trap _term SIGTERM
trap _int SIGINT

echo "Starting Fetcharr.."

java -jar /app/fetcharr.jar "$@" &
child=$!

wait "$child"
exit $?
