package io.github.fengyanglin09.springintegrationlab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;

// @SpringBootApplication enables component scanning and Spring Boot auto-configuration.
@SpringBootApplication
// @EnableIntegration registers Spring Integration infrastructure beans.
@EnableIntegration
// @IntegrationComponentScan finds @MessagingGateway interfaces and creates gateway proxy beans.
@IntegrationComponentScan
public class SpringIntegrationLabApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringIntegrationLabApplication.class, args);
    }
}
