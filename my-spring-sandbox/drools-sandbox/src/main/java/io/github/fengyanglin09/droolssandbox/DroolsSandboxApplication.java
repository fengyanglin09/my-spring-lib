package io.github.fengyanglin09.droolssandbox;

import io.github.fengyanglin09.droolssandbox.drools.config.DiscountProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(DiscountProperties.class)
@SpringBootApplication
public class DroolsSandboxApplication {

    public static void main(String[] args) {
        SpringApplication.run(DroolsSandboxApplication.class, args);
    }

}
