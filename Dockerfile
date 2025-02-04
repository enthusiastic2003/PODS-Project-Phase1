FROM openjdk:17
COPY target/pods-0.0.1-SNAPSHOT.jar /
ENTRYPOINT ["java", "-jar", "/pods-0.0.1-SNAPSHOT.jar"]