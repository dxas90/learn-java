package com.learn.springboot.dto;

import java.util.List;

/**
 * Welcome response DTO matching Node.js structure
 */
public record WelcomeData(
        String message,
        String application,
        String version,
        String environment,
        List<EndpointInfo> endpoints
) {

    public record EndpointInfo(
            String path,
            String method,
            String description
    ) {}
}
