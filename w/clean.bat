REM clean source

REM delete class files left at toplevel
del *.class
REM and intermediate (.j) files
del *.j


REM delete compiler class files

del sal/util/*.class
del sal/small/*.class
del sal/*.class

REM delete jar file
del w\small.jar

REM remove temp files left behind by jar
del w\*.tmp
