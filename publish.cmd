@REM cd ./fabric
@REM powershell -Command "./gradlew githubRelease";
@REM powershell -Command "./gradlew curseforge";
@REM cd ../forge
cd ./forge
powershell -Command "./gradlew githubRelease";
powershell -Command "./gradlew curseforge";