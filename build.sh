#!/bin/bash

echo "========================================"
echo "   Will's Enhanced Game - Build Script"
echo "========================================"
echo

echo "Creating bin directory..."
mkdir -p bin

echo "Compiling Java files..."
javac -d bin -cp "src" src/com/willcodes/main/*.java

if [ $? -eq 0 ]; then
    echo
    echo "Compilation successful!"
    echo
    echo "Starting the game..."
    echo
    java -cp bin com.willcodes.main.Game
else
    echo
    echo "Compilation failed! Please check your Java installation."
    echo "Make sure you have Java 11 or higher installed."
    read -p "Press Enter to continue..."
fi 