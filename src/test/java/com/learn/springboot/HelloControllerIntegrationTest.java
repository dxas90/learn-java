package com.learn.springboot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Hello Controller Integration Tests")
class HelloControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    @DisplayName("Should return welcome message via HTTP")
    void shouldReturnWelcomeMessage() {
        String url = "http://localhost:" + port + "/";
        String response = restTemplate.getForObject(url, String.class);

        assertThat(response).isNotNull();
        assertThat(response).contains("Welcome to Learn Java API");
        assertThat(response).contains("success");
    }

    @Test
    @DisplayName("Should return pong via HTTP")
    void shouldReturnPongForPing() {
        String url = "http://localhost:" + port + "/ping";
        String response = restTemplate.getForObject(url, String.class);

        assertThat(response).isEqualTo("pong");
    }

    @Test
    @DisplayName("Should return health status via HTTP")
    void shouldReturnHealthStatus() {
        String url = "http://localhost:" + port + "/healthz";
        String response = restTemplate.getForObject(url, String.class);

        assertThat(response).isNotNull();
        assertThat(response).contains("success");
        assertThat(response).contains("healthy");
    }

    @Test
    @DisplayName("Should return system info via HTTP")
    void shouldReturnSystemInfo() {
        String url = "http://localhost:" + port + "/info";
        String response = restTemplate.getForObject(url, String.class);

        assertThat(response).isNotNull();
        assertThat(response).contains("success");
        assertThat(response).contains("application");
        assertThat(response).contains("system");
    }
}
