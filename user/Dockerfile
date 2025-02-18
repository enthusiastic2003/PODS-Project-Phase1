FROM eclipse-temurin:17

# Copy the entire marketplace folder (including the code, pom.xml, etc.) into /marketplace in the container
COPY . /user-service/

# Install Maven
RUN apt-get update && apt-get install -y maven

# Build the project using Maven
RUN cd user-service && mvn package

# Define the entry point for the application
ENTRYPOINT ["java", "-jar", "/user-service/target/user-0.0.1-SNAPSHOT.jar"]
