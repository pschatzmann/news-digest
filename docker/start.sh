#!/bin/sh
# Start smart java main
java -Xmx$xmx -server -Dlog4j.configuration=file:log4j.properties -cp *.jar ch.pschatzmann.news.Main 
