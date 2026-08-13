package io.github.fengyanglin09.springbootlab.lessons.lesson05_auto_configuration;

import io.github.fengyanglin09.springbootlab.lessons.lesson05_auto_configuration.model.Lesson05AutoConfigurationSnapshot;
import io.github.fengyanglin09.springbootlab.lessons.lesson05_auto_configuration.service.Lesson05CustomGreetingService;
import io.github.fengyanglin09.springbootlab.lessons.lesson05_auto_configuration.service.Lesson05DefaultGreetingService;
import io.github.fengyanglin09.springbootlab.lessons.lesson05_auto_configuration.service.Lesson05GreetingService;
import io.github.fengyanglin09.springbootlab.lessons.lesson05_auto_configuration.support.Lesson05ConditionReportInspector;
import io.github.fengyanglin09.springbootlab.lessons.lesson05_auto_configuration.support.Lesson05GreetingLibraryMarker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Lesson 05's executable proof.
 *
 * <p>This test does not start the whole Spring Boot lab. Instead, it uses
 * ApplicationContextRunner to create tiny contexts with one auto-configuration
 * class. That makes the auto-configuration decision easy to observe.</p>
 *
 * <p>There is no {@code @SpringBootTest} here on purpose. {@code @SpringBootTest}
 * asks Boot to find the main application configuration and create a full test
 * ApplicationContext. Lesson 05 needs a smaller experiment: one context that
 * includes exactly one auto-configuration, plus whatever user bean or classpath
 * change the test adds.</p>
 */
class Lesson05AutoConfigurationTest {

    /*
     * ApplicationContextRunner is from Spring Boot Test.
     *
     * It is designed for testing auto-configuration. Each call to run(...) starts
     * a small ApplicationContext, lets you inspect it, then closes it.
     *
     * This is still a real Spring integration test in the narrow sense that a
     * real ApplicationContext is created and real bean conditions are evaluated.
     * It is not a full application integration test because it does not start
     * SpringBootLabApplication, scan every lesson package, or load the app's
     * complete set of beans.
     *
     * This is smaller and clearer than @SpringBootTest for Lesson 05 because we
     * want to control exactly which auto-configuration is present.
     *
     * Flow when a test calls contextRunner.run(...):
     *
     * Lesson05AutoConfigurationTest
     *     -> calls contextRunner.run(...)
     *
     * ApplicationContextRunner
     *     -> creates a small ConfigurableApplicationContext
     *     -> imports Lesson05GreetingAutoConfiguration
     *     -> evaluates @ConditionalOnClass
     *     -> evaluates @ConditionalOnMissingBean on the @Bean method
     *     -> creates matching beans
     *     -> passes the started context into the callback
     *     -> closes the context after the callback finishes
     */
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            /*
             * AutoConfigurations.of(...) tells the runner:
             *
             * "Include this class as auto-configuration for the tiny context."
             *
             * Real external starters usually list auto-configuration classes in
             * META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports.
             * This lesson imports the class directly so the mechanism stays
             * visible in one test file.
             */
            .withConfiguration(AutoConfigurations.of(Lesson05GreetingAutoConfiguration.class));

    /*
     * This helper is plain Java, not a Spring bean. The test creates it directly
     * because it only reads each short-lived test context.
     */
    private final Lesson05ConditionReportInspector inspector = new Lesson05ConditionReportInspector();

    @Test
    void autoConfigurationCreatesDefaultBeanWhenApplicationDoesNotProvideOne() {
        contextRunner.run(context -> {
            Lesson05AutoConfigurationSnapshot snapshot = inspector.inspect(
                    context.getSourceApplicationContext(),
                    Lesson05GreetingAutoConfiguration.class
            );

            /*
             * No user bean exists in this context. The auto-configuration class
             * matches because the classpath marker is present, the
             * @ConditionalOnMissingBean method matches, and the default
             * Lesson05GreetingService bean appears.
             *
             * The auto-configuration class itself is also present as a
             * configuration bean. That configuration bean is the source of the
             * @Bean method. It is not the same object as the greeting service.
             *
             * Important: "lesson05GreetingService" is a bean name, not a class
             * name. It appears because the @Bean method below is named
             * lesson05GreetingService().
             */
            assertThat(context).hasSingleBean(Lesson05GreetingService.class);
            assertThat(snapshot.autoConfigurationWasRegisteredAsConfigurationBean()).isTrue();
            assertThat(snapshot.autoConfigurationCreatedDefaultBean()).isTrue();
            assertThat(snapshot.conditionReportWasRecorded()).isTrue();
            assertThat(snapshot.greetingBeanName()).isEqualTo("lesson05GreetingService");
        });
    }

    @Test
    void autoConfigurationBacksOffWhenApplicationProvidesBeanOfSameType() {
        contextRunner
                /*
                 * withBean(...) registers an application/user bean before
                 * auto-configuration creates its defaults.
                 *
                 * The bean type is Lesson05GreetingService. That is important:
                 * @ConditionalOnMissingBean checks by type, so this one custom
                 * bean is enough to make the default back off.
                 */
                .withBean(
                        "lesson05CustomGreetingService",
                        Lesson05GreetingService.class,
                        () -> new Lesson05CustomGreetingService("student")
                )
                .run(context -> {
                    Lesson05AutoConfigurationSnapshot snapshot = inspector.inspect(
                            context.getSourceApplicationContext(),
                            Lesson05GreetingAutoConfiguration.class
                    );

                    /*
                     * There is still exactly one Lesson05GreetingService bean,
                     * but it is the custom bean. The default bean named
                     * lesson05GreetingService was not created. The
                     * auto-configuration class can still be present as a
                     * configuration bean; its @Bean method simply backs off.
                     */
                    assertThat(context).hasSingleBean(Lesson05GreetingService.class);
                    assertThat(context).doesNotHaveBean("lesson05GreetingService");
                    assertThat(snapshot.autoConfigurationWasRegisteredAsConfigurationBean()).isTrue();
                    assertThat(snapshot.userBeanMadeAutoConfigurationBackOff()).isTrue();
                    assertThat(snapshot.conditionReportWasRecorded()).isTrue();
                });
    }

    @Test
    void autoConfigurationDoesNotMatchWhenRequiredClassIsMissingFromClasspath() {
        contextRunner
                /*
                 * FilteredClassLoader is from Spring Boot Test.
                 *
                 * It starts the tiny context with a classloader that pretends
                 * Lesson05GreetingLibraryMarker is not available. This simulates
                 * the common auto-configuration question:
                 *
                 * "Is the library/class this auto-configuration supports on
                 * the classpath?"
                 *
                 * Notice that we hide the marker class, not
                 * Lesson05GreetingService. That separation keeps the lesson
                 * clear:
                 *
                 * - marker class missing -> auto-configuration is not relevant
                 * - service bean missing -> auto-configuration may create a
                 *   default service bean
                 */
                .withClassLoader(new FilteredClassLoader(Lesson05GreetingLibraryMarker.class))
                .run(context -> {
                    Lesson05AutoConfigurationSnapshot snapshot = inspector.inspect(
                            context.getSourceApplicationContext(),
                            Lesson05GreetingAutoConfiguration.class
                    );

                    /*
                     * Because @ConditionalOnClass does not match, the
                     * auto-configuration should not create the default bean.
                     */
                    assertThat(context).doesNotHaveBean("lesson05GreetingService");
                    assertThat(snapshot.missingClasspathInputPreventedDefaultBean()).isTrue();
                    assertThat(snapshot.conditionReportWasRecorded()).isTrue();
                });
    }

    /*
     * @AutoConfiguration is from Spring Boot autoconfigure.
     *
     * The short mental model is:
     *
     * @Configuration
     *     = "Here are bean definitions for this application."
     *
     * @AutoConfiguration
     *     = "Here are default bean definitions that Spring Boot may apply
     *        automatically, usually only if certain conditions match."
     *
     * It marks a configuration class as something Boot may import as part of its
     * auto-configuration process. It is different from ordinary application
     * component scanning:
     *
     * - @Service and @Component are usually found by scanning your app package.
     * - @AutoConfiguration classes are usually listed in AutoConfiguration.imports
     *   and selected by Boot.
     *
     * This class is nested inside the test on purpose. If we put it under the
     * main application package as a normal top-level @Configuration class,
     * component scanning could find it and blur the lesson. Here, the runner
     * imports it explicitly through AutoConfigurations.of(...).
     *
     * Process when conditions match:
     *
     * ApplicationContextRunner
     *     -> imports Lesson05GreetingAutoConfiguration
     *     -> Spring treats it as a configuration bean
     *     -> Spring reads its @Bean methods
     *     -> matching @Bean methods register bean definitions
     *     -> singleton beans are created from those definitions
     *
     * So yes, the auto-configuration class can be a bean itself. But its job is
     * to provide bean definitions. It is not the final service object the app
     * wants to use.
     */
    @AutoConfiguration
    /*
     * @ConditionalOnClass is from Spring Boot autoconfigure.
     *
     * It says: "only apply this auto-configuration if this class is available
     * from the classpath." Real Boot auto-configurations use this constantly.
     * This condition does not ask whether a Spring bean exists. It only asks
     * whether the Java class can be loaded.
     *
     * Lesson 05 deliberately separates two questions:
     *
     * 1. @ConditionalOnClass(Lesson05GreetingLibraryMarker)
     *        -> Is the pretend greeting library available on the classpath?
     *
     * 2. @ConditionalOnMissingBean(Lesson05GreetingService)
     *        -> Has the application already defined a greeting service bean?
     *
     * If question 1 is yes and question 2 is no, the @Bean method below can
     * create the default Lesson05GreetingService bean.
     *
     * Example mental model:
     * - if a JDBC driver and DataSource classes are present, database defaults
     *   may become possible
     * - if web classes are present, web MVC defaults may become possible
     * - if a required library is missing, that auto-configuration stays inactive
     *
     * The name attribute avoids forcing the class to load just because the
     * annotation is read. That matters when testing the "class is missing" case.
     */
    @ConditionalOnClass(name = "io.github.fengyanglin09.springbootlab.lessons.lesson05_auto_configuration.support.Lesson05GreetingLibraryMarker")
    static class Lesson05GreetingAutoConfiguration {

        /*
         * @Bean is from Spring Framework.
         *
         * This method creates the default Lesson05GreetingService bean when the
         * conditions allow it. The default bean name is the method name:
         * "lesson05GreetingService".
         *
         * It does not guarantee the bean will be created. Spring calls this
         * method only if:
         *
         * - the class-level @ConditionalOnClass condition matched
         * - this method-level @ConditionalOnMissingBean condition matched
         *
         * More precisely, Spring first registers a bean definition for this
         * method. When the context creates non-lazy singleton beans, it calls
         * the method and stores the returned object as the bean.
         *
         * That means there is no need for a class annotated with @Service or
         * @Component. If this method runs, Spring registers the returned object
         * as a bean named "lesson05GreetingService".
         */
        @Bean
        /*
         * @ConditionalOnMissingBean is from Spring Boot autoconfigure.
         *
         * It says: "create this bean only if the application does not already
         * have a bean of this type."
         *
         * This is the backing-off behavior Spring Boot is famous for. Boot gives
         * you useful defaults, but if you define your own bean, Boot usually
         * steps aside instead of creating a duplicate.
         */
        @ConditionalOnMissingBean(Lesson05GreetingService.class)
        Lesson05GreetingService lesson05GreetingService() {
            /*
             * The returned object is plain Java. It becomes a Spring bean only
             * because this @Bean method returns it while the context is starting.
             *
             * Bean name: lesson05GreetingService
             * Bean type: Lesson05GreetingService
             * Actual object class: Lesson05DefaultGreetingService
             */
            return new Lesson05DefaultGreetingService("lesson05-auto-configuration");
        }
    }
}
