package com.learn.springboot;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Value("${spring.application.name:learn-java}")
    private String applicationName;

    @Value("${app.version:unknown}")
    private String applicationVersion;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.info("🚀 Application '{}' version '{}' is ready and running!",
                applicationName, applicationVersion);
        logger.info("📊 Actuator endpoints available at /actuator");
        logger.info("📖 API documentation available at /swagger-ui.html");
    }

    @Bean
    @Profile("debug")
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            logger.info("🔍 Inspecting Spring Boot beans (debug mode):");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);

            logger.info("📦 Total beans loaded: {}", beanNames.length);

            if (logger.isDebugEnabled()) {
                for (String beanName : beanNames) {
                    logger.debug("Bean: {}", beanName);
                }
            }
        };
    }

}
