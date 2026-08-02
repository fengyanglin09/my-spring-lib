package io.github.fengyanglin09.springbootlab.lessons.lesson03_application_startup_context.support;

import io.github.fengyanglin09.springbootlab.lessons.lesson03_application_startup_context.model.Lesson03StartupSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Inspects the context created by SpringApplication.
 *
 * <p>This bean is deliberately observational. Production code should usually
 * receive its collaborators directly through constructor injection, not inspect
 * the bean registry. For Lesson 03, inspecting the registry helps make startup
 * visible.</p>
 */
/*
 * @Component is a Spring stereotype annotation. It tells component scanning,
 * "create one bean of this class and register it in the ApplicationContext."
 *
 * This matters for the lesson because SpringApplication starts component
 * scanning from SpringBootLabApplication's package, finds this class, and makes
 * it retrievable with context.getBean(Lesson03StartupInspector.class).
 */
@Component
/*
 * @RequiredArgsConstructor is from Lombok, not Spring. It generates a
 * constructor for every final field below. Spring then uses that constructor to
 * inject ConfigurableApplicationContext, ApplicationArguments, Environment, and
 * Lesson03StartupRecorder.
 *
 * This keeps the lesson focused on startup concepts instead of constructor
 * boilerplate.
 */
@RequiredArgsConstructor
public class Lesson03StartupInspector {

    /*
     * ConfigurableApplicationContext is from Spring Framework.
     *
     * ApplicationContext is the Spring container: it owns bean definitions and
     * created bean instances. The "Configurable" subtype is what Boot returns
     * from SpringApplication.run(...), and it can be closed when a programmatic
     * startup test is done.
     *
     * This lesson injects it so we can prove that run(...) created a live
     * context before application code retrieved this inspector bean.
     */
    private final ConfigurableApplicationContext applicationContext;

    /*
     * ApplicationArguments is from Spring Boot.
     *
     * Boot creates this object from the String[] passed to
     * SpringApplication.run(...), then registers it as a bean. It separates
     * option args, such as --lesson03.enabled=true, from non-option args, such
     * as startup-input.txt.
     *
     * Option args start with "--" and have a name, so Boot can expose them by
     * name. Non-option args are bare positional values, often file names,
     * command names, or one-time job inputs.
     *
     * Examples of option args:
     * - --lesson03.enabled=true
     * - --server.port=8081
     * - --debug
     *
     * Examples of non-option args:
     * - startup-input.txt
     * - orders.csv
     * - run-once
     *
     * This lesson uses it to show that startup arguments are not just raw
     * strings; Boot parses them into a useful object application code can inject.
     */
    private final ApplicationArguments applicationArguments;

    /*
     * Environment is from Spring Framework.
     *
     * It is Spring's view of properties and profiles from many sources:
     * command-line args, system properties, environment variables, config files,
     * and more. Boot contributes command-line options to it, which is why
     * --lesson03.enabled=true can be read as lesson03.enabled.
     *
     * environment.getProperty("lesson03.enabled") means:
     * "look up the property named lesson03.enabled and return its value."
     * In this lesson, the value should be "true" because the test starts the
     * app with --lesson03.enabled=true.
     *
     * This lesson checks Environment to show that startup args influence the
     * property system, not only ApplicationArguments.
     */
    private final Environment environment;

    /*
     * This is our lesson-specific ApplicationRunner bean. It proves that Boot
     * calls runner beans near the end of startup before run(...) returns.
     */
    private final Lesson03StartupRecorder startupRecorder;

    public Lesson03StartupSnapshot inspect() {
        return new Lesson03StartupSnapshot(
                applicationContext.getId(),
                applicationContext.isActive(),
                applicationContext.getBeanDefinitionCount(),
                applicationContext.containsBean("springBootLabApplication"),
                applicationContext.containsBean("lesson03StartupInspector"),
                new ArrayList<>(applicationArguments.getOptionNames()),
                /*
                 * getNonOptionArgs() returns bare arguments that did not start
                 * with "--". In the test, "startup-input.txt" lands here
                 * because it is positional input rather than a named option.
                 *
                 * Not non-option args: --lesson03.enabled=true, --debug
                 * Non-option args: startup-input.txt, orders.csv, run-once
                 */
                applicationArguments.getNonOptionArgs(),
                applicationArguments.containsOption("lesson03.enabled"),
                /*
                 * This returns the value for the property named
                 * "lesson03.enabled". Because the test passes
                 * --lesson03.enabled=true, the snapshot should receive "true".
                 */
                environment.getProperty("lesson03.enabled"),
                startupRecorder.invocationCount(),
                startupRecorder.sawOption("lesson03.enabled"),
                startupRecorder.nonOptionArgs()
        );
    }
}
