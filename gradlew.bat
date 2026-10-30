@echo off
setlocal EnableExtensions
set "GRADLE_VERSION=8.9"
if not defined GRADLE_USER_HOME set "GRADLE_USER_HOME=%USERPROFILE%\.gradle"
set "GRADLE_DIR=%GRADLE_USER_HOME%\cse489-gradle\gradle-%GRADLE_VERSION%"

if not exist "%GRADLE_DIR%\bin\gradle.bat" (
  if not exist "%GRADLE_USER_HOME%\cse489-gradle" mkdir "%GRADLE_USER_HOME%\cse489-gradle"
  powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; $u='https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip'; $z='%GRADLE_USER_HOME%\cse489-gradle\gradle-%GRADLE_VERSION%-bin.zip'; Invoke-WebRequest -Uri $u -OutFile $z; Expand-Archive -Path $z -DestinationPath '%GRADLE_USER_HOME%\cse489-gradle' -Force"
  if errorlevel 1 exit /b 1
)

call "%GRADLE_DIR%\bin\gradle.bat" -p "%~dp0." %*
exit /b %ERRORLEVEL%
