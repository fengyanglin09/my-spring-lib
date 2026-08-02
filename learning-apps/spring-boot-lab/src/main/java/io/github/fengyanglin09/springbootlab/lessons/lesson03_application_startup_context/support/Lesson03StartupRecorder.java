package io.github.fengyanglin09.springbootlab.lessons.lesson03_application_startup_context.support;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Records the arguments seen by an ApplicationRunner during startup.
 *
 * <p>ApplicationRunner exists for code that should run after the context is
 * created but before {@code SpringApplication.run(...)} has fully completed.
 * This recorder keeps the lesson observable without doing real startup work.</p>
 */
/*
 * @Component makes this recorder a Spring bean. Because it also implements
 * ApplicationRunner, Boot will discover and call it during startup.
 */
@Component
public class Lesson03StartupRecorder implements ApplicationRunner {

    /*
     * AtomicInteger is a thread-safe integer holder from java.util.concurrent.
     *
     * In this lesson, normal Boot startup should call this runner once, so a
     * plain int would be enough. AtomicInteger is used here as a tiny mutable
     * holder that can be incremented inside run(...) and read later by the test.
     */
    private final AtomicInteger invocationCount = new AtomicInteger();

    /*
     * AtomicReference is a thread-safe object holder from java.util.concurrent.
     *
     * ApplicationRunner.run(...) receives ApplicationArguments as a method
     * parameter, but the test wants to inspect those arguments after startup has
     * finished. This reference stores the last ApplicationArguments object Boot
     * passed to run(...), so helper methods below can read it later.
     *
     * A simpler field would also work for this lesson:
     *
     * private ApplicationArguments observedArguments;
     *
     * AtomicReference just makes the "store now, read later" behavior explicit
     * and safe even if startup callbacks are observed from another thread.
     */
    private final AtomicReference<ApplicationArguments> observedArguments = new AtomicReference<>();

    @Override
    public void run(ApplicationArguments args) {
        /*
         * ApplicationRunner is from Spring Boot.
         *
         * Boot calls ApplicationRunner.run(...) after the ApplicationContext has
         * been refreshed and beans are ready. The method receives the same
         * ApplicationArguments bean that other application code can inject.
         *
         * Do not confuse this callback with SpringApplication.run(...):
         *
         * - SpringApplication.run(...) starts the whole app and returns a
         *   ConfigurableApplicationContext.
         * - This Lesson03StartupRecorder.run(...) method is one callback inside
         *   that startup process and returns void.
         *
         * This lesson stores the arguments so the test can prove the runner saw
         * the parsed command-line data before SpringApplication.run(...) returned.
         */
        /*
         * ApplicationArguments is from Spring Boot. It is Boot's parsed view of
         * the raw String[] startup args:
         *
         * Raw args passed by the test:
         * - --lesson03.enabled=true
         * - startup-input.txt
         *
         * ApplicationArguments lets us ask:
         * - containsOption("lesson03.enabled") -> true
         * - getNonOptionArgs() -> ["startup-input.txt"]
         */
        observedArguments.set(args);
        invocationCount.incrementAndGet();
    }

    public int invocationCount() {
        return invocationCount.get();
    }

    public boolean sawOption(String optionName) {
        ApplicationArguments args = observedArguments.get();
        /*
         * observedArguments.get() reads the ApplicationArguments object that was
         * saved during run(...). If run(...) has not happened, the reference is
         * still empty and this method safely returns false.
         */
        return args != null && args.containsOption(optionName);
    }

    public List<String> nonOptionArgs() {
        ApplicationArguments args = observedArguments.get();
        /*
         * Non-option args are bare positional values. In this lesson,
         * "startup-input.txt" is a non-option arg because the test passes it
         * without a leading "--".
         *
         * Option args, not returned here:
         * - --lesson03.enabled=true
         * - --server.port=8081
         *
         * Non-option args, returned here:
         * - startup-input.txt
         * - orders.csv
         * - run-once
         */
        return args == null ? List.of() : args.getNonOptionArgs();
    }
}
