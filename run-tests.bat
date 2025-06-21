@echo off
setlocal enabledelayedexpansion

REM Define multiple TestNG files
set TESTNG_FILES="src\test\resources\tests\testng.xml"

REM Clean previous runs
set FAILED_SCENARIOS=failed_scenarios.txt
del "%FAILED_SCENARIOS%" 2>nul

REM Run all TestNG suites sequentially
for %%X in (%TESTNG_FILES%) do (
    echo [RUNNING SUITE] %%X
    call mvn clean test -DsuiteXmlFile="%%X" -Dcucumber.plugin="pretty,html:target/cucumber-reports/report.html,rerun:%FAILED_SCENARIOS%" -Dsurefire.useFile=false
)

REM Rerun failed scenarios (if any)
if exist "%FAILED_SCENARIOS%" (
    echo [RERUNNING FAILED SCENARIOS]
    setlocal disabledelayedexpansion
    for /f "tokens=*" %%F in (%FAILED_SCENARIOS%) do (
        echo Rerunning: %%F
        call mvn test -Dcucumber.features="%%F" -Dcucumber.plugin="pretty,html:target/cucumber-reports/rerun.html" -Dsurefire.useFile=false
    )
    endlocal
    exit /b %ERRORLEVEL%
) else (
    echo [✓] All tests passed!
    exit /b 0
)