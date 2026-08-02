package io.github.fengyanglin09.springbootlab.lessons.lesson03_application_startup_context;

import io.github.fengyanglin09.springbootlab.SpringBootLabApplication;
import io.github.fengyanglin09.springbootlab.lessons.lesson03_application_startup_context.model.Lesson03StartupSnapshot;
import io.github.fengyanglin09.springbootlab.lessons.lesson03_application_startup_context.support.Lesson03StartupInspector;
import org.junit.jupiter.api.Test;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Lesson 03's executable proof.
 *
 * <p>This test starts the app with SpringApplication directly. That keeps the
 * startup chain visible: configure the launcher, pass args, receive a live
 * context, inspect the context, then close it.</p>
 */
class Lesson03ApplicationStartupContextTest {

    @Test
    void springApplicationCreatesContextParsesArgsRunsRunnerAndReturnsContext() {
        /*
         * SpringApplication is from Spring Boot.
         *
         * It is the launcher usually called from main(...). Creating it directly
         * in the test lets the lesson show the startup sequence instead of
         * hiding it behind @SpringBootTest.
         */
        SpringApplication application = new SpringApplication(SpringBootLabApplication.class);

        /*
         * WebApplicationType is from Spring Boot.
         *
         * NONE says "start a normal Spring context, not a servlet or reactive
         * web server." Lesson 03 is about startup mechanics, so starting Tomcat
         * or another web runtime would be extra noise.
         */
        application.setWebApplicationType(WebApplicationType.NONE);

        /*
         * Banner is from Spring Boot.
         *
         * OFF removes the ASCII startup banner from this test's output. The
         * banner is useful in real apps, but it is not part of the lesson.
         */
        application.setBannerMode(Banner.Mode.OFF);

        /*
         * The option arg should become both an ApplicationArguments option and
         * an Environment property. The bare value should become a non-option arg.
         *
         * Examples:
         *
         * --lesson03.enabled=true
         *     -> option arg
         *     -> name: lesson03.enabled
         *     -> value: true
         *     -> also visible through Environment as lesson03.enabled
         *
         * startup-input.txt
         *     -> non-option arg
         *     -> a bare positional value
         *     -> not an Environment property because it has no option name
         *
         * More option args:
         *     -> --server.port=8081
         *     -> --debug
         *
         * More non-option args:
         *     -> orders.csv
         *     -> run-once
         *
         * Full startup flow for this test:
         *
         * Lesson03ApplicationStartupContextTest
         *     -> creates a SpringApplication object
         *     -> calls SpringApplication.run(...)
         *
         * SpringApplication.run(...)
         *     -> starts the Spring app
         *     -> scans for @Component classes
         *     -> finds Lesson03StartupRecorder
         *     -> creates Lesson03StartupRecorder as a bean
         *     -> sees it implements ApplicationRunner
         *     -> calls Lesson03StartupRecorder.run(args)
         *        -> this returns void back to SpringApplication.run(...)
         *     -> SpringApplication.run(...) continues finishing startup
         *     -> SpringApplication.run(...) returns the started ApplicationContext
         *
         * application.run(...) is SpringApplication.run(...). It owns the whole
         * startup sequence. Inside that sequence, Boot will call any
         * ApplicationRunner beans, including Lesson03StartupRecorder.run(args).
         * That recorder method returns void, then SpringApplication.run(...)
         * continues and finally returns this ConfigurableApplicationContext.
         */
        try (ConfigurableApplicationContext context = application.run(
                "--lesson03.enabled=true",
                "startup-input.txt"
        )) {
            /*
             * ConfigurableApplicationContext is from Spring Framework.
             *
             * SpringApplication.run(...) returns it after startup. The
             * try-with-resources block closes it at the end of the test, which
             * is important whenever a test starts a context manually.
             */
            Lesson03StartupSnapshot snapshot = context
                    .getBean(Lesson03StartupInspector.class)
                    .inspect();

            assertThat(snapshot.applicationContextStarted()).isTrue();
            assertThat(snapshot.componentScanFoundLessonBeans()).isTrue();
            assertThat(snapshot.commandLineArgumentsWereParsed()).isTrue();

            /*
             * This makes the Environment property value visible in the test:
             * environment.getProperty("lesson03.enabled") returned "true" and
             * the inspector copied that value into the snapshot.
             */
            assertThat(snapshot.lessonOptionEnvironmentValue()).isEqualTo("true");
            assertThat(snapshot.commandLineOptionReachedEnvironment()).isTrue();
            assertThat(snapshot.applicationRunnerObservedStartupArguments()).isTrue();
        }
    }
}
