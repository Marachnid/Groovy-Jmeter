import groovy.json.JsonSlurper
import java.nio.file.Files
import java.nio.file.Paths
import static java.nio.file.StandardOpenOption.*

import org.apache.jmeter.services.FileServer


//extended jmeter cli paths
// jmeter -n -t path/to/your_test.jmx -l path/to/results.jtl -e -o path/to/report
// jmeter -n -t test_plan.jmx -l results.jtl -e -o report


//lean script
// jmeter -n -t path/to/your_test_plan.jmx


//HTTP GET request 
def uri = new URI("http://localhost:8080/ids")
def url = uri.toURL()
def ids = new JsonSlurper().parseText(url.text)        //parse json data


//INTELLIJ IDEA CONFIG - not used in Jmeter setup - comment out if not using within IDEA project structure
// def outputPath = Paths.get("report.csv")
// def writer = Files.newBufferedWriter(outputPath, CREATE, TRUNCATE_EXISTING, WRITE)

// writer.write("ids\n")                           //write a static header
// ids.each { id -> writer.write("${id}\n")}       //loop and write each individual item
// writer.close()



//JMeter output file setup
def baseDir = FileServer.getFileServer().getBaseDir()
def fileName = 'report.csv'
def file = new File(baseDir, fileName)
file.withWriter {writer -> ids.each { id -> writer.write("${id}\n")}}
log.info("Wrote responses to: " + file.getAbsolutePath())

println "Success: $url has been called, report sent to \"logs/report.csv\""