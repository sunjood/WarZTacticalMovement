@echo off
echo WarZ Tactical Movement - Build Script
echo ========================================

echo Checking Java version...
java -version
if %ERRORLEVEL% neq 0 (
    echo ERROR: Java not found! Please install Java 17 or higher.
    pause
    exit /b 1
)

echo.
echo Setting up Gradle Wrapper...
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
echo Building mod...
call gradlew.bat build
if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo Build completed successfully!
echo The mod jar file is located in: build\libs\
echo.
pause