#!/bin/bash

# Define multiple TestNG files (space-separated)
TESTNG_FILES=("src/test/resources/tests/testng.xml")

# Clean previous runs
FAILED_SCENARIOS="failed_scenarios.txt"
rm -f "$FAILED_SCENARIOS"

# Run all TestNG suites sequentially
for TESTNG_XML in "${TESTNG_FILES[@]}"; do
    echo "[RUNNING SUITE] $TESTNG_XML"
    mvn clean test -DsuiteXmlFile="$TESTNG_XML" \
        -Dcucumber.plugin="pretty,html:target/cucumber-reports/report.html,rerun:$FAILED_SCENARIOS" \
        -Dsurefire.useFile=false
done

# Rerun failed scenarios (if any)
if [ -f "$FAILED_SCENARIOS" ]; then
    echo "[RERUNNING FAILED SCENARIOS]"
    while IFS= read -r scenario; do
        echo "Rerunning: $scenario"
        mvn test -Dcucumber.features="$scenario" \
            -Dcucumber.plugin="pretty,html:target/cucumber-reports/rerun.html" \
            -Dsurefire.useFile=false
    done < "$FAILED_SCENARIOS"
    exit $?
else
    echo "[✓] All tests passed!"
    exit 0
fi