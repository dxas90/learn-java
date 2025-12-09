# Learn Java Spring Boot 🚀

[![CI/CD Pipeline](https://github.com/dxas90/learn-java/actions/workflows/dockerimage.yml/badge.svg)](https://github.com/dxas90/learn-java/actions/workflows/dockerimage.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green.svg)](https://spring.io/projects/spring-boot)

A modern Spring Boot application demonstrating best practices for enterprise Java development, containerization, and cloud-native deployments.

## 🌟 Features

- **Modern Java 21** with Spring Boot 3.2.0
- **RESTful API** with OpenAPI/Swagger documentation
- **Comprehensive Testing** - Unit, integration, and contract tests
- **Monitoring & Observability** - Health checks, metrics, and Actuator endpoints
- **Container Ready** - Optimized Docker images with security best practices
- **CI/CD Ready** - GitHub Actions and GitLab CI configurations
- **Kubernetes Deployment** - Helm charts and manifest files
- **Code Quality** - SonarQube integration and test coverage

## 🏗️ Architecture

```text
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   REST API      │    │   Business      │    │   Monitoring    │
│                 │    │   Logic         │    │                 │
│ • Controllers   │────│ • Services      │────│ • Actuator      │
│ • Validation    │    │ • DTOs          │    │ • Metrics       │
│ • Exception     │    │ • Configuration │    │ • Health Checks │
│   Handling      │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🚀 Quick Start

### Prerequisites

- Java 21 or higher
- Maven 3.9+ or Gradle 8+
- Docker (optional)

### Running Locally

```bash
# Clone the repository
git clone https://github.com/dxas90/learn-java.git
cd learn-java

# Run with Maven
./mvnw spring-boot:run

# Or run with Gradle
./gradlew bootRun

# The application will start on http://localhost:8080
```

### Running with Docker

```bash
# Build the Docker image
docker build -t learn-java .

# Run the container
docker run -p 8080:8080 learn-java

# Or use Docker Compose
docker-compose up
```

## 📡 API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/` | GET | Welcome message with app info |
| `/api/v1/ping` | GET | Simple connectivity test |
| `/api/v1/health` | GET | Application health status |
| `/api/v1/greet` | POST | Personalized greeting |
| `/actuator/health` | GET | Detailed health information |
| `/actuator/metrics` | GET | Application metrics |
| `/swagger-ui.html` | GET | Interactive API documentation |

### Example Requests

```bash
# Get welcome message
curl http://localhost:8080/api/v1/

# Health check
curl http://localhost:8080/actuator/health

# Create personalized greeting
curl -X POST http://localhost:8080/api/v1/greet \
  -H "Content-Type: application/json" \
  -d '{"name": "World"}'
```

## 🧪 Testing

```bash
# Run all tests
./mvnw test

# Run tests with coverage
./mvnw clean test jacoco:report

# Run integration tests only
./mvnw test -Dtest="*IntegrationTest"
```

## 📦 Building

### Maven

```bash
# Build JAR
./mvnw clean package

# Build Docker image
./mvnw clean package
docker build -t learn-java .
```

### Gradle

```bash
# Build JAR
./gradlew bootJar

# Build Docker image
./gradlew bootJar
docker build -f Dockerfile.gradle -t learn-java .
```

## 🐳 Docker

The application includes optimized Dockerfiles with:

- **Multi-stage builds** for smaller images
- **Layered JARs** for better caching
- **Non-root user** for security
- **Health checks** for container orchestration
- **JVM optimization** for containerized environments

```bash
# Maven-based build
docker build -t learn-java .

# Gradle-based build
docker build -f Dockerfile.gradle -t learn-java .
```

## 📊 Monitoring

The application exposes several monitoring endpoints:

- **Health**: `/actuator/health` - Application and dependency health
- **Metrics**: `/actuator/metrics` - JVM and application metrics
- **Info**: `/actuator/info` - Application build and runtime information
- **Prometheus**: `/actuator/prometheus` - Metrics in Prometheus format

## 🔧 Configuration

### Application Profiles

- **dev** - Development with H2 database and debug logging
- **test** - Testing with in-memory database
- **prod** - Production with PostgreSQL and optimized logging

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `dev` |
| `DATABASE_URL` | Database connection URL | `jdbc:h2:mem:devdb` |
| `DATABASE_USERNAME` | Database username | `sa` |
| `DATABASE_PASSWORD` | Database password | `password` |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | OpenTelemetry collector endpoint | `http://tempo.observability.svc.cluster.local:4318` |
| `OTEL_SAMPLING_PROBABILITY` | Tracing sampling rate (0.0-1.0, 0=disabled) | `0.0` |
| `OTEL_EXPORTER_ENABLED` | Enable OTLP exporter | `false` |
| `OTEL_LOGS_EXPORTER` | Logs exporter type (none/otlp) | `none` |
| `OTEL_METRICS_EXPORTER` | Metrics exporter type (none/otlp) | `none` |
| `OTEL_TRACES_EXPORTER` | Traces exporter type (none/otlp) | `none` |

## 🚀 CI/CD Pipeline

### GitHub Actions

The project includes comprehensive CI/CD pipelines:

1. **Test Stage** - Unit tests, integration tests, coverage
2. **Security Stage** - Vulnerability scanning with Trivy
3. **Build Stage** - Docker image build and push
4. **Deploy Stage** - Kubernetes deployment to staging

### GitLab CI

Alternative CI/CD pipeline with:

- Maven/Gradle build caching
- Parallel test execution
- Security scanning
- Container registry integration

## 📈 Code Quality

- **Test Coverage**: JaCoCo integration with coverage reports
- **Static Analysis**: SonarQube quality gates
- **Security Scanning**: Trivy vulnerability detection
- **Code Formatting**: Consistent code style enforcement

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

- 📖 [Documentation](https://github.com/dxas90/learn-java/wiki)
- 🐛 [Issue Tracker](https://github.com/dxas90/learn-java/issues)
- 💬 [Discussions](https://github.com/dxas90/learn-java/discussions)

---

**Built with ❤️ using Spring Boot, Java 21, and modern DevOps practices**

```text
          Git Actions:                CI System Actions:

   +-------------------------+       +-----------------+
+-►| Create a Feature Branch |   +--►| Build Container |
|  +------------+------------+   |   +--------+--------+
|               |                |            |
|               |                |            |
|      +--------▼--------+       |    +-------▼--------+
|  +--►+ Push the Branch +-------+    | Push Container |
|  |   +--------+--------+            +-------+--------+
|  |            |                             |
|  |            |                             |
|  |     +------▼------+            +---------▼-----------+
|  +-----+ Test/Verify +◄-------+   | Deploy Container to |
|        +------+------+        |   | Ephemeral Namespace |
|               |               |   +---------+-----------+
|               |               |             |
|               |               +-------------+
|               |
|               |                    +-----------------+
|               |             +-----►| Build Container |
|      +--------▼--------+    |      +--------+--------+
|  +--►+ Merge to Master +----+               |
|  |   +--------+--------+                    |
|  |            |                     +-------▼--------+
|  |            |                     | Push Container |
|  |     +------▼------+              +-------+--------+
|  +-----+ Test/Verify +◄------+              |
|        +------+------+       |              |
|               |              |    +---------▼-----------+
|               |              |    | Deploy Container to |
|               |              |    | Staging   Namespace |
|               |              |    +---------+-----------+
|               |              |              |
|               |              +--------------+
|               |
|        +------▼-----+             +---------------------+
+--------+ Tag Master +------------►| Deploy Container to |
         +------------+             |     Production      |
                                    +---------------------+
```
