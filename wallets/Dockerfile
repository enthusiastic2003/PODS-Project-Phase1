FROM eclipse-temurin:17

# Copy the entire marketplace folder (including the code, pom.xml, etc.) into /marketplace in the container
COPY . /wallet-service/

# Install Maven
RUN apt-get update && apt-get install -y maven

# Build the project using Maven
RUN cd /wallet-service/ && mvn package

# Define the entry point for the application
ENTRYPOINT ["java", "-jar", "/wallet-service/target/wallets-0.0.1-SNAPSHOT.jar"]
