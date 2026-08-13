package io.github.fengyanglin09.springbootlab.lessons.lesson05_auto_configuration.service;

/**
 * The default implementation created by the lesson auto-configuration.
 *
 * <p>This class is plain Java. It has no Spring annotation. If it becomes a
 * Spring bean, that happens because an auto-configuration class creates it with
 * an {@code @Bean} method.</p>
 */
public class Lesson05DefaultGreetingService implements Lesson05GreetingService {

    private final String source;

    public Lesson05DefaultGreetingService(String source) {
        this.source = source;
    }

    @Override
    public String greet(String name) {
        return "auto-configured greeting from %s to %s".formatted(source, name);
    }
}
