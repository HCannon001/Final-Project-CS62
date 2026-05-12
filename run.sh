#!/bin/bash

# 1. Setup - Create a local bin folder
mkdir -p bin

# 2. Compile - We point to src/App.java. 
# Java's compiler is smart enough to find other files App needs, 
# while ignoring files that are broken/unused (like UserInputFeat.java).
echo "Compiling project..."
javac -d bin src/App.java

# 3. Check if compilation worked
if [ $? -eq 0 ]; then
    echo "Compilation successful. Starting application..."
    echo "------------------------------------------------"
    
    # 4. Run - Using the local bin we just created
    java -XX:+ShowCodeDetailsInExceptionMessages \
         -cp bin src.App \
         --courses data/courses.csv \
         --sections data/sections.csv
else
    echo "------------------------------------------------"
    echo "COMPILATION ERROR: The code has issues."
    echo "Make sure App.java and its dependencies are correct."
fi