@echo off
echo WarZ Tactical Movement - Development Environment
echo =================================================

echo Checking Java version...
java -version
if %ERRORLEVEL% neq 0 (
    echo ERROR: Java not found! Please install Java 17 or higher.
    pause
    exit /b 1
)

echo.
echo Setting up development environment...
if not exist "gradle\wrapper\gradle-wrapper.jar" (
    echo Downloading Gradle Wrapper...
    powershell -Command "Invoke-WebRequest -Uri 'https://github.com/gradle/gradle/raw/v7.6.0/gradle/wrapper/gradle-wrapper.jar' -OutFile 'gradle\wrapper\gradle-wrapper.jar'"
    if %ERRORLEVEL% neq 0 (
        echo WARNING: Failed to download gradle-wrapper.jar automatically.
        echo Please download it manually from: https://github.com/gradle/gradle/raw/v7.6.0/gradle/wrapper/gradle-wrapper.jar
        echo And place it in: gradle\wrapper\gradle-wrapper.jar
        pause
        exit /b 1
    )
)

echo.
echo Starting Minecraft development client...
echo This will take a few minutes on first run...
echo.
call gradlew.bat runClient

if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to start development client!
    pause
    exit /b 1
)

echo.
echo Development session ended.
pause