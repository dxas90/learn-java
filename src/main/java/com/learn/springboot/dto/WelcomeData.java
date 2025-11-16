package com.learn.springboot.dto;

import java.util.List;
import java.util.Collections;

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

    public WelcomeData {
        // Defensive copy to prevent external mutation
        endpoints = endpoints != null ? List.copyOf(endpoints) : List.of();
    }

    @Override
    public List<EndpointInfo> endpoints() {
        return List.copyOf(endpoints);
    }

    public record EndpointInfo(
            String path,
            String method,
            String description
    ) {}
}
