REM build compiler

REM compile .java files
javac -cp w\jasmin.jar sal\util\*.java sal\small\*.java
javac sal\Library.java


REM create a new jar file
xcopy w\jasmin.jar w\small.jar
jar ufe w\small.jar sal.small.Main sal\util\*.class sal\small\*.class

