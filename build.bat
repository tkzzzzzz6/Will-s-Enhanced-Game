@echo off
echo ========================================
echo    Will's Enhanced Game - Build Script
echo ========================================
echo.

echo Creating bin directory...
if not exist "bin" mkdir bin

echo Compiling Java files...
javac -d bin -cp "src" src/com/willcodes/main/*.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Compilation successful!
    echo.
    echo Starting the game...
    echo.
    java -cp bin com.willcodes.main.Game
) else (
    echo.
    echo Compilation failed! Please check your Java installation.
    echo Make sure you have Java 21 or higher installed.
    pause
) 