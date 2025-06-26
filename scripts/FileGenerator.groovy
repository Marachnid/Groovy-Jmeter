import groovy.json.JsonSlurper
import org.apache.jmeter.services.FileServer

//FOR USE as part of a JSR223 Pre-Processor INSIDE of JMeter (groovyTest.jmx)

//simple Jmeter CLI command
// jmeter -n -t path/to/your_test_plan.jmx

//NOTE Avoid using generic variable names when embedding into JMeter
//Groovy AND JMeter variables sit adjacent to each other in scope - matching variables will cause conflicts
//example - a groovy script using id instead of testIds in a JSR223 PreProcessor will conflict with CSV Config elements
// - and interfere with any groovy variable using the same name (id)


//HTTP GET request
def uri = new URI("http://localhost:8080/ids")
def url = uri.toURL()
def testValues = new JsonSlurper().parseText(url.text)        //parse json data


def baseDir = FileServer.getFileServer().getBaseDir()

def singleFileName = 'singleDoc.csv'
def singleFile = new File(baseDir, singleFileName)
def multiFileName = 'multiDoc.csv'
def multiFile = new File(baseDir, multiFileName)



singleFile.withWriter {
    writer -> testValues.withIndex().each {
        testValue, testIndex ->
            if (testIndex >= 100) return
            writer.write("${testValue}\n")
    }
}

multiFile.withWriter {
    writer -> testValues.withIndex().each {
        testValue, testIndex ->
            if (testIndex >= 200) return
            writer.write("${testValue}\n")
            if ((testIndex + 1) % 2 == 0) {writer.write("\n")}
            else {writer.write(",")}
    }
}