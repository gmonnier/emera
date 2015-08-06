#!/bin/sh
# Start script for GRNASeq analyser.
clear
export JAVA_BIN_PATH=./jre1.8.0_31/bin/java
echo execute java command line from ${JAVA_BIN_PATH}
${JAVA_BIN_PATH} -jar -Dsun.java2d.gdiblit=false -Xmx1g -Duser.country=EN-Duser.language=en -Duser.variant=Traditional_WIN Application.jar