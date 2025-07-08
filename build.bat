@echo off
REM Build fat jar
call mvn clean package

REM Remove old custom JavaFX runtime if exists
if exist output\myruntime rmdir /s /q output\myruntime

REM Create custom JavaFX runtime
jlink ^
  --module-path "%JAVA_HOME%/jmods;javafx-jmods-21.0.7" ^
  --add-modules javafx.base,javafx.controls,javafx.fxml,java.sql ^
  --output output/myruntime

REM Package as Windows exe, ensure data directory is included
jpackage ^
  --type msi ^
  --input target ^
  --name Cookbook ^
  --main-jar cookbook-1.0-SNAPSHOT.jar ^
  --main-class g.App ^
  --runtime-image output/myruntime ^
  --icon src/main/resources/g/cookbook.ico ^
  --app-version 1.0 ^
  --vendor "Cookbook Team" ^
  --win-dir-chooser ^
  --win-menu ^
  --win-shortcut

echo Done!
pause