package com.learn.springboot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.springboot.dto.HealthData;
import com.learn.springboot.dto.SystemInfo;
import com.learn.springboot.service.SystemInfoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HelloController.class)
@DisplayName("Hello Controller Tests")
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SystemInfoService systemInfoService;

    @Test
    @DisplayName("Should return welcome message with available endpoints")
    void shouldReturnWelcomeMessage() throws Exception {
        mockMvc.perform(get("/")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.message", containsString("Welcome to Learn Java API")))
                .andExpect(jsonPath("$.data.application", is("learn-java")))
                .andExpect(jsonPath("$.data.endpoints", hasSize(4)))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Should return plain text pong response for ping")
    void shouldReturnPongForPing() throws Exception {
        mockMvc.perform(get("/ping"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain"))
                .andExpect(content().string("pong"));
    }

    @Test
    @DisplayName("Should return health status with system metrics")
    void shouldReturnHealthStatus() throws Exception {
        // Mock the health data
        HealthData.MemoryInfo memoryInfo = new HealthData.MemoryInfo(1000L, 2000L, 500L, 0L, 0L);
        HealthData healthData = new HealthData("healthy", 123.45, "2023-01-01T12:00:00Z", memoryInfo, "1.0.0", "development");

        when(systemInfoService.getHealthData()).thenReturn(healthData);

        mockMvc.perform(get("/healthz")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("healthy")))
                .andExpect(jsonPath("$.data.uptime", notNullValue()))
                .andExpect(jsonPath("$.data.memory", notNullValue()))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Should return application and system information")
    void shouldReturnSystemInfo() throws Exception {
        // Mock the system info
        SystemInfo.ApplicationInfo appInfo = new SystemInfo.ApplicationInfo("learn-java", "1.0.0", "development", "2023-01-01T12:00:00Z");
        SystemInfo.MemoryUsage memory = new SystemInfo.MemoryUsage(1000L, 2000L, 500L, 0L, 0L);
        SystemInfo.CpuInfo cpu = new SystemInfo.CpuInfo(0L, 0L);
        SystemInfo.SystemDetails systemDetails = new SystemInfo.SystemDetails("linux", "amd64", "17.0.1", 123.45, memory, cpu);
        SystemInfo.EnvironmentInfo envInfo = new SystemInfo.EnvironmentInfo("development", "8080", "0.0.0.0");
        SystemInfo systemInfo = new SystemInfo(appInfo, systemDetails, envInfo);

        when(systemInfoService.getSystemInfo()).thenReturn(systemInfo);

        mockMvc.perform(get("/info")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.application.name", is("learn-java")))
                .andExpect(jsonPath("$.data.system.platform", notNullValue()))
                .andExpect(jsonPath("$.data.environment.port", is("8080")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }
}
