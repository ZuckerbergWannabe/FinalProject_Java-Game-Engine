#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
if [[ -f PatternQuest.jar ]]; then
    exec java -jar PatternQuest.jar "$@"
fi
if command -v mvn >/dev/null 2>&1; then
    mvn -q -DskipTests package
    exec java -jar target/pattern-quest-roguelike-2.0.0.jar "$@"
fi
if ! command -v javac >/dev/null 2>&1; then
    echo "Java 17+ (JDK) is required. Install Java or open the included PatternQuest.jar." >&2
    exit 1
fi
mkdir -p target/classes
find src/main/java -name '*.java' > target/sources.txt
javac --release 17 -encoding UTF-8 -d target/classes @target/sources.txt
exec java -cp target/classes edu.game.Main "$@"
