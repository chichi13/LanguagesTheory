REM compile a small file

REM compile .java files
java -jar w\small.jar %1
IF ERRORLEVEL 1 GOTO FAIL
REM run a small program

java -cp .;sal %~n1
GOTO EXIT
:FAIL
ECHO  Errors compiling %1
:EXIT

