# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the Gradle wrapper files
COPY gradlew .
COPY gradle gradle

# Copy the build.gradle file
COPY build.gradle .
COPY settings.gradle .

# Grant execution rights to the Gradle wrapper
RUN chmod +x ./gradlew

# Download dependencies. This will be cached if the build.gradle file hasn't changed.
RUN ./gradlew dependencies

# Copy the rest of the application's source code
COPY src src

# Expose the port the app runs on
EXPOSE 15000

# Command to run the application
CMD ["./gradlew", "bootRun"]
