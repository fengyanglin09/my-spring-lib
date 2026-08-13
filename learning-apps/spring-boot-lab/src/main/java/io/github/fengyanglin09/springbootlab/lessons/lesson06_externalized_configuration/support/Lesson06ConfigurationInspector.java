package io.github.fengyanglin09.springbootlab.lessons.lesson06_externalized_configuration.support;

import io.github.fengyanglin09.springbootlab.lessons.lesson06_externalized_configuration.model.Lesson06ConfigurationSnapshot;
import io.github.fengyanglin09.springbootlab.lessons.lesson06_externalized_configuration.model.Lesson06LabProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Compares raw Environment values with the typed Lesson06LabProperties bean.
 *
 * <p>This class exists only to make configuration visible to the lesson test.
 * Normal application code usually injects the typed properties object directly
 * and does not repeatedly read raw strings from Environment.</p>
 */
@Component
@RequiredArgsConstructor
public class Lesson06ConfigurationInspector {

    /*
     * Environment is from Spring Framework.
     *
     * It is Spring's view of all property sources after Boot has loaded config
     * data, applied profile-specific files, and added command-line arguments.
     *
     * Environment answers questions such as:
     *
     * environment.getProperty("lesson06.lab.region")
     *
     * That call returns the final resolved value for that property key after
     * precedence rules have been applied.
     */
    private final Environment environment;

    /*
     * Lesson06LabProperties is a typed @ConfigurationProperties bean.
     *
     * Spring Boot creates it by binding Environment properties with the
     * "lesson06.lab" prefix into the record. Code that needs these settings can
     * depend on this typed object instead of manually reading strings.
     */
    private final Lesson06LabProperties labProperties;

    public Lesson06ConfigurationSnapshot inspect() {
        return new Lesson06ConfigurationSnapshot(
                Arrays.asList(environment.getActiveProfiles()),
                /*
                 * These raw Environment reads return strings because no target
                 * type is requested. They are useful for seeing the final keys
                 * and values Spring resolved.
                 */
                environment.getProperty("lesson06.lab.region"),
                environment.getProperty("lesson06.lab.refresh-interval"),
                environment.getProperty("lesson06.lab.security.require-https"),
                environment.getProperty("lesson06.lab.security.token-audience"),
                /*
                 * These values come from the typed bound object. Notice that
                 * refreshInterval is a Duration and requireHttps is a boolean,
                 * not raw strings.
                 */
                labProperties.region(),
                labProperties.refreshInterval(),
                labProperties.security().requireHttps(),
                labProperties.security().tokenAudience()
        );
    }
}
