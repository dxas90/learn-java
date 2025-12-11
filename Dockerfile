# Build stage
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copy dependency files first for better caching
COPY pom.xml .

# Download dependencies using system Maven (more reliable in containers)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application using system Maven
RUN mvn clean package -DskipTests -B

# Extract layers for better Docker layer caching
RUN java -Djarmode=layertools -jar target/*.jar extract

# Runtime stage - Alpine JRE for minimal size
FROM eclipse-temurin:21.0.9_10-jre-alpine AS production

# Install runtime dependencies
RUN apk add --no-cache \
    curl \
    tzdata \
    ca-certificates

# Create non-root user
RUN addgroup -g 1001 appuser && \
    adduser -D -u 1001 -G appuser appuser

# Set up application directory
WORKDIR /app

# Create logs directory
RUN mkdir -p /app/logs && \
    chown -R appuser:appuser /app

# Copy application layers from builder stage
COPY --from=builder --chown=appuser:appuser /build/dependencies/ ./
COPY --from=builder --chown=appuser:appuser /build/spring-boot-loader/ ./
COPY --from=builder --chown=appuser:appuser /build/snapshot-dependencies/ ./
COPY --from=builder --chown=appuser:appuser /build/application/ ./

# Switch to non-root user
USER appuser

# Health check (give more time for Spring Boot startup)
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Expose port
EXPOSE 8080

# Labels for better container management
LABEL org.opencontainers.image.authors="Daniel Ramirez <dxas90@gmail.com>" \
      org.opencontainers.image.description="Modern Spring Boot application for learning" \
      org.opencontainers.image.licenses="Apache-2.0" \
      org.opencontainers.image.source="https://github.com/dxas90/learn-java" \
      org.opencontainers.image.title="Learn Java Spring Boot" \
      org.opencontainers.image.version="2.0.0"

# JVM optimization for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

# Run application using Spring Boot JarLauncher
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
