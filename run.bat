@echo off
setlocal
cd /d "%~dp0"

REM Check Java
where java >nul 2>&1
if errorlevel 1 (
    echo Java is not installed or not in PATH.
    pause
    exit /b 1
)

REM Show Java version
java -version

REM Build project if Maven is available
where mvn >nul 2>&1
if not errorlevel 1 (
    echo Building FinalProject...
    call mvn -q -DskipTests package
    if errorlevel 1 (
        echo Build failed!
        pause
        exit /b 1
    )
) else (
    echo Maven not found. Checking existing JAR...
)

REM Run game
if exist "target\pattern-quest-roguelike-2.0.0.jar" (
    java -jar "target\pattern-quest-roguelike-2.0.0.jar" %*
) else if exist "PatternQuest.jar" (
    java -jar "PatternQuest.jar" %*
) else (
    echo No executable JAR found!
    pause
    exit /b 1
)

if errorlevel 1 (
    echo Game exited with an error.
    pause
)
    
