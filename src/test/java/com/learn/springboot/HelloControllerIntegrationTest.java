package com.learn.springboot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class HelloControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnWelcomeMessage() {
        ResponseEntity<String> response = this.restTemplate
                .getForEntity("http://localhost:" + port + "/", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Welcome to Learn Java API");
        assertThat(response.getBody()).contains("\"success\":true");
    }

    @Test
    void shouldReturnPongForPing() {
        ResponseEntity<String> response = this.restTemplate
                .getForEntity("http://localhost:" + port + "/ping", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("pong");
    }

    @Test
    void shouldReturnHealthStatus() {
        ResponseEntity<String> response = this.restTemplate
                .getForEntity("http://localhost:" + port + "/healthz", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"success\":true");
        assertThat(response.getBody()).contains("\"status\":\"healthy\"");
    }

    @Test
    void shouldReturnSystemInfo() {
        ResponseEntity<String> response = this.restTemplate
                .getForEntity("http://localhost:" + port + "/info", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"success\":true");
        assertThat(response.getBody()).contains("application");
        assertThat(response.getBody()).contains("system");
    }
}
