@echo off
echo Building fat jar...
call mvn clean package

echo Creating custom runtime with JavaFX and all dependencies...
jlink ^
  --module-path "%JAVA_HOME%/jmods;output/openjfx-21.0.7_windows-x64_bin-jmods/javafx-jmods-21.0.7" ^
  --add-modules java.base,java.desktop,java.logging,java.sql,java.xml,java.naming,java.management,java.security.jgss,java.instrument,java.compiler,java.scripting,java.rmi,java.transaction.xa,java.sql.rowset,java.prefs,java.datatransfer,java.xml.crypto,java.security.sasl,javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web ^
  --output output/myruntime

echo Creating Windows exe...
jpackage ^
  --type exe ^
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