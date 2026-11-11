#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

JAVA="/usr/lib/jvm/java-21-openjdk-amd64/bin/java"

if [[ ! -x "$JAVA" ]]; then
    echo "Java 21 not found!"
    exit 1
fi

if [[ -f PatternQuest.jar ]]; then
    exec "$JAVA" -jar PatternQuest.jar "$@"
fi

mvn -q -DskipTests package

exec "$JAVA" -jar target/pattern-quest-roguelike-2.0.0.jar "$@"
