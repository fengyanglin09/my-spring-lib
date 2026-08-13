package io.github.fengyanglin09.springbootlab.lessons.lesson05_auto_configuration.service;

/**
 * The bean type that the auto-configuration can provide.
 *
 * <p>Do not confuse this with the classpath marker used by
 * {@code @ConditionalOnClass}. {@code Lesson05GreetingLibraryMarker} answers
 * "is the pretend library available?" This interface answers "what type of bean
 * might the auto-configuration create?"</p>
 */
public interface Lesson05GreetingService {

    /**
     * Produces a greeting message.
     *
     * <p>The behavior is intentionally unimportant. The lesson cares about
     * whether this type is present in the context, which implementation won,
     * and why Boot made that choice.</p>
     */
    String greet(String name);
}
