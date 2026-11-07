@echo off
cd /d "%~dp0"
if exist PatternQuest.jar (
    java -jar PatternQuest.jar %*
    goto :eof
)
where mvn >nul 2>nul
if %errorlevel%==0 (
    call mvn -q -DskipTests package
    if errorlevel 1 exit /b 1
    java -jar target\pattern-quest-roguelike-2.0.0.jar %*
    goto :eof
)
if not exist target\classes mkdir target\classes
(for /r src\main\java %%f in (*.java) do @echo "%%f") > target\sources.txt
javac --release 17 -encoding UTF-8 -d target\classes @target\sources.txt
if errorlevel 1 exit /b 1
java -cp target\classes edu.game.Main %*
