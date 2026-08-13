package io.github.fengyanglin09.springbootlab.lessons.lesson05_auto_configuration.support;

/**
 * A pretend "library is present" class for Lesson 05.
 *
 * <p>This class is not a Spring bean. It is not annotated with {@code @Service}
 * or {@code @Component}. It exists only so {@code @ConditionalOnClass} can ask a
 * classpath question:</p>
 *
 * <pre>
 * Can the classloader find Lesson05GreetingLibraryMarker?
 * </pre>
 *
 * <p>Real Spring Boot auto-configurations usually check for classes from
 * external libraries, such as web, JSON, database, messaging, or cache types.
 * This lesson uses a tiny local marker class so we can simulate the same idea
 * without adding another dependency.</p>
 */
public final class Lesson05GreetingLibraryMarker {

    private Lesson05GreetingLibraryMarker() {
        /*
         * Utility/marker class: no instances needed. The lesson only cares
         * whether the class exists on the classpath.
         */
    }
}
