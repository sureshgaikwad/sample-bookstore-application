# Multi-stage build for smaller image size
FROM maven:3.9-openjdk-17-slim AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml first to leverage Docker cache
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jre-slim

# Add labels for better image management
LABEL maintainer="Suresh Gaikwad <suresh.gaikwad@example.com>"
LABEL description="Simple Bookstore Application using Spring Boot"
LABEL version="1.0.0"

# Create non-root user for security
RUN groupadd -r bookstore && useradd -r -g bookstore bookstore

# Set working directory
WORKDIR /app

# Copy the JAR file from builder stage
COPY --from=builder /app/target/bookstore-app-1.0.0.jar app.jar

# Change ownership to non-root user
RUN chown -R bookstore:bookstore /app

# Switch to non-root user
USER bookstore

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
