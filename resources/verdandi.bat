@echo off

rem set JAVA=%JAVA_HOME%/bin/java
set JAVA=javaw


set CLASSPATH=verdandi.jar
set CLASSPATH=%CLASSPATH%;lib/
set CLASSPATH=%CLASSPATH%;lib/avalon-framework-4.1.3.jar
set CLASSPATH=%CLASSPATH%;lib/commons-logging-1.1.jar
set CLASSPATH=%CLASSPATH%;lib/filters-2.0.235.jar
set CLASSPATH=%CLASSPATH%;lib/idw-gpl.jar
set CLASSPATH=%CLASSPATH%;lib/log4j-1.2.16.jar
set CLASSPATH=%CLASSPATH%;lib/logkit-1.0.1.jar
set CLASSPATH=%CLASSPATH%;lib/looks-2.1.4.jar
set CLASSPATH=%CLASSPATH%;lib/scala-library-2.9.0-1.jar
set CLASSPATH=%CLASSPATH%;lib/scala-swing-2.9.0.jar
set CLASSPATH=%CLASSPATH%;lib/servlet-api-2.3.jar
set CLASSPATH=%CLASSPATH%;lib/slf4j-api-1.6.1.jar
set CLASSPATH=%CLASSPATH%;lib/slf4j-log4j12-1.6.1.jar
set CLASSPATH=%CLASSPATH%;lib/slf4s_2.9.0-1-1.0.6.jar
set CLASSPATH=%CLASSPATH%;lib/swing-worker-1.1.jar
set CLASSPATH=%CLASSPATH%;lib/swingx-1.6.1.jar

start %JAVA% -cp %CLASSPATH% verdandi.ui.Main $@
