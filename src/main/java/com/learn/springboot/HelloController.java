package com.learn.springboot;

import com.learn.springboot.dto.HealthData;
import com.learn.springboot.dto.SystemInfo;
import com.learn.springboot.dto.WelcomeData;
import com.learn.springboot.service.SystemInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Learn Java API", description = "Main API endpoints matching Node.js structure")
public class HelloController {

    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Value("${spring.application.name:learn-java}")
    private String applicationName;

    @Value("${app.version:1.0.0}")
    private String applicationVersion;

    @Value("${spring.profiles.active:development}")
    private String environment;

    @Autowired
    private SystemInfoService systemInfoService;

    @GetMapping("/")
    @Operation(summary = "Get welcome message", description = "Returns welcome message with available endpoints")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved welcome message")
    })
    public ResponseEntity<com.learn.springboot.dto.ApiResponse<WelcomeData>> index() {
        logger.info("Welcome endpoint accessed");

        List<WelcomeData.EndpointInfo> endpoints = List.of(
                new WelcomeData.EndpointInfo("/", "GET", "Welcome message with available endpoints"),
                new WelcomeData.EndpointInfo("/ping", "GET", "Simple ping-pong response"),
                new WelcomeData.EndpointInfo("/healthz", "GET", "Health check endpoint"),
                new WelcomeData.EndpointInfo("/info", "GET", "Application information")
        );

        WelcomeData welcomeData = new WelcomeData(
                "Welcome to Learn Java API! 🚀",
                applicationName,
                applicationVersion,
                environment,
                endpoints
        );

        com.learn.springboot.dto.ApiResponse<WelcomeData> response = com.learn.springboot.dto.ApiResponse.success(welcomeData);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ping")
    @Operation(summary = "Ping endpoint", description = "Simple ping-pong endpoint for connectivity testing")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pong response received")
    })
    public ResponseEntity<String> ping() {
        logger.info("Ping endpoint accessed");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body("pong");
    }

    @GetMapping("/healthz")
    @Operation(summary = "Health check", description = "Returns application health status with system metrics")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Application is healthy")
    })
    public ResponseEntity<com.learn.springboot.dto.ApiResponse<HealthData>> healthz() {
        logger.info("Health endpoint accessed");

        HealthData healthData = systemInfoService.getHealthData();
        com.learn.springboot.dto.ApiResponse<HealthData> response = com.learn.springboot.dto.ApiResponse.success(healthData);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    @Operation(summary = "Application information", description = "Returns detailed application and system information")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved application information")
    })
    public ResponseEntity<com.learn.springboot.dto.ApiResponse<SystemInfo>> info() {
        logger.info("Info endpoint accessed");

        SystemInfo systemInfo = systemInfoService.getSystemInfo();
        com.learn.springboot.dto.ApiResponse<SystemInfo> response = com.learn.springboot.dto.ApiResponse.success(systemInfo);

        return ResponseEntity.ok(response);
    }
}
