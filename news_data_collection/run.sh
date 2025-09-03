#!/bin/bash
#echo "Cleaning and building JARs..."
#./gradlew clean :api:bootJar :view:bootJar :batch:bootJar

echo "Rebuilding Docker containers..."
docker compose down
docker compose up --build -d

echo "All services started successfully."