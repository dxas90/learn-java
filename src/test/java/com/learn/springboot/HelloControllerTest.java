package com.learn.springboot;

import com.learn.springboot.service.SystemInfoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Hello Controller Tests")
class HelloControllerTest {

    @Autowired
    private HelloController helloController;

    @Autowired
    private SystemInfoService systemInfoService;

    @Test
    @DisplayName("Should return welcome message with available endpoints")
    void shouldReturnWelcomeMessage() {
        var response = helloController.index();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();
        assertThat(response.getBody().data().message()).contains("Welcome to Learn Java API");
        assertThat(response.getBody().data().application()).isEqualTo("learn-java");
        assertThat(response.getBody().data().endpoints()).hasSize(4);
    }

    @Test
    @DisplayName("Should return plain text pong response for ping")
    void shouldReturnPongForPing() {
        var response = helloController.ping();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo("pong");
    }

    @Test
    @DisplayName("Should return health status with system metrics")
    void shouldReturnHealthStatus() {
        var response = helloController.healthz();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();
        assertThat(response.getBody().data().status()).isEqualTo("healthy");
        assertThat(response.getBody().data().uptime()).isNotNull();
        assertThat(response.getBody().data().memory()).isNotNull();
    }

    @Test
    @DisplayName("Should return application and system information")
    void shouldReturnSystemInfo() {
        var response = helloController.info();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();
        assertThat(response.getBody().data().application().name()).isEqualTo("learn-java");
        assertThat(response.getBody().data().system()).isNotNull();
        assertThat(response.getBody().data().environment()).isNotNull();
    }
}
