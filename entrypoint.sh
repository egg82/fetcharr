#!/usr/bin/env bash
set -Eeuo pipefail

# ChatGPT wrote almost 100% of this, with minor edits

fix_permissions() {
  local path
  for path in /app/config /app/cache /app/logs /app/plugins /tmp; do
    mkdir -p "$path"
    chown -R "${PUID}:${PGID}" "$path"
  done
}

remap_user_group() {
  local current_group current_uid

  if getent group "${APP_USER}" >/dev/null 2>&1; then
    current_group="$(getent group "${APP_USER}" | cut -d: -f3)"
    if [[ "${current_group}" != "${PGID}" ]]; then
      groupmod -o -g "${PGID}" "${APP_USER}"
    fi
  else
    groupadd -o -g "${PGID}" "${APP_USER}"
  fi

  if id -u "${APP_USER}" >/dev/null 2>&1; then
    current_uid="$(id -u "${APP_USER}")"
    if [[ "${current_uid}" != "${PUID}" ]]; then
      usermod -o -u "${PUID}" -g "${PGID}" "${APP_USER}"
    else
      usermod -g "${PGID}" "${APP_USER}" >/dev/null 2>&1 || true
    fi
  else
    useradd -o -u "${PUID}" -g "${PGID}" -d /app -s /sbin/nologin -M "${APP_USER}"
  fi
}

if [[ "$(id -u)" == "0" ]]; then
  echo "Running as root, enabling UID/GID remap"
  echo "APP_USER=${APP_USER} PUID=${PUID} PGID=${PGID}"

  remap_user_group
  fix_permissions

  exec setpriv \
    --reuid="${PUID}" \
    --regid="${PGID}" \
    --clear-groups \
    /app/start.sh "$@"
  # Editor's note: no exit (usually required because fallthrough) because we're running the exec syscall which replaces the running proc entirely
fi

echo "Running as non-root ($(id -u):$(id -g)), skipping remap"
exec /app/start.sh "$@"
