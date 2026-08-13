package io.github.fengyanglin09.springbootlab.lessons.lesson06_externalized_configuration;

import io.github.fengyanglin09.springbootlab.SpringBootLabApplication;
import io.github.fengyanglin09.springbootlab.lessons.lesson06_externalized_configuration.model.Lesson06ConfigurationSnapshot;
import io.github.fengyanglin09.springbootlab.lessons.lesson06_externalized_configuration.support.Lesson06ConfigurationInspector;
import org.junit.jupiter.api.Test;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Lesson 06's executable proof.
 *
 * <p>This test starts Spring Boot through SpringApplication because config data,
 * profile files, and command-line overrides are part of Boot's startup process.
 * The goal is to see real Boot configuration loading, not a mocked map of
 * values.</p>
 */
class Lesson06ExternalizedConfigurationTest {

    @Test
    void springApplicationLoadsYamlProfileAndCommandLineOverridesThenBindsTypedProperties() {
        /*
         * SpringApplication is from Spring Boot.
         *
         * Lesson 06 uses it directly so the test can pass startup arguments that
         * affect config loading:
         *
         * --spring.config.name=lesson06-application
         *     -> tells Boot to load lesson06-application.yml instead of the
         *        usual application.yml name for this test
         *     -> this file currently lives in src/test/resources, so it is
         *        test-only configuration
         *
         * --spring.profiles.active=dev
         *     -> activates the dev profile, so Boot also loads
         *        lesson06-application-dev.yml
         *     -> the dev file overrides the base file for property keys that
         *        appear in both files
         *
         * --lesson06.lab.region=command-line-region
         *     -> command-line property that overrides YAML
         */
        SpringApplication application = new SpringApplication(SpringBootLabApplication.class);

        /*
         * WebApplicationType is from Spring Boot.
         *
         * A Spring Boot application can start different kinds of contexts:
         *
         * - SERVLET: a web app that starts servlet web infrastructure, such as
         *   Spring MVC and an embedded servlet server when those dependencies
         *   are available
         * - REACTIVE: a reactive web app when reactive web dependencies are
         *   available
         * - NONE: a regular non-web Spring ApplicationContext
         *
         * Lesson 06 is only about configuration loading. It does not need HTTP,
         * controllers, Tomcat, Netty, servlet filters, or a web server port.
         *
         * Setting WebApplicationType.NONE tells Boot:
         *
         * "Start the Spring context, load configuration, create beans, but do
         * not start this as a web application."
         *
         * The configuration behavior we are studying still happens. We are only
         * removing unrelated web startup work from the lesson.
         */
        application.setWebApplicationType(WebApplicationType.NONE);

        /*
         * Banner is from Spring Boot.
         *
         * By default, Spring Boot prints the startup banner before the app logs:
         *
         *   .   ____          _            __ _ _
         *  /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
         *
         * That banner is useful branding/feedback for normal app startup, but
         * it is not part of externalized configuration.
         *
         * Banner.Mode.OFF keeps the test output focused on the lesson. It does
         * not change which configuration files are loaded, which profiles are
         * active, or how @ConfigurationProperties binding works.
         */
        application.setBannerMode(Banner.Mode.OFF);

        /*
         * Full config flow for this test:
         *
         * SpringApplication.run(...)
         *     -> reads command-line args
         *     -> sees spring.config.name=lesson06-application
         *     -> loads lesson06-application.yml from the test classpath
         *     -> sees spring.profiles.active=dev
         *     -> loads lesson06-application-dev.yml
         *     -> lets profile-specific values override base YAML values for
         *        matching keys
         *     -> adds command-line args as a higher-priority property source
         *     -> lets command-line values override both base YAML and dev YAML
         *     -> creates the Environment
         *     -> creates Lesson06LabProperties by binding lesson06.lab.*
         *     -> creates Lesson06ConfigurationInspector
         *     -> returns the started ApplicationContext
         */
        try (ConfigurableApplicationContext context = application.run(
                /*
                 * This is a Spring Boot config-loading option.
                 *
                 * It tells Boot to look for lesson06-application.yml instead of
                 * the usual application.yml file name. Lesson 06 does this so
                 * its teaching config does not become global app config.
                 *
                 * Current project layout:
                 *
                 * - src/test/resources/lesson06-application.yml exists
                 * - src/main/resources/lesson06-application.yml does not exist
                 *
                 * If both folders had a file with the exact same classpath name,
                 * Maven's test classpath for this module puts test resources
                 * first:
                 *
                 * target/test-classes
                 * target/classes
                 *
                 * So during Maven tests, the file from src/test/resources would
                 * be found before the same-named file from src/main/resources.
                 * In a packaged application, src/test/resources is not included,
                 * so the main resource would be the one available at runtime.
                 */
                "--spring.config.name=lesson06-application",
                /*
                 * This is another Spring Boot config-loading option.
                 *
                 * It activates the dev profile, so Boot loads both:
                 *
                 * - lesson06-application.yml
                 * - lesson06-application-dev.yml
                 *
                 * The profile-specific file does not replace the whole base
                 * file. It overrides only matching property keys. For example:
                 *
                 * base file:
                 *     lesson06.lab.refresh-interval=30s
                 *
                 * dev file:
                 *     lesson06.lab.refresh-interval=5s
                 *
                 * final value:
                 *     5s
                 *
                 * But if a property exists only in the base file, the base value
                 * remains available.
                 */
                "--spring.profiles.active=dev",
                /*
                 * This is NOT a built-in Spring setting.
                 *
                 * This is our lesson-specific application property. The property
                 * name matches the @ConfigurationProperties prefix and record
                 * component:
                 *
                 * lesson06.lab.region
                 *     -> prefix: lesson06.lab
                 *     -> record component: region
                 *     -> Lesson06LabProperties.region()
                 *
                 * The base YAML file already says:
                 *
                 * lesson06:
                 *   lab:
                 *     region: file-default-region
                 *
                 * Passing --lesson06.lab.region=command-line-region proves that
                 * command-line properties override YAML values. The final value
                 * in Environment and Lesson06LabProperties should be
                 * "command-line-region".
                 */
                "--lesson06.lab.region=command-line-region",
                /*
                 * This is also a lesson-specific application property.
                 *
                 * The dotted property path maps to nested YAML and the nested
                 * Security record:
                 *
                 * lesson06.lab.security.require-https
                 *     -> prefix: lesson06.lab
                 *     -> nested property: security.requireHttps
                 *     -> Lesson06LabProperties.security().requireHttps()
                 *
                 * The base YAML file says require-https is true. Passing this
                 * command-line value sets the final value to false.
                 *
                 * This demonstrates the same precedence rule as region:
                 *
                 * command-line property wins over YAML property.
                 */
                "--lesson06.lab.security.require-https=false"
        )) {
            /*
             * context is the ConfigurableApplicationContext returned by
             * SpringApplication.run(...). At this point, Boot has already:
             *
             * - loaded configuration files
             * - applied the dev profile
             * - applied command-line overrides
             * - created the Environment
             * - created Spring beans
             *
             * context.getBean(Lesson06ConfigurationInspector.class) asks the
             * Spring container:
             *
             * "Give me the bean whose type is Lesson06ConfigurationInspector."
             *
             * That works because Lesson06ConfigurationInspector is annotated
             * with @Component, so component scanning registered it as a bean
             * during startup.
             *
             * Then .inspect() is an ordinary Java method call on that inspector
             * object. It reads both:
             *
             * - raw values from Environment
             * - typed values from Lesson06LabProperties
             *
             * and returns a Lesson06ConfigurationSnapshot record. The snapshot is
             * not a Spring bean; it is just a plain result object used by the
             * assertions below.
             *
             * Written in slower steps, this chain is:
             *
             * Lesson06ConfigurationInspector inspector =
             *         context.getBean(Lesson06ConfigurationInspector.class);
             *
             * Lesson06ConfigurationSnapshot snapshot = inspector.inspect();
             */
            Lesson06ConfigurationSnapshot snapshot = context
                    .getBean(Lesson06ConfigurationInspector.class)
                    .inspect();

            /*
             * The dev profile should be active because the test passed
             * --spring.profiles.active=dev.
             */
            assertThat(snapshot.activeProfiles()).containsExactly("dev");

            /*
             * The command-line values should win over YAML values.
             */
            assertThat(snapshot.commandLineOverridesWon()).isTrue();

            /*
             * The dev profile YAML should override base YAML for values not
             * overridden by command line.
             */
            assertThat(snapshot.profileSpecificYamlWasUsed()).isTrue();

            /*
             * Environment shows raw resolved property values. The typed
             * @ConfigurationProperties bean shows the same values converted into
             * Java types.
             */
            assertThat(snapshot.environmentAndTypedBindingAgree()).isTrue();
        }
    }
}
