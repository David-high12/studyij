@echo off
setlocal

cd /d "%~dp0"

set MYSQL_PORT=3306
set MYSQL_BASE=%CD%\.tools\mysql-9.7.0-winx64
set MYSQL_DATA=%CD%\.tools\mysql-data
set MYSQLD=%MYSQL_BASE%\bin\mysqld.exe
set MYSQL=%MYSQL_BASE%\bin\mysql.exe
set MAVEN=%CD%\.tools\apache-maven-3.9.9\bin\mvn.cmd
set APP_JAR=%CD%\target\lab-management-0.0.1-SNAPSHOT.jar

echo ==========================================
echo Lab Equipment Management System
echo ==========================================

if not exist "%MYSQLD%" (
    echo MySQL was not found: %MYSQLD%
    pause
    exit /b 1
)

if not exist "%MAVEN%" (
    echo Maven was not found: %MAVEN%
    pause
    exit /b 1
)

where java >nul 2>nul
if errorlevel 1 (
    echo Java was not found. Please install JDK 21 or configure Java in PATH.
    pause
    exit /b 1
)

if not exist "%MYSQL_DATA%\mysql" (
    echo [1/4] Initializing local MySQL data...
    "%MYSQLD%" --no-defaults --basedir="%MYSQL_BASE%" --datadir="%MYSQL_DATA%" --port=%MYSQL_PORT% --bind-address=127.0.0.1 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci --initialize-insecure --console
    if errorlevel 1 (
        echo MySQL initialization failed.
        pause
        exit /b 1
    )
) else (
    echo [1/4] Local MySQL data already exists.
)

netstat -ano | findstr /R /C:":%MYSQL_PORT% .*LISTENING" >nul
if errorlevel 1 (
    echo [2/4] Starting local MySQL...
    start "Lab MySQL" /min "%MYSQLD%" --no-defaults --basedir="%MYSQL_BASE%" --datadir="%MYSQL_DATA%" --port=%MYSQL_PORT% --bind-address=127.0.0.1 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
    timeout /t 8 /nobreak >nul
) else (
    echo [2/4] Local MySQL is already running.
)

echo [3/4] Preparing database and user...
"%MYSQL%" -u root -h 127.0.0.1 -P %MYSQL_PORT% -e "CREATE DATABASE IF NOT EXISTS lab_equipment_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci; CREATE USER IF NOT EXISTS 'lab_user'@'localhost' IDENTIFIED BY 'lab_pass_123'; CREATE USER IF NOT EXISTS 'lab_user'@'127.0.0.1' IDENTIFIED BY 'lab_pass_123'; GRANT ALL PRIVILEGES ON lab_equipment_db.* TO 'lab_user'@'localhost'; GRANT ALL PRIVILEGES ON lab_equipment_db.* TO 'lab_user'@'127.0.0.1'; FLUSH PRIVILEGES;"
if errorlevel 1 (
    echo Database preparation failed. Check whether port %MYSQL_PORT% is used by another MySQL service.
    pause
    exit /b 1
)

netstat -ano | findstr /R /C:":8080 .*LISTENING" >nul
if not errorlevel 1 (
    if not exist "%CD%\target\classes\templates\login.html" (
        echo Runtime resources are missing. Restoring target/classes...
        "%MAVEN%" process-resources
        if errorlevel 1 (
            echo Runtime resource restore failed.
            pause
            exit /b 1
        )
    )
    echo The web system is already running.
    echo Opening http://localhost:8080/login
    if /I not "%NO_OPEN%"=="1" start "" "http://localhost:8080/login"
    if /I "%NO_PAUSE%"=="1" exit /b 0
    pause
    exit /b 0
)

echo [4/4] Starting Spring Boot system...
echo.
echo Login URL: http://localhost:8080/login
echo Admin:   admin520 / admin1314
echo Student: user520 / user1314
echo.
echo Keep this window open while using the system.
echo Press Ctrl+C in this window to stop the web system.
echo.

if exist "%APP_JAR%" (
    java -jar "%APP_JAR%"
) else (
    "%MAVEN%" spring-boot:run
)

if /I "%NO_PAUSE%"=="1" exit /b %ERRORLEVEL%
pause
