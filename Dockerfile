# Multi-stage build for smaller image size
# Using Red Hat UBI with Maven for better compatibility with OpenShift
FROM registry.access.redhat.com/ubi8/openjdk-17:1.18 AS builder

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

# Runtime stage - using Red Hat UBI with OpenJDK
FROM registry.access.redhat.com/ubi8/openjdk-17-runtime:1.18

# Add labels for better image management
LABEL maintainer="Suresh Gaikwad <suresh.gaikwad@example.com>"
LABEL description="Simple Bookstore Application using Spring Boot"
LABEL version="1.0.0"

# Install curl for health checks (UBI-specific)
RUN microdnf install curl -y && microdnf clean all

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

# UBI images are already optimized for OpenShift

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
