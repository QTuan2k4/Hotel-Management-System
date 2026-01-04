\
    @ECHO OFF
    SETLOCAL
    set BASE_DIR=%~dp0
    set WRAPPER_DIR=%BASE_DIR%\.mvn\wrapper
    set WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar
    set PROPERTIES_FILE=%WRAPPER_DIR%\maven-wrapper.properties

    IF NOT EXIST "%PROPERTIES_FILE%" (
      ECHO Missing %PROPERTIES_FILE%
      EXIT /B 1
    )

    REM Download wrapper jar if missing
    IF NOT EXIST "%WRAPPER_JAR%" (
      FOR /F "usebackq tokens=1,* delims==" %%A IN ("%PROPERTIES_FILE%") DO (
        IF "%%A"=="wrapperUrl" SET WRAPPER_URL=%%B
      )
      ECHO Downloading Maven Wrapper jar...
      powershell -NoProfile -ExecutionPolicy Bypass -Command ^
        "$ProgressPreference='SilentlyContinue';" ^
        "New-Item -ItemType Directory -Force -Path '%WRAPPER_DIR%' | Out-Null;" ^
        "Invoke-WebRequest -UseBasicParsing -Uri '%WRAPPER_URL%' -OutFile '%WRAPPER_JAR%';"
      IF ERRORLEVEL 1 (
        ECHO Failed to download Maven Wrapper jar.
        EXIT /B 1
      )
    )

    "%JAVA_HOME%\bin\java.exe" -jar "%WRAPPER_JAR%" %*
    ENDLOCAL
