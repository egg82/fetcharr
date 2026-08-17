#!/usr/bin/env bash
set -Eeuo pipefail

# ChatGPT cleaned this original script up a bit
# Memory limit portion also generated - I was not about to dive into that mess

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

# MaxRAM REPLACES the detected memory rather than capping it, so only apply it
# when there is no cgroup limit. With a limit set it is wrong both ways: it
# would raise the ceiling above a 512m limit, and shrink it under a 4g one.
mem_limit() {
  if [[ -r /sys/fs/cgroup/memory.max ]]; then
    cat /sys/fs/cgroup/memory.max                       # cgroup v2: "max" or bytes
  elif [[ -r /sys/fs/cgroup/memory/memory.limit_in_bytes ]]; then
    cat /sys/fs/cgroup/memory/memory.limit_in_bytes     # v1: huge sentinel when unlimited
  else
    echo max
  fi
}

java_args=()
limit="$(mem_limit)"
if [[ ! "$limit" =~ ^[0-9]+$ ]] || (( limit >= 4611686018427387904 )); then
  java_args+=("-XX:MaxRAM=${MAX_RAM:-1g}")
fi

echo "Starting Fetcharr.."

java "${java_args[@]}" -jar /app/fetcharr.jar "$@" &
child=$!

wait "$child"
exit $?
