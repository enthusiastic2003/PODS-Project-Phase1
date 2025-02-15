#!/bin/bash

# Kill specific Java processes based on JAR file names

# Define the paths to the JAR files
JAR_PATHS=(
    '/home/sirjanh/PODS-Project-Phase1/wallets/target/wallets-0.0.1-SNAPSHOT.jar'
    '/home/sirjanh/PODS-Project-Phase1/user/target/user-0.0.1-SNAPSHOT.jar'
    '/home/sirjanh/PODS-Project-Phase1/products/target/products-0.0.1-SNAPSHOT.jar'
)

# Flag to track if any task was killed
ANY_KILLED=false

# Loop through the defined JAR paths
for JAR in "${JAR_PATHS[@]}"; do
    # Find the PID of the process running the specific JAR file
    PID=$(ps aux | grep "java -jar $JAR" | grep -v "grep" | awk '{print $2}')
    
    # If the PID is found, kill the process
    if [ -n "$PID" ]; then
        echo "Killing process for JAR: $JAR with PID: $PID"
        kill $PID
        ANY_KILLED=true
    else
        echo "No process found for JAR: $JAR"
    fi
done

# Check if any tasks were killed
if [ "$ANY_KILLED" = true ]; then
    echo "All specified tasks have been killed."
else
    echo "No specified tasks were running."
fi

