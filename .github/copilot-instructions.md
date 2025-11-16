# AI Coding Assistant — learn-java (Spring Boot microservice)

This file provides focused guidance for AI coding agents working on the learn-java Spring Boot microservice project.

## Project Overview
- **Spring Boot 3.5.7** REST API microservice with Java 21 (matching Node.js/Python/Ruby/Go sibling projects)
- **Single JAR deployment** with embedded Tomcat, JPA/H2 for persistence, Actuator for observability
- **Helm chart** `k8s/learn-java/` for Kubernetes deployment with optional HTTPRoute, HPA, PVC, NetworkPolicy
- **CI/CD**: GitHub Actions workflow tests Java 21, runs checkstyle/spotbugs/jacoco, builds Docker, deploys to Kind for e2e validation
- **Endpoints**: `/` (welcome), `/ping`, `/healthz`, `/info` — deliberately minimal business logic for learning

## Architecture & Code Organization

### Core Structure (Standard Spring Boot + Learning Patterns)
- **`src/main/java/com/learn/springboot/`**:
  - `Application.java` — Entry point with `@SpringBootApplication`, `@EventListener` for startup logging, optional `@Profile("debug")` bean inspector
  - `HelloController.java` — Main REST controller with all 4 endpoints, uses `@Value` for config injection, `@Autowired SystemInfoService`
  - `CorsConfig.java` — `@Configuration` with `CorsConfigurationSource` bean (allows all origins/methods/headers)
  - `dto/` — Java records for API responses: `ApiResponse<T>` (standardized success/error), `WelcomeData`, `HealthData`, `SystemInfo.*` records
  - `service/SystemInfoService.java` — Provides JVM/OS metrics using `ManagementFactory` (memory, CPU, threads, uptime)
  - `exception/GlobalExceptionHandler.java` — `@RestControllerAdvice` for unified error handling (404, validation, runtime exceptions)
  - `config/` — OpenApiConfig (Swagger/OpenAPI setup), WebConfig (security headers middleware)

### Key Java/Spring Patterns Used
1. **Records for DTOs**: All data transfer objects use Java 21 records (immutable, concise). Example: `ApiResponse<T>` has static factory methods `success()` and `error()`.
2. **Defensive Copying in Records**: `WelcomeData` uses compact constructor with `List.copyOf(endpoints)` and overrides `endpoints()` to return `Collections.unmodifiableList()` (fixes SpotBugs EI_EXPOSE_REP).
3. **Locale-Aware Operations**: `SystemInfoService` uses `Locale.ROOT` for `.toLowerCase()` calls to avoid internationalization issues (fixes SpotBugs DM_CONVERT_CASE).
4. **Layered JAR Builds**: `pom.xml` Spring Boot plugin config has `<layers><enabled>true</enabled></layers>` for Docker optimization.
5. **4-Space Indentation**: Code uses spaces (not tabs) — checkstyle enforces Google Java Style with 2-space config expectation but project uses 4 spaces.

## Critical Developer Workflows

### Maven Build & Test
```bash
# Primary commands (./mvnw wrapper handles Maven)
./mvnw clean compile                     # Build only
./mvnw clean test                        # Run tests (includes checkstyle validation)
./mvnw clean test jacoco:report          # Tests + coverage report (target/site/jacoco/)
./mvnw checkstyle:check                  # Style check (Google checks, 100+ warnings expected for indentation/javadoc)
./mvnw spotbugs:check                    # Static analysis (currently 0 bugs after recent fixes)
./mvnw spring-boot:run                   # Run locally on port 8080 (dev profile default)
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod  # Production profile

# Makefile shortcuts (see Makefile for all targets)
make build        # ./mvnw clean compile
make test         # ./mvnw clean test jacoco:report
make run          # ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
make package      # ./mvnw clean package -DskipTests (produces JAR in target/)
```

### Testing Strategy
- **Unit tests**: `src/test/java/com/learn/springboot/HelloControllerTest.java` — Mocked `SystemInfoService`, uses `@WebMvcTest`
- **Integration tests**: `HelloControllerIntegrationTest.java` — Full Spring context with `@SpringBootTest(webEnvironment = RANDOM_PORT)`
- **Coverage**: Jacoco configured in `pom.xml`, enforced in CI but not locally (see `<jacoco-maven-plugin>` config)
- **Test profiles**: Use `@ActiveProfiles("test")` in integration tests to avoid actuator noise
- **Checkstyle warnings**: 100+ warnings exist (missing javadoc, import order, indentation level 4 vs 2 expected) — **non-blocking**, functionality validated

