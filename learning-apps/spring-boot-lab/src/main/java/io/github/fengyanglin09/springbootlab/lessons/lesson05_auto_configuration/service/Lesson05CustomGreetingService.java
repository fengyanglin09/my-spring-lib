package io.github.fengyanglin09.springbootlab.lessons.lesson05_auto_configuration.service;

/**
 * A user-defined implementation used to show auto-configuration backing off.
 *
 * <p>This class is deliberately not annotated with {@code @Component}. The
 * lesson test registers it as a user bean with {@code ApplicationContextRunner}
 * so the exact experiment is visible in the test.</p>
 */
public class Lesson05CustomGreetingService implements Lesson05GreetingService {

    private final String owner;

    public Lesson05CustomGreetingService(String owner) {
        this.owner = owner;
    }

    @Override
    public String greet(String name) {
        return "custom greeting from %s to %s".formatted(owner, name);
    }
}
