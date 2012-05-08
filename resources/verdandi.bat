@echo off

rem set JAVA=%JAVA_HOME%/bin/java
set JAVA=java
setLocal EnableDelayedExpansion

set CLASSPATH="verdandi.jar;

for /R ./lib %%a in (*.jar) do (
set CLASSPATH=!CLASSPATH!;%%a
)
set CLASSPATH=!CLASSPATH!"

start %JAVA% -cp %CLASSPATH% verdandi.Main $@
