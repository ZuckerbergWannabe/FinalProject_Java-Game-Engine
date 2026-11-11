#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
mkdir -p target/classes
find src/main/java -name '*.java' | sort > target/sources.txt
javac --release 17 -encoding UTF-8 -d target/classes @target/sources.txt
java -Djava.awt.headless=true -cp target/classes edu.game.Main --smoke-test
java -Djava.awt.headless=true -cp target/classes edu.game.Main --preview target/game-preview.png
if command -v mvn >/dev/null 2>&1; then
    mvn test
else
    echo 'Maven not found: JUnit tests are included but not executed here.'
fi
