# Multi-stage build
# Stage 1: Build the application
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Copy Gradle files
COPY gradle/ gradle/
COPY gradlew .
COPY gradlew.bat .
COPY build.gradle .
COPY settings.gradle .

# Copy source code
COPY src/ src/

# Build the application
RUN chmod +x gradlew
RUN ./gradlew build -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
