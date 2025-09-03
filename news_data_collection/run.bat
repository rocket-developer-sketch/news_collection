@echo off
@REM echo Cleaning and building JARs...
@REM call gradlew clean :api:bootJar :view:bootJar :batch:bootJar

echo Rebuilding Docker containers...
docker compose down
docker compose up --build -d

echo All services are up.
pause