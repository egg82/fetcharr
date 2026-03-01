#!/usr/bin/env bash
set -Eeuo pipefail
shopt -s nullglob

# ChatGPT largely wrote this because, frankly, I couldn't be bothered - it has no effect on the final product

IMAGE_NAME="${IMAGE_NAME:-fetcharr}"
DOCKERHUB_USER="${DOCKERHUB_USER:-}"
CONTAINERFILE="${CONTAINERFILE:-./Containerfile}"

candidates=()
for f in ./App/target/*.jar; do
  name="$(basename "$f")"
  case "$name" in
    *-sources.jar|*-javadoc.jar|*-javadoc-resources.jar|*-test-javadoc-resources.jar|original-*.jar)
      continue
      ;;
    *)
      candidates+=("$f")
      ;;
  esac
done

if (( ${#candidates[@]} == 0 )); then
  echo "No usable JAR found in ./App/target" >&2
  exit 1
fi

# Prefer shortest basename, which is usually the main artifact
JAR_FILE="${JAR_FILE:-$(printf '%s\n' "${candidates[@]}" | awk -F/ '{print length($NF), $0}' | sort -n | head -n1 | cut -d' ' -f2-)}"
JAR_BASENAME="$(basename "$JAR_FILE")"

# Extract version from fetcharr-1.0.0.jar -> 1.0.0
VERSION="${VERSION:-}"
if [[ -z "$VERSION" ]]; then
  if [[ "$JAR_BASENAME" =~ ^${IMAGE_NAME}-(.+)\.jar$ ]]; then
    VERSION="${BASH_REMATCH[1]}"
  else
    echo "Could not determine version from JAR name: $JAR_BASENAME" >&2
    echo "Set VERSION explicitly if needed." >&2
    exit 1
  fi
fi

LOCAL_LATEST="${IMAGE_NAME}:latest"
LOCAL_VERSIONED="${IMAGE_NAME}:${VERSION}"

echo "Using JAR:     $JAR_FILE"
echo "Version:       $VERSION"
echo "Building tag:  $LOCAL_VERSIONED"

podman build \
  --pull=always \
  -f "$CONTAINERFILE" \
  --build-arg "JAR_FILE=$JAR_FILE" \
  -t "$LOCAL_VERSIONED" \
  .

# Also tag as latest
podman tag "$LOCAL_VERSIONED" "$LOCAL_LATEST"

if [[ -n "$DOCKERHUB_USER" ]]; then
  REMOTE_VERSIONED="docker.io/${DOCKERHUB_USER}/${IMAGE_NAME}:${VERSION}"
  REMOTE_LATEST="docker.io/${DOCKERHUB_USER}/${IMAGE_NAME}:latest"

  echo "Tagging for Docker Hub..."
  podman tag "$LOCAL_VERSIONED" "$REMOTE_VERSIONED"
  podman tag "$LOCAL_LATEST" "$REMOTE_LATEST"

  echo "Pushing versioned tag..."
  podman push "$REMOTE_VERSIONED"

  echo "Pushing latest tag..."
  podman push "$REMOTE_LATEST"
fi

echo
echo "Done."
echo "Local tags:"
echo "  $LOCAL_VERSIONED"
echo "  $LOCAL_LATEST"

if [[ -n "$DOCKERHUB_USER" ]]; then
  echo "Remote tags:"
  echo "  docker.io/${DOCKERHUB_USER}/${IMAGE_NAME}:${VERSION}"
  echo "  docker.io/${DOCKERHUB_USER}/${IMAGE_NAME}:latest"
fi