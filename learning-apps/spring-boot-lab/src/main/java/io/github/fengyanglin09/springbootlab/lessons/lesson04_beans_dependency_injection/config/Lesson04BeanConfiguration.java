package io.github.fengyanglin09.springbootlab.lessons.lesson04_beans_dependency_injection.config;

import io.github.fengyanglin09.springbootlab.lessons.lesson04_beans_dependency_injection.support.Lesson04OrderNumberGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

/**
 * Explicit bean setup for Lesson 04.
 *
 * <p>Use a {@code @Configuration} class when you want to tell Spring how to
 * create one or more beans yourself. This is especially useful for third-party
 * classes, interfaces, clients, adapters, or objects where annotating the class
 * with {@code @Component} is not the best fit.</p>
 */
/*
 * @Configuration is from Spring Framework.
 *
 * It says: "this class contains bean definitions." Because this class is under
 * the root application package, component scanning finds it and registers it as
 * a bean named "lesson04BeanConfiguration".
 *
 * Then Spring reads the @Bean methods inside it and calls them while building
 * the ApplicationContext.
 */
@Configuration
public class Lesson04BeanConfiguration {

    /*
     * @Bean is from Spring Framework.
     *
     * It says: "call this method and register the returned object as a bean."
     * By default, the bean name is the method name, so this creates a bean named
     * "lesson04OrderNumberGenerator".
     *
     * Notice that the returned lambda is not annotated with @Component. That is
     * the point of this example: @Bean lets configuration code register a plain
     * Java object as a Spring-managed object.
     */
    @Bean
    public Lesson04OrderNumberGenerator lesson04OrderNumberGenerator() {
        /*
         * This lambda is the concrete implementation of the
         * Lesson04OrderNumberGenerator interface. Spring will keep the returned
         * object in the ApplicationContext and inject it anywhere a
         * Lesson04OrderNumberGenerator is required.
         */
        return request -> "L04-%s-%02d".formatted(
                request.customerId().toUpperCase(Locale.ROOT),
                request.itemCount()
        );
    }
}
