#!/usr/bin/env sh
# Lightweight wrapper bootstrap. It downloads the pinned Gradle distribution when needed.
set -eu
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
GRADLE_VERSION="8.9"
GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
GRADLE_DIR="$GRADLE_USER_HOME/cse489-gradle/gradle-$GRADLE_VERSION"

if [ -n "${GRADLE_HOME:-}" ] && [ -x "$GRADLE_HOME/bin/gradle" ]; then
  exec "$GRADLE_HOME/bin/gradle" -p "$APP_HOME" "$@"
fi
if command -v gradle >/dev/null 2>&1; then
  exec gradle -p "$APP_HOME" "$@"
fi
if [ ! -x "$GRADLE_DIR/bin/gradle" ]; then
  mkdir -p "$GRADLE_USER_HOME/cse489-gradle"
  ZIP_PATH="$GRADLE_USER_HOME/cse489-gradle/gradle-$GRADLE_VERSION-bin.zip"
  URL="https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
  if command -v curl >/dev/null 2>&1; then
    curl -fL --retry 2 "$URL" -o "$ZIP_PATH"
  elif command -v wget >/dev/null 2>&1; then
    wget -O "$ZIP_PATH" "$URL"
  else
    echo "Gradle is not installed and neither curl nor wget is available." >&2
    exit 1
  fi
  if command -v unzip >/dev/null 2>&1; then
    unzip -q -o "$ZIP_PATH" -d "$GRADLE_USER_HOME/cse489-gradle"
  else
    echo "Please install unzip and run this script again." >&2
    exit 1
  fi
fi
exec "$GRADLE_DIR/bin/gradle" -p "$APP_HOME" "$@"
