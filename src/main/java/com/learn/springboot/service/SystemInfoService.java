package com.learn.springboot.service;

import com.learn.springboot.dto.HealthData;
import com.learn.springboot.dto.SystemInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.time.Instant;

/**
 * Service for gathering system and application information
 */
@Service
public class SystemInfoService {

    @Value("${spring.application.name:learn-java}")
    private String applicationName;

    @Value("${app.version:1.0.0}")
    private String applicationVersion;

    @Value("${spring.profiles.active:development}")
    private String environment;

    @Value("${server.port:8080}")
    private String port;

    private final long startTime = System.currentTimeMillis();

    public double getUptime() {
        return (System.currentTimeMillis() - startTime) / 1000.0;
    }

    public HealthData.MemoryInfo getMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();

        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();

        return new HealthData.MemoryInfo(
                totalMemory,           // rss equivalent
                maxMemory,             // heapTotal equivalent
                usedMemory,            // heapUsed equivalent
                0L,                    // external (not applicable in Java)
                0L                     // arrayBuffers (not applicable in Java)
        );
    }

    public SystemInfo.MemoryUsage getDetailedMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();

        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();

        return new SystemInfo.MemoryUsage(
                totalMemory,           // rss equivalent
                maxMemory,             // heapTotal equivalent
                usedMemory,            // heapUsed equivalent
                0L,                    // external (not applicable in Java)
                0L                     // arrayBuffers (not applicable in Java)
        );
    }

    public SystemInfo.CpuInfo getCpuInfo() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

        // These are simplified CPU metrics as Java doesn't provide direct equivalents
        return new SystemInfo.CpuInfo(
                0L,  // user time (simplified)
                0L   // system time (simplified)
        );
    }

    public SystemInfo getSystemInfo() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

        SystemInfo.ApplicationInfo appInfo = new SystemInfo.ApplicationInfo(
                applicationName,
                applicationVersion,
                environment,
                Instant.now().toString()
        );

        SystemInfo.SystemDetails systemDetails = new SystemInfo.SystemDetails(
                osBean.getName().toLowerCase(),
                osBean.getArch(),
                System.getProperty("java.version"),
                getUptime(),
                getDetailedMemoryUsage(),
                getCpuInfo()
        );

        SystemInfo.EnvironmentInfo envInfo = new SystemInfo.EnvironmentInfo(
                environment,
                port,
                "0.0.0.0"
        );

        return new SystemInfo(appInfo, systemDetails, envInfo);
    }

    public HealthData getHealthData() {
        return new HealthData(
                "healthy",
                getUptime(),
                Instant.now().toString(),
                getMemoryInfo(),
                applicationVersion,
                environment
        );
    }
}
