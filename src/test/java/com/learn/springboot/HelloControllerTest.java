package com.learn.springboot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HelloController.class)
@DisplayName("Hello Controller Tests")
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should return welcome message with application info")
    void shouldReturnWelcomeMessage() throws Exception {
        mockMvc.perform(get("/api/v1/")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Hello World from Spring Boot!")))
                .andExpect(jsonPath("$.applicationName", notNullValue()))
                .andExpect(jsonPath("$.version", notNullValue()))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Should return pong response for ping")
    void shouldReturnPongForPing() throws Exception {
        mockMvc.perform(get("/api/v1/ping")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("pong")))
                .andExpect(jsonPath("$.status", is("healthy")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Should return health status")
    void shouldReturnHealthStatus() throws Exception {
        mockMvc.perform(get("/api/v1/health")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.message", notNullValue()))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.version", notNullValue()));
    }

    @Test
    @DisplayName("Should create personalized greeting with valid name")
    void shouldCreatePersonalizedGreeting() throws Exception {
        HelloController.GreetingRequest request = new HelloController.GreetingRequest("John");
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/greet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("Hello, John!")))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Should return validation error for empty name")
    void shouldReturnValidationErrorForEmptyName() throws Exception {
        HelloController.GreetingRequest request = new HelloController.GreetingRequest("");
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/greet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.details.name", notNullValue()));
    }

    @Test
    @DisplayName("Should return validation error for too long name")
    void shouldReturnValidationErrorForTooLongName() throws Exception {
        String longName = "a".repeat(51); // 51 characters
        HelloController.GreetingRequest request = new HelloController.GreetingRequest(longName);
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/greet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.details.name", notNullValue()));
    }
}
