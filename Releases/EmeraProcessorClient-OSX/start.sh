#!/bin/sh
# Dependencies for java VTK Dlls
export JAVA_AWT_BIN_PATH=./jre7/bin

echo "execute java command ${JAVA_AWT_BIN_PATH}/java"
${JAVA_AWT_BIN_PATH}/java -jar Application.jar