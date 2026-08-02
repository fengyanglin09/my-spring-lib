package io.github.fengyanglin09.springbootlab.lessons.lesson03_application_startup_context.model;

import java.util.List;

/**
 * A read model for observing one Spring Boot startup.
 *
 * <p>Lesson 03 is about the chain from {@code SpringApplication.run(...)} to a
 * live {@code ApplicationContext}. This record keeps the observations together
 * so the test can ask lesson-shaped questions instead of reaching into Spring
 * APIs directly for every assertion.</p>
 */
public record Lesson03StartupSnapshot(
        String applicationContextId,
        boolean applicationContextActive,
        int beanDefinitionCount,
        boolean rootApplicationBeanRegistered,
        boolean lessonInspectorBeanRegistered,
        List<String> optionNames,
        List<String> nonOptionArgs,
        boolean lessonOptionPresent,
        String lessonOptionEnvironmentValue,
        int applicationRunnerInvocationCount,
        boolean applicationRunnerSawLessonOption,
        List<String> applicationRunnerNonOptionArgs
) {

    /**
     * SpringApplication.run(...) should return a real, active context with bean
     * definitions already loaded.
     */
    public boolean applicationContextStarted() {
        return applicationContextActive
                && applicationContextId != null
                && !applicationContextId.isBlank()
                && beanDefinitionCount > 0;
    }

    /**
     * Component scanning starts from the root application package, so it should
     * register both the application class and this lesson's inspector bean.
     */
    public boolean componentScanFoundLessonBeans() {
        return rootApplicationBeanRegistered && lessonInspectorBeanRegistered;
    }

    /**
     * ApplicationArguments is a Spring Boot abstraction over the raw String[]
     * passed to SpringApplication.run(...). It should expose parsed option
     * arguments and raw non-option arguments.
     *
     * In this lesson, "--lesson03.enabled=true" is an option argument and
     * "startup-input.txt" is a non-option argument.
     *
     * Option args start with "--"; examples include --debug and
     * --server.port=8081. Non-option args are bare values; examples include
     * startup-input.txt, orders.csv, and run-once.
     */
    public boolean commandLineArgumentsWereParsed() {
        return lessonOptionPresent
                && optionNames.contains("lesson03.enabled")
                && nonOptionArgs.contains("startup-input.txt");
    }

    /**
     * Boot also exposes command-line options as environment properties.
     *
     * lessonOptionEnvironmentValue stores the result of
     * environment.getProperty("lesson03.enabled"), which returns the value of
     * the property named lesson03.enabled.
     */
    public boolean commandLineOptionReachedEnvironment() {
        return "true".equals(lessonOptionEnvironmentValue);
    }

    /**
     * ApplicationRunner runs late in startup. By the time run(...) returns and
     * the test inspects the context, the runner should already have seen the
     * same parsed arguments.
     */
    public boolean applicationRunnerObservedStartupArguments() {
        return applicationRunnerInvocationCount == 1
                && applicationRunnerSawLessonOption
                && applicationRunnerNonOptionArgs.contains("startup-input.txt");
    }
}
