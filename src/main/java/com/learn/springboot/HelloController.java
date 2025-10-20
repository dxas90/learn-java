package com.learn.springboot;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Hello API", description = "Simple greeting and health check operations")
public class HelloController {

    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Value("${app.name:Learn Java Spring Boot}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @GetMapping("/")
    @Operation(summary = "Get welcome message", description = "Returns a welcome message from the application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved welcome message")
    })
    public ResponseEntity<WelcomeResponse> index() {
        logger.info("Welcome endpoint accessed");
        WelcomeResponse response = new WelcomeResponse(
                "Hello World from Spring Boot!",
                appName,
                appVersion,
                LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ping")
    @Operation(summary = "Ping endpoint", description = "Simple ping-pong endpoint for connectivity testing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pong response received")
    })
    public ResponseEntity<Map<String, Object>> ping() {
        logger.debug("Ping endpoint accessed");
        return ResponseEntity.ok(Map.of(
                "message", "pong",
                "timestamp", LocalDateTime.now(),
                "status", "healthy"
        ));
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Returns application health status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application is healthy")
    })
    public ResponseEntity<Map<String, Object>> health() {
        logger.debug("Health endpoint accessed");
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "message", "Application is running smoothly",
                "timestamp", LocalDateTime.now(),
                "version", appVersion
        ));
    }

    @PostMapping("/greet")
    @Operation(summary = "Personalized greeting", description = "Returns a personalized greeting message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created personalized greeting"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided")
    })
    public ResponseEntity<GreetingResponse> greet(@Valid @RequestBody GreetingRequest request) {
        logger.info("Greeting requested for: {}", request.name());

        String message = String.format("Hello, %s! Welcome to %s", request.name(), appName);
        GreetingResponse response = new GreetingResponse(
                message,
                request.name(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // DTOs
    public record WelcomeResponse(
            String message,
            String applicationName,
            String version,
            LocalDateTime timestamp
    ) {}

    public record GreetingRequest(
            @NotBlank(message = "Name cannot be blank")
            @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
            String name
    ) {}

    public record GreetingResponse(
            String message,
            String name,
            LocalDateTime timestamp
    ) {}
}