### Docker Build (Multi-Stage)
```bash
# Maven-based (default Dockerfile)
docker build -t learn-java:latest .      # Uses mvnw, layered JAR extraction
make docker-build                        # Makefile wrapper with version tagging

# Gradle-based (Dockerfile.gradle)
docker build -f Dockerfile.gradle -t learn-java:latest .

# Both produce ~200MB images with:
# - openjdk:21-jdk-slim base (build) → eclipse-temurin:21-jre-alpine (runtime)
# - Non-root user (uid 1001)
# - HEALTHCHECK using wget against /actuator/health
# - Layered JAR for caching (dependencies/spring-boot-loader/snapshot-dependencies/application layers)
```

### Helm & Kubernetes
- **Chart**: `k8s/learn-java/` (Helm v3, name: "learn-java", appVersion: "v0.0.12")
- **Deployment**: Uses `image.repository=dxas90/learn-java`, `service.port=8080`, `replicaCount=1`
- **Optional features** (toggle in `values.yaml`):
  - `autoscaling.enabled=false` (HPA with CPU target)
  - `persistence.enabled=false` (PVC for `/data`)
  - `httproute.enabled=false` (Gateway API routing)
- **Helm tests**: `k8s/learn-java/tests/*.yaml` use helm-unittest plugin (not present yet — CI references `k8s/learn-ruby` mistakenly)
- **CI deployment**: GitHub Actions workflow loads Docker image into Kind cluster, runs `helm upgrade --install learn-java k8s/learn-java --set image.pullPolicy=Never,image.tag=test`
- **E2E tests**: `scripts/e2e-test.sh` validates endpoints (`/`, `/ping`, `/healthz`, `/info`) inside Kind cluster using curl pod

## Project-Specific Conventions

### Configuration Management
- **`application.yml`**: Primary config (not `.properties`), profiles: `dev` (default), `prod`
- **Maven resource filtering**: `app.version: "@project.version@"` replaced at build time (see `pom.xml` `<resources>` plugin)
- **Environment variables**: `SPRING_PROFILES_ACTIVE`, `SERVER_PORT`, `APP_VERSION` override YAML defaults
- **Actuator**: Exposed endpoints: `health,info,metrics,prometheus` at `/actuator/*` (base path set in config)

### API Response Format (Matches Node.js Siblings)
```java
// All JSON endpoints return ApiResponse<T> record
ApiResponse.success(data)                 // { "success": true, "data": {...}, "timestamp": "..." }
ApiResponse.error("message", 500)         // { "error": true, "message": "...", "statusCode": 500, "timestamp": "..." }

// Controller pattern:
@GetMapping("/")
public ResponseEntity<ApiResponse<WelcomeData>> welcome() {
    return ResponseEntity.ok(ApiResponse.success(welcomeData));
}
```

### Logging Conventions
- **SLF4J with Logback**: Use `LoggerFactory.getLogger(Class.class)` — NOT `System.out.println`
- **Levels**: DEBUG for endpoint access (HelloController), INFO for startup/shutdown, WARN for actuator exposure notices, ERROR for exceptions
- **Emoji in logs**: Startup uses `🚀`, `📊`, `📖` for readability (see `Application.onApplicationReady()`)

### Code Quality Tools (Non-Blocking but Tracked)
- **Checkstyle**: `google_checks.xml` (modified), runs in CI `validate` phase — 100+ warnings expected (mostly indentation/javadoc)
- **SpotBugs**: `spotbugs-maven-plugin`, runs separately (`./mvnw spotbugs:check`) — **must be 0 bugs** (recently fixed EI_EXPOSE_REP, DLS_DEAD_LOCAL_STORE, DM_CONVERT_CASE)
- **Jacoco**: Coverage reporting only (no enforcement) — generates `target/site/jacoco/index.html`

## Integration Points & External Dependencies

### Maven Dependencies (Key Ones)
- **Spring Boot starters**: `spring-boot-starter-web`, `spring-boot-starter-actuator`, `spring-boot-starter-validation`, `spring-boot-starter-data-jpa`
- **Database**: `h2` (runtime scope) — in-memory, no schema files needed
- **Monitoring**: `micrometer-registry-prometheus` for metrics export
- **Documentation**: `springdoc-openapi-starter-webmvc-ui` 2.8.14 — provides Swagger UI at `/swagger-ui.html`
- **Testing**: `spring-boot-starter-test`, `spring-boot-testcontainers`, `testcontainers` (junit-jupiter, postgresql)
- **Dev tools**: `spring-boot-devtools` (optional, runtime scope, enables hot reload)

### CI/CD Pipeline (GitHub Actions `.github/workflows/full-workflow.yml`)
**Jobs Order**: lint → test (matrix: Java 21) → security-scan → helm-test → build → test-deployment → deploy (staging/production)

**Key CI Notes**:
1. **Lint job**: Runs `./mvnw checkstyle:check` (warnings allowed, not enforced)
2. **Test job**: Matrix for Java 21, runs `./mvnw clean test jacoco:report -Dcheckstyle.skip=true`, uploads coverage to Codecov
3. **Security-scan**: Runs `ossindex-maven-plugin:audit` (Maven Central vulnerability check)
4. **Helm-test**: Installs helm-unittest plugin, runs `helm unittest k8s/learn-ruby` (WRONG PATH — should be `k8s/learn-java`)
5. **Build**: Multi-platform Docker build (amd64/arm64), pushes to `ghcr.io/${{ github.repository }}`, saves image artifact for Kind
6. **Test-deployment**: Creates Kind cluster, loads image, Helm installs, runs `scripts/e2e-test.sh` + smoke tests
7. **Deploy**: FluxCD webhook triggers (staging on `main` branch, production on tags)

### Kind Test Cluster (`.github/kind-config.yaml`)
- Cluster name: `test-cluster`
- Port mappings: `80:80`, `443:443` (host:container) for Gateway API testing
- Used by CI `test-deployment` job and can be replicated locally

## Common Pitfalls & Important Notes

1. **Don't add tabs**: Code uses 4 spaces for indentation (not tabs) — `sed 's/\t/    /g'` fixes violations
2. **Record immutability**: When creating records with mutable fields (Lists, Maps), use defensive copying in compact constructor + override accessor with unmodifiable wrapper
3. **Locale in string ops**: Use `Locale.ROOT` for `.toLowerCase()`/`.toUpperCase()` to avoid locale-dependent bugs (e.g., Turkish i)
4. **Actuator warnings**: Spring Boot logs "open-in-view is enabled by default" and "SpringDoc endpoints enabled" warnings — **expected** in dev/test, disable with `spring.jpa.open-in-view=false` and `springdoc.api-docs.enabled=false` in production
5. **Helm chart path**: CI mistakenly references `k8s/learn-ruby` in helm-unittest job — should be `k8s/learn-java` (known issue)
6. **E2E endpoint list**: Tests validate `/`, `/ping`, `/healthz`, `/info` — no `/version` or `/echo` endpoints exist (removed to match core learning endpoints)
7. **Maven wrapper**: Always use `./mvnw` (not `mvn`) to ensure consistent Maven version (3.9.x)

## Key Files & Directories

**Must-read for understanding**:
- `pom.xml` — Dependencies, plugins (Spring Boot, Jacoco, Checkstyle, SpotBugs), resource filtering config
- `src/main/resources/application.yml` — Profiles, server config, actuator settings, logging levels
- `src/main/java/com/learn/springboot/HelloController.java` — All REST endpoints, demonstrates controller patterns
- `src/main/java/com/learn/springboot/dto/ApiResponse.java` — Response wrapper pattern with static factory methods
- `Makefile` — Developer commands, version strategy (git tags → branch → commit hash), Docker/Helm shortcuts

**Modify with care**:
- `src/main/java/com/learn/springboot/Application.java` — App entry point, startup logging
- `.github/workflows/full-workflow.yml` — CI pipeline (fix helm-test path bug)
- `k8s/learn-java/values.yaml` — Helm defaults (image repo, ports, toggles)
- `scripts/e2e-test.sh` — Kubernetes deployment validation logic

## AI Agent Quick Start Checklist

When working on this codebase:
1. **Before modifying code**: Run `./mvnw clean test` to ensure baseline passes (9/9 tests)
2. **After editing Java files**: Run `./mvnw spotbugs:check` to catch bugs (must be 0), optionally `./mvnw checkstyle:check` for style (warnings OK)
3. **Adding new endpoints**: Follow `HelloController` pattern — use `ApiResponse<T>` wrapper, add `@Operation` for Swagger, log with SLF4J
4. **Creating DTOs**: Use Java records with defensive copying for collections (see `WelcomeData` compact constructor)
5. **Testing new features**: Write both unit test (`@WebMvcTest` with mocks) and integration test (`@SpringBootTest` full context)
6. **Helm changes**: Update `k8s/learn-java/values.yaml` and add corresponding tests in `k8s/learn-java/tests/` using helm-unittest
7. **CI failures**: Check `helm-test` job path bug, verify e2e tests use correct endpoint list (no `/version`, `/echo`)

For unclear patterns or missing context, ask about specific files or workflow steps — this guide covers the 80% case, edge cases exist in integration tests and CI scripts.
