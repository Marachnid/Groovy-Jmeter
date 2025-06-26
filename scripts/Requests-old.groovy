import groovy.json.JsonSlurper
import java.nio.file.Files
import java.nio.file.Paths
import static java.nio.file.StandardOpenOption.*


//call endpoint - set as URI and convert to URL
def uri = new URI("http://localhost:8080/ids")
def url = uri.toURL()
def ids = new JsonSlurper().parseText(url.text)        //parse json data


def outputPath = Paths.get("logs/report.csv")
def writer = Files.newBufferedWriter(outputPath, CREATE, TRUNCATE_EXISTING, WRITE)

//write a static doc header
writer.write("ids\n")

ids.each { id -> writer.write("${id}\n")}

writer.close()

println "Success: $url has been called, report sent to \"logs/report.csv\""