package com.learn.springboot.dto;

/**
 * Health check response DTO matching Node.js structure
 */
public record HealthData(
        String status,
        Double uptime,
        String timestamp,
        MemoryInfo memory,
        String version,
        String environment
) {

    public record MemoryInfo(
            Long rss,
            Long heapTotal,
            Long heapUsed,
            Long external,
            Long arrayBuffers
    ) {}
}
