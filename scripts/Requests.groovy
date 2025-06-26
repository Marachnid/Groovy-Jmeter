import groovy.json.JsonSlurper
import java.nio.file.Files
import java.nio.file.Paths
import static java.nio.file.StandardOpenOption.*

import org.apache.jmeter.services.FileServer

//FOR USE as part of a JSR223 Pre-Processor INSIDE of JMeter (groovyTest.jmx)

//Jmeter CLI command
// jmeter -n -t path/to/your_test_plan.jmx


//HTTP GET request
def uri = new URI("http://localhost:8080/ids")
def url = uri.toURL()
def ids = new JsonSlurper().parseText(url.text)        //parse json data


//JMeter output file
def baseDir = FileServer.getFileServer().getBaseDir()
def fileName = 'report.csv'
def file = new File(baseDir, fileName)
file.withWriter {writer -> ids.each { id -> writer.write("${id}\n")}}
log.info("Wrote responses to: " + file.getAbsolutePath())

println "Success: $url has been called, report sent to \"logs/report.csv\""