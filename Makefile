SHELL=/bin/bash -o pipefail

# Application configuration
APP_NAME := learn-java
DOCKER_REPO := dxas90
REGISTRY := ghcr.io

# Version strategy using git tags
GIT_BRANCH := $(shell git rev-parse --abbrev-ref HEAD)
GIT_TAG := $(shell git describe --exact-match --abbrev=0 2>/dev/null || echo "")
COMMIT_HASH := $(shell git rev-parse --verify HEAD)
COMMIT_TIMESTAMP := $(shell git show -s --format=%ci | cut -d' ' -f1,2 | tr ' ' 'T')

VERSION := $(shell git describe --tags --always --dirty)
VERSION_STRATEGY := commit_hash

ifdef GIT_TAG
	VERSION := $(GIT_TAG)
	VERSION_STRATEGY := tag
else
	ifeq (,$(findstring $(GIT_BRANCH),main master HEAD))
		ifneq (,$(patsubst release-%,,$(GIT_BRANCH)))
			VERSION := $(GIT_BRANCH)
			VERSION_STRATEGY := branch
		endif
	endif
endif

# Colors for output
RED := \033[31m
GREEN := \033[32m
YELLOW := \033[33m
BLUE := \033[34m
RESET := \033[0m

.PHONY: help build test clean run docker-build docker-run docker-compose k8s-deploy version lint security

## Show this help message
help:
	@echo -e "$(BLUE)Available commands:$(RESET)"
	@awk '/^[a-zA-Z\-\_0-9%:\\ ]+:/ { \
		helpMessage = match(lastLine, /^## (.*)/); \
		if (helpMessage) { \
			helpCommand = $$1; \
			helpMessage = substr(lastLine, RSTART + 3, RLENGTH); \
			gsub(":", "", helpCommand); \
			printf "  $(GREEN)%-20s$(RESET) %s\n", helpCommand, helpMessage; \
		} \
	} \
	{ lastLine = $$0 }' $(MAKEFILE_LIST)

## Build the application using Maven
build:
	@echo -e "$(BLUE)Building application...$(RESET)"
	./mvnw clean compile

## Build the application JAR
package:
	@echo -e "$(BLUE)Packaging application...$(RESET)"
	./mvnw clean package -DskipTests

## Run tests with coverage
test:
	@echo -e "$(BLUE)Running tests...$(RESET)"
	./mvnw clean test jacoco:report

## Run integration tests
test-integration:
	@echo -e "$(BLUE)Running integration tests...$(RESET)"
	./mvnw test -Dtest="*IntegrationTest"

## Clean build artifacts
clean:
	@echo -e "$(BLUE)Cleaning build artifacts...$(RESET)"
	./mvnw clean
	@if command -v docker > /dev/null 2>&1; then \
		echo "$(BLUE)Cleaning Docker artifacts...$(RESET)"; \
		docker system prune -f || echo "$(YELLOW)Warning: Could not clean Docker artifacts$(RESET)"; \
	else \
		echo "$(YELLOW)Docker not available, skipping Docker cleanup$(RESET)"; \
	fi

## Run the application locally
run:
	@echo -e "$(BLUE)Starting application locally...$(RESET)"
	./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

## Run with production profile
run-prod:
	@echo -e "$(BLUE)Starting application with production profile...$(RESET)"
	./mvnw spring-boot:run -Dspring-boot.run.profiles=prod

## Build Docker image using Maven
docker-build:
	@echo -e "$(BLUE)Building Docker image...$(RESET)"
	docker build -t $(APP_NAME):$(VERSION) .
	docker tag $(APP_NAME):$(VERSION) $(APP_NAME):latest

## Build Docker image using Gradle
docker-build-gradle:
	@echo -e "$(BLUE)Building Docker image with Gradle...$(RESET)"
	docker build -f Dockerfile.gradle -t $(APP_NAME):$(VERSION) .
	docker tag $(APP_NAME):$(VERSION) $(APP_NAME):latest

## Run Docker container
docker-run:
	@echo -e "$(BLUE)Running Docker container...$(RESET)"
	docker run -it --rm -p 8080:8080 --name $(APP_NAME) $(APP_NAME):$(VERSION)

## Start application with Docker Compose
docker-compose:
	@echo -e "$(BLUE)Starting services with Docker Compose...$(RESET)"
	docker-compose up --build

## Stop Docker Compose services
docker-compose-down:
	@echo -e "$(BLUE)Stopping Docker Compose services...$(RESET)"
	docker-compose down -v

## Deploy to Kubernetes using kubectl
k8s-deploy:
	@echo -e "$(BLUE)Deploying to Kubernetes...$(RESET)"
	kubectl apply -f k8s/

## Deploy using Helm
helm-deploy:
	@echo -e "$(BLUE)Deploying with Helm...$(RESET)"
	helm upgrade --install $(APP_NAME) ./charts/learn-java

## Remove Kubernetes deployment
k8s-undeploy:
	@echo -e "$(BLUE)Removing Kubernetes deployment...$(RESET)"
	kubectl delete -f k8s/

## Run security scan with Trivy
security:
	@echo -e "$(BLUE)Running security scan...$(RESET)"
	trivy fs --exit-code 1 --severity HIGH,CRITICAL .

## Run code quality checks
lint:
	@echo -e "$(BLUE)Running code quality checks...$(RESET)"
	./mvnw checkstyle:check spotbugs:check

## Run tests without checkstyle (for CI/CD)
test-no-style:
	@echo -e "$(BLUE)Running tests without checkstyle...$(RESET)"
	./mvnw clean test jacoco:report -Dcheckstyle.skip=true

## Show application logs
logs:
	@echo -e "$(BLUE)Showing application logs...$(RESET)"
	kubectl logs -l app=$(APP_NAME) --tail=100 -f

## Open monitoring dashboards
monitoring:
	@echo -e "$(BLUE)Opening monitoring dashboards...$(RESET)"
	@echo -e "Prometheus: http://localhost:9090"
	@echo -e "Grafana: http://localhost:3000 (admin/admin)"
	@echo -e "Application: http://localhost:8080"
	@echo -e "Swagger UI: http://localhost:8080/swagger-ui.html"

## Show version information
version:
	@echo -e "$(BLUE)Version Information:$(RESET)"
	@echo -e "Version: $(VERSION)"
	@echo -e "Strategy: $(VERSION_STRATEGY)"
	@echo -e "Git Tag: $(GIT_TAG)"
	@echo -e "Git Branch: $(GIT_BRANCH)"
	@echo -e "Commit Hash: $(COMMIT_HASH)"
	@echo -e "Commit Timestamp: $(COMMIT_TIMESTAMP)"

## Setup development environment
dev-setup:
	@echo -e "$(BLUE)Setting up development environment...$(RESET)"
	@echo -e "Installing pre-commit hooks..."
	# Add pre-commit hooks setup here
	@echo -e "$(GREEN)Development environment ready!$(RESET)"

## Quick start - build, test, and run locally
quick-start: clean test package run

## Full pipeline - test, build, and deploy locally
full-pipeline: test security docker-build docker-compose

## Release - tag and build for release
release:
	@echo -e "$(BLUE)Preparing release $(VERSION)...$(RESET)"
	git tag -a v$(VERSION) -m "Release version $(VERSION)"
	$(MAKE) docker-build
	@echo -e "$(GREEN)Release $(VERSION) ready!$(RESET)"
