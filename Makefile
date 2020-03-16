build_fat_jar:
	chmod +x ./gradlew && ./gradlew jar && cp build/libs/cs4240-1.0-SNAPSHOT.jar .
submit:
	tar czf submit.tar.gz build/generated-src gradle src build.gradle gradlew gradlew.bat Makefile README.md settings.gradle --transform s/build\\///
