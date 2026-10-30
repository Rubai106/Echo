#!/usr/bin/env sh
# Project-local Gradle bootstrap. Always use the pinned version so local and CI
# builds do not silently pick up an incompatible system Gradle installation.
set -eu

APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
GRADLE_VERSION="8.9"
GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
GRADLE_DIR="$GRADLE_USER_HOME/cse489-gradle/gradle-$GRADLE_VERSION"

if [ ! -x "$GRADLE_DIR/bin/gradle" ]; then
  mkdir -p "$GRADLE_USER_HOME/cse489-gradle"
  ZIP_PATH="$GRADLE_USER_HOME/cse489-gradle/gradle-$GRADLE_VERSION-bin.zip"
  URL="https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"

  if command -v curl >/dev/null 2>&1; then
    curl -fL --retry 3 "$URL" -o "$ZIP_PATH"
  elif command -v wget >/dev/null 2>&1; then
    wget -O "$ZIP_PATH" "$URL"
  else
    echo "Gradle 8.9 is not cached, and neither curl nor wget is available." >&2
    exit 1
  fi

  if ! command -v unzip >/dev/null 2>&1; then
    echo "Please install unzip and run this script again." >&2
    exit 1
  fi
  unzip -q -o "$ZIP_PATH" -d "$GRADLE_USER_HOME/cse489-gradle"
fi

exec "$GRADLE_DIR/bin/gradle" -p "$APP_HOME" "$@"
