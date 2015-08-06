@echo off
rem --- Dependencies for java VTK Dlls ---
set JAVA_AWT_BIN_PATH=.\jre7\bin

echo execute java command %JAVA_AWT_BIN_PATH%\java.exe
%JAVA_AWT_BIN_PATH%\java.exe -jar Application.jar