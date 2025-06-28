
## Overview
Repo is used to test combining JMeter with Groovy scripts. Groovy is built into JMeter and allows easy utilization of Groovy
for creating pre-processing steps in Test Plans, making requests, and more.

The application uses a Java/Micronaut/Gradle framework for creating a simple HTTP endpoint with generic data. Groovy is then used to call that endpoint,
create an output file from the response data, and then be embedded within the JMeter test plan.

The goal of this is to carry out performance testing with real-life (or as realistic as can be) data to better represent endpoint performance.
By using a JSR223 Pre-processor in JMeter, we're able to execute a Groovy script that calls an existing endpoint for testable values (such as IDs),
records those values into a CSV document, and makes that CSV document available for subsequent testing threads/CSV config elements to pull data from.
This keeps testing data 'fresh' while also not requiring any additional configurations or steps to running JMeter.


### Process Flow
For convenience, a pre-configured bat file (scripts/run-jmeter-test.bat) holds the JMeter CLI run command. When this command runs,
it will kick off the following processes:

- run-jmeter-test.bat is executed
- logs/groovyTest.jmx is initiated
- JSR223 Pre-processor is initiated and executes groovyTest.groovy (from within the JMeter test)
- groovyTest.groovy (everything is more groovy with Groovy):
  - calls Java/Micronaut HTTP endpoint
  - Retrieves response data and writes values to logs/report.csv
- JMETER CSV Config elements pull logs/report.csv
- JMETER Threads execute
  - HTTP Samplers using CSV data pull from logs/report.csv


### Quirks
It's tricky trying to get report.csv to appear in the correct directory location. The file is generated from within JMeter's processes,
and it does not seem to want to go anywhere but right next to the jmx file - this is why groovyTest.jmx is located within logs/

For convenience, adding a JMETER_HOME & path variable makes running it a lot easier



## UPDATE ... after testing some of this setup and ideas a more complicated environment
When trying this approach out in a more complicated environment it became apparent that there's a bit more going on between 
JMeter and Groovy that I needed to work out. 

When adding in more complicated JMeter elements I ended up finding out JMeter and embedded Groovy variables sit adjacent 
to each other in scope and may conflict with each other. When Config Elements are added into the base JMeter Test Plan,
they end up injecting new variables into JMeter's runtime that would not be there otherwise. So now you're once-working Groovy script
might stop working as intended outside of testing it in isolation.

For example, I had a JSR223 PreProcessor that was the first item in my Test Plan to run. The PreProcessor contained a Groovy script
that would call an endpoint, retrieve the response data (collection of ID numbers), and write those IDs to a CSV, one per line. When running
on its own with no additional configurations besides a test thread (JMeter won't execute the Test Plan without at least one active Test Thread),
the script and output are as expected.

However, if you were to add a CSV Dataset Config element to your Test Plan, now things change. The Groovy CSV output was going from writing
response data as expected to now writing the same arbitrary number for each item in the CSV output. 

The fix? Changing generic variable names in my Groovy script to something more unique. At a first glance I was iterating through 
'ids' (collection of response data) with 'id' as the iterator which could explain why CSV values themselves were being overwritten. 
On the other hand, I was using the variable 'index' to control some line creation behavior - this could have been overwritten in 
some case and caused the 'id' variable to become 'stuck' at a certain value. Either way, after changing the variable names, the CSV output
returned to writing as expected.

On an interesting note - this could also really come down to how Groovy handles scope for some of its operations. Groovy uses closures which feel
very similar to modern JavaScript syntax for function definitions and anonymous functions, but they handle scope differently. In a closure, it
can access variables outside its immediate scope where in JavaScript the variable would have to be explicitly passed in as a parameter.

##### example
JS
const greeting = "Hello, "
const myFunction = (myName, greeting) => console.log(greeting + myName)

**myFunction("Matt", greeting)**

Groovy
def greeting = "Hello, "
def myFunction = {myName -> println greeting + myName}

**myFunction("Matt")**


Because Groovy closures can access variables outside their immediate local scope, this could be the root cause of the naming conflicts, or one
of them.

#### The sad part
The biggest bummer after figuring out all of this for my own purposes was that it won't fit into the overall workflow of the testing plan.
Why? Because of the CSV Dataset Configs... again.

The purpose of embedding Groovy into JMeter was to create dynamic testing data. We have a collection of endpoints that all do various things, but
they all rely on an ID value to process. Think of an order management system - an order has an ID, associated with that ID could be a buyer,
seller, shipper, status report, etc. Instead of using dummy ID values that could be null and warp response/performance times of endpoints (null requests will be quicker/smaller), we
want to use actual data to test performance.

_*Luckily with JMeter, it does not record request data by default - meaning that the results of an endpoint
(customer names, sensitive data, etc..) can remain anonymous._

With that in mind, the plan was to have a JSR223 PreProcessor be the first item to initiate in the Test Plan. 
The PreProcessor would:
kick off,
call an endpoint to obtain sample IDs, 
write those IDs to CSVs documents

Then:
CSV Dataset Configs would pull those generated documents
Testing threads execute
performance results recorded
output CSV documents are produced

However... The PreProcessor and CSV Dataset Configs execute simultaneously - meaning that the newly generated files 
aren't available to the CSV configs. This results in either the CSV Dataset Configs failing, or they end up using old CSV files
instead of the newly generated ones (if they exist).


Even if you put the PreProcessor in a SetUp thread, it won't separate its run execution from the CSV Dataset Config run execution. 
A workaround to this could be placing the CSV Dataset Configs within the Test Thread itself, but this could get messy if 
you have multiple threads. In a basic scenario where you only have 1-3 testing threads and 1-2 CSV Dataset Config elements, 
it's easy to copy/paste the global CSV Dataset Configs into each thread. But if you have 5+ testing threads with 2 or more Dataset 
Config elements each, you'll have to make changes to 10 or more CSV configs if anything were to change with them or their location.


### The best solution so far
For this particular scenario, there's no real reason that my Groovy script even has to be in JMeter to begin with. 
I wanted to avoid a 2-step process while also keeping thing easy to maintain/use which is why I tried to make the PreProcessor work 
for as long as I did. The solution ended up just being a batch file that runs the Groovy script first and then executes the JMeter CLI 
command to run in non-GUI mode. While the solution was a bit of an eye-roll at the time I could have saved if I tried it earlier, it 
was a good opportunity to learn about some of the inner-workings of JMeter as well as trying out Groovy.