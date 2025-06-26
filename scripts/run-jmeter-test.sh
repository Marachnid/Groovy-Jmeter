#!/bin/bash
JMETER_PATH="/path/to/apache-jmeter/bin/jmeter"
JMX_PATH="../logs/your_test_plan.jmx"

"$JMETER_PATH" -n -t "$JMX_PATH"