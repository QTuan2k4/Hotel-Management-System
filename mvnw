#!/bin/sh
# ----------------------------------------------------------------------------
# Maven Wrapper startup script, version 3.2.0
# ----------------------------------------------------------------------------
set -e
BASE_DIR="$(cd "$(dirname "$0")"; pwd -P)"
MAVEN_PROJECTBASEDIR="${MAVEN_PROJECTBASEDIR:-$BASE_DIR}"
WRAPPER_DIR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper"
WRAPPER_JAR="$WRAPPER_DIR/maven-wrapper.jar"
PROPERTIES_FILE="$WRAPPER_DIR/maven-wrapper.properties"

if [ ! -f "$PROPERTIES_FILE" ]; then
  echo "Missing $PROPERTIES_FILE"
  exit 1
fi

# Download wrapper jar if missing
if [ ! -f "$WRAPPER_JAR" ]; then
  echo "Downloading Maven Wrapper jar..."
  URL="$(grep -E '^wrapperUrl=' "$PROPERTIES_FILE" | cut -d= -f2-)"
  mkdir -p "$WRAPPER_DIR"
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$URL" -o "$WRAPPER_JAR"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$URL" -O "$WRAPPER_JAR"
  else
    echo "Neither curl nor wget is available to download Maven Wrapper jar."
    exit 1
  fi
fi

JAVA_CMD="${JAVA_HOME:+$JAVA_HOME/bin/}java"
exec "$JAVA_CMD" -jar "$WRAPPER_JAR" "$@"
