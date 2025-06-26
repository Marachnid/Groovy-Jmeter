
### Overview

Repo is used to test combining JMeter with Groovy scripts. Groovy is built into JMeter and allows easy utilization of Groovy
for creating pre-processing steps in Test Plans, making requests, and more.

The application uses a Java/Micronaut/Gradle framework for creating a simple HTTP endpoint with generic data. Groovy is then used to call that endpoint,
create an output file from the response data, and then be embedded within the JMeter test plan.

The goal of this is to carry out performance testing with real-life (or as realistic as can be) data to better represent endpoint performance.
By using a JSR223 Pre-processor in JMeter, we're able to execute a Groovy script that calls an existing endpoint for testable values (such as IDs),
records those values into a CSV document, and makes that CSV document available for subsequent testing threads/CSV config elements to pull data from.
This keeps testing data 'fresh' while also not requiring any additional configurations or steps to running JMeter.

For convenience, a pre-configured bat file (scripts/run-jmeter-test.bat) holds the JMeter CLI run command.

run-jmeter-test.bat is executed
logs/groovyTest.jmx is initiated
JSR223 Pre-processor is initiated and executes groovyTest.groovy
groovyTest.groovy:
    calls Java/Micronaut endpoint
    Retrieves response data and writes values to logs/report.csv
JMETER CSV Config elements pull logs/report.csv 
JMETER Threads execute
    HTTP Samplers using CSV data pull from logs/report.csv


### Quirks
It's tricky trying to get report.csv to appear in the correct directory location. The file is generated from within JMeter's processes,
and it does not seem to want to go anywhere but right next to the jmx file - this is why groovyTest.jmx is located within logs/

For convenience, adding a JMETER_HOME & path variable makes running it a lot easier
