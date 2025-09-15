# Multi-stage build for smaller image size
# Using upstream Maven image for better compatibility
FROM docker.io/maven:3.9.4-eclipse-temurin-17-alpine AS builder

# Set working directory
WORKDIR /app

# Ensure proper permissions for the /app directory
RUN chmod -R 777 /app

# Copy pom.xml first to leverage Docker cache
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage - using upstream OpenJDK image
FROM docker.io/eclipse-temurin:17-jre-alpine

# Add labels for better image management
LABEL maintainer="Suresh Gaikwad <suresh.gaikwad@example.com>"
LABEL description="Simple Bookstore Application using Spring Boot"
LABEL version="1.0.0"

# Install curl for health checks (Alpine-specific)
RUN apk add --no-cache curl

# Create application directory
RUN mkdir -p /app && \
    chmod -R g+rwX /app && \
    chmod -R g+rwX /tmp

# Set working directory
WORKDIR /app

# Copy the JAR file from builder stage
COPY --from=builder /app/target/bookstore-app-1.0.0.jar app.jar

# OpenShift runs containers with random UIDs, so we need to ensure proper permissions
RUN chmod -R g+rwX /app

# Create a non-root user (though OpenShift will override the UID)
RUN addgroup -g 1001 appgroup && \
    adduser -D -u 1001 -G appgroup appuser

# Change ownership to the application user
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER 1001

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application with optimized JVM settings for containers
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+UseG1GC", \
    "-XX:+UseStringDeduplication", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
