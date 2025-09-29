# --- Stage 1: Build the application using Gradle ---
# Use the official Gradle image with the required JDK version
FROM gradle:8.5-jdk17-jammy AS build

# Set the working directory inside the image
WORKDIR /app

# Copy build files to cache dependencies
COPY build.gradle settings.gradle /app/
COPY gradle /app/gradle

# Download dependencies. This layer will be cached.
RUN gradle dependencies --no-daemon

# Copy the application source code
COPY src /app/src

# Build the application into an executable JAR file
RUN gradle bootJar --no-daemon


# --- Stage 2: Create a lightweight final image ---
# Use a lightweight image with Java 17
FROM eclipse-temurin:17-jre-jammy

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Specify the port that our application will listen on
EXPOSE 8080

# Command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]