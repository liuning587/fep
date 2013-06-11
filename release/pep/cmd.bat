title protocolParser
@echo off
if "%OS%" == "Windows_NT" setlocal
rem ---------------------------------------------------------------------------
rem Start script for the Mas Client
rem ---------------------------------------------------------------------------

set CURRENT_DIR=%cd%
set APP_HOME=%CURRENT_DIR%

set JAVA_HOME=%JAVA_HOME%
:gotJavaHome

set CLASSPATH=.;%JAVA_HOME%\lib\tools.jar
for %%i in ("%APP_HOME%\lib\*.jar") do call "%APP_HOME%\cpappend.bat" %%i 
set CLASSPATH=%CLASSPATH%;%APP_HOME%\fep.jar

set _RUNJAVA="%JAVA_HOME%\bin\java"

echo Using APP_HOME=%CURRENT_DIR%
echo Using JAVA_HOME:%JAVA_HOME%
echo CLASSPATH:   %CLASSPATH%

set MAINCLASS=fep.main.Main
set JAVA_OPTS=-Xms128m -Xmx256m

%_RUNJAVA% -version
%_RUNJAVA% %JAVA_OPTS% -classpath %CLASSPATH% %MAINCLASS% 

:end
