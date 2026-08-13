package io.github.fengyanglin09.springbootlab.lessons.lesson06_externalized_configuration.config;

import io.github.fengyanglin09.springbootlab.lessons.lesson06_externalized_configuration.model.Lesson06LabProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Enables the typed configuration object used in Lesson 06.
 *
 * <p>This class has no {@code @Bean} methods because it does not manually
 * create a service object. Its job is to tell Spring Boot, "please bind
 * properties with the lesson06.lab prefix into Lesson06LabProperties and
 * register that bound object as a bean."</p>
 */
/*
 * @Configuration is from Spring Framework.
 *
 * It says this class contributes configuration to the ApplicationContext.
 * Because this class lives under SpringBootLabApplication's root package,
 * component scanning finds it during normal app startup.
 *
 * proxyBeanMethods controls whether Spring creates a runtime proxy subclass for
 * this configuration class so calls between @Bean methods can be intercepted.
 *
 * Default behavior, proxyBeanMethods = true:
 *
 * @Bean
 * UserService userService() {
 *     return new UserService(userRepository());
 * }
 *
 * @Bean
 * UserRepository userRepository() {
 *     return new UserRepository();
 * }
 *
 * If userService() directly calls userRepository(), Spring intercepts that call
 * and returns the managed singleton UserRepository bean instead of letting the
 * Java method create a second plain object. This is called "full" configuration
 * mode and uses a CGLIB-generated subclass behind the scenes.
 *
 * Lesson 06 uses proxyBeanMethods = false:
 *
 * @Configuration(proxyBeanMethods = false)
 *
 * That means:
 *
 * "Do not proxy this configuration class. If it had @Bean methods that called
 * each other directly, those calls would be plain Java method calls."
 *
 * This is safe here because Lesson06Configuration has no @Bean methods at all.
 * It only enables Lesson06LabProperties with @EnableConfigurationProperties.
 * There are no inter-bean method calls for Spring to intercept.
 *
 * The practical rule:
 *
 * - Use the default/full behavior when @Bean methods call other @Bean methods
 *   in the same configuration class and you rely on Spring to preserve singleton
 *   semantics.
 * - Use proxyBeanMethods = false when the configuration class is just enabling
 *   features or each @Bean method is independent and dependencies are supplied
 *   through method parameters or constructors.
 */
@Configuration(proxyBeanMethods = false)
/*
 * @EnableConfigurationProperties is from Spring Boot.
 *
 * It enables one or more @ConfigurationProperties types explicitly. In this
 * lesson, it causes Spring Boot to:
 *
 * 1. read values from the Environment
 * 2. find values whose keys start with "lesson06.lab"
 * 3. bind those values into Lesson06LabProperties
 * 4. register the finished Lesson06LabProperties object as a Spring bean
 *
 * Without this annotation, Lesson06LabProperties would just be a Java record
 * with @ConfigurationProperties metadata; Spring would not automatically create
 * and inject it in this lesson.
 */
@EnableConfigurationProperties(Lesson06LabProperties.class)
public class Lesson06Configuration {
}
