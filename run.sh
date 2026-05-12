#!/bin/bash

# 1. Setup
mkdir -p bin
# Ensure the data directory exists (where your source files are)
mkdir -p data

# --- THE FIX: Point to the folder where your JSON/TXT files live ---
SOURCE_DIR="data"

# 2. Check for the Builder's source dependency inside data/
if [ ! -f "$SOURCE_DIR/alt-staff.json" ]; then
    echo "ERROR: 'alt-staff.json' not found in $SOURCE_DIR/"
    echo "Please ensure all JSON and TXT files are inside the 'data' folder."
    exit 1
fi

# 3. Build data if missing
# We check if the OUTPUT files (courses.csv) exist. 
# In your Java code, they are saved in the same place as the source.
if [ ! -f "$SOURCE_DIR/courses.csv" ] || [ ! -f "$SOURCE_DIR/sections.csv" ]; then
    echo "CSV files missing. Running CourseDataBuilder..."
    
    javac -d bin src/CourseDataBuilder.java
    
    if [ $? -eq 0 ]; then
        # Run the builder and pass "data" as the argument
        # This tells the Java code to look in data/ for input and save there for output
        java -cp bin src.CourseDataBuilder "$SOURCE_DIR"
        
        if [ $? -ne 0 ]; then
            echo "Java execution failed."
            exit 1
        fi
    else
        echo "Failed to compile CourseDataBuilder."
        exit 1
    fi
fi

# 4. Compile and Run Main App
echo "Compiling project..."
javac -d bin src/App.java

if [ $? -eq 0 ]; then
    echo "Compilation successful. Starting application..."
    echo "------------------------------------------------"
    
    # Pass the paths relative to the data/ folder
    java -XX:+ShowCodeDetailsInExceptionMessages \
         -cp bin src.App \
         --courses "$SOURCE_DIR/courses.csv" \
         --sections "$SOURCE_DIR/sections.csv"
else
    echo "------------------------------------------------"
    echo "COMPILATION ERROR in App.java."
fi