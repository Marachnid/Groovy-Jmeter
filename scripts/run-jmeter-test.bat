@echo off
set JMX_PATH=C:\Users\Matth\dev\idea\IdeaProjects\performance-endpoints\logs\groovyTest.jmx
jmeter -n -t "%JMX_PATH%" -j ../logs/jmeter.log