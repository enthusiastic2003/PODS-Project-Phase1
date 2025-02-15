FROM eclipse-temurin:17
# Set the working directory to /marketplace inside the container
WORKDIR /marketplace

# Copy the entire marketplace folder (including the code, pom.xml, etc.) into /marketplace in the container
COPY marketplace/ /marketplace/

# Copy the products.csv file into the container (if needed)
COPY products.csv /

# Install Maven
RUN apt-get update && apt-get install -y maven

# Build the project using Maven
RUN mvn package

# Define the entry point for the application
ENTRYPOINT ["java", "-jar", "/marketplace/target/products-0.0.1-SNAPSHOT.jar"]