/**
 * Lesson 06 objective: understand how Spring Boot turns external configuration
 * into Environment values and typed configuration objects.
 *
 * <p>Study path:</p>
 *
 * <ol>
 *     <li>Load a lesson-specific YAML file with {@code spring.config.name}.</li>
 *     <li>Activate a profile with {@code spring.profiles.active}.</li>
 *     <li>See profile-specific YAML override base YAML.</li>
 *     <li>See command-line properties override YAML.</li>
 *     <li>Read final values through Spring's {@code Environment}.</li>
 *     <li>Bind the same final values into a typed
 *     {@code @ConfigurationProperties} record.</li>
 * </ol>
 */
package io.github.fengyanglin09.springbootlab.lessons.lesson06_externalized_configuration;
