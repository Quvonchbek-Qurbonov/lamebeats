# Use JDK 21 as the base image
FROM eclipse-temurin:21-jdk as builder

# Set the working directory
WORKDIR /app

# Copy gradle files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Make gradlew executable
RUN chmod +x ./gradlew

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy the source code
COPY src src

# Build the application
RUN ./gradlew build -x test --no-daemon

# Use JRE for the runtime image
FROM eclipse-temurin:21-jre

# Set working directory
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Set entry point
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Expose the port the app runs on
EXPOSE 8080