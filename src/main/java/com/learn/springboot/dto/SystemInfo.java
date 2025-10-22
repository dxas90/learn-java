package com.learn.springboot.dto;

/**
 * System information response DTO matching Node.js structure
 */
public record SystemInfo(
        ApplicationInfo application,
        SystemDetails system,
        EnvironmentInfo environment
) {

    public record ApplicationInfo(
            String name,
            String version,
            String environment,
            String timestamp
    ) {}

    public record SystemDetails(
            String platform,
            String arch,
            String javaVersion,
            Double uptime,
            MemoryUsage memory,
            CpuInfo cpu
    ) {}

    public record EnvironmentInfo(
            String nodeEnv,
            String port,
            String host
    ) {}

    public record MemoryUsage(
            Long rss,
            Long heapTotal,
            Long heapUsed,
            Long external,
            Long arrayBuffers
    ) {}

    public record CpuInfo(
            Long user,
            Long system
    ) {}
}
