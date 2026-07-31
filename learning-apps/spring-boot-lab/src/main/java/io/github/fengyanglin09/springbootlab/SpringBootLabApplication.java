package io.github.fengyanglin09.springbootlab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The root application class for the Spring Boot lab.
 *
 * <p>Keeping this class in the {@code io.github.fengyanglin09.springbootlab}
 * root package is intentional: {@link SpringBootApplication} starts component
 * scanning from this package, so lesson beans under {@code lessons} are found
 * automatically.</p>
 */
@SpringBootApplication
public class SpringBootLabApplication {

    public static void main(String[] args) {
        // Lesson 01 focus: this single call asks Boot to create and start the Spring application context.
        SpringApplication.run(SpringBootLabApplication.class, args);
    }
}
