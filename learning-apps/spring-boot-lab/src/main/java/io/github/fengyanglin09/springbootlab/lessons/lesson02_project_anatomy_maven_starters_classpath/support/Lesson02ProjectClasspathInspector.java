package io.github.fengyanglin09.springbootlab.lessons.lesson02_project_anatomy_maven_starters_classpath.support;

import io.github.fengyanglin09.springbootlab.lessons.lesson02_project_anatomy_maven_starters_classpath.model.Lesson02ClasspathEntry;
import io.github.fengyanglin09.springbootlab.lessons.lesson02_project_anatomy_maven_starters_classpath.model.Lesson02DependencySnapshot;
import org.springframework.boot.SpringBootVersion;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Inspects the classpath Maven prepared for this module.
 *
 * <p>This is plain Java on purpose. Lesson 02 is not testing the Spring
 * application context; it is showing the chain before Boot does application
 * work: Maven prepares the classpath, Java's ClassLoader searches it, and
 * Spring Boot can use the classes Java is able to load.</p>
 *
 * <p>The lesson content is deliberately narrow: Maven module shape, Boot
 * dependency management, starters, direct helper dependencies, test
 * dependencies, and the classpath Java searches when code runs.</p>
 */
public class Lesson02ProjectClasspathInspector {

    /*
     * This checklist translates build concepts into concrete classes.
     *
     * The lesson is not that Spring directly reads the POM. Spring Boot sees
     * the world after Maven and Java have done their jobs: Maven resolved the
     * dependencies, and Java can now load classes from target/ and dependency
     * jars.
     *
     * - This lab's main application class should be present from
     *   target/classes, which is where Maven compiles src/main/java.
     * - This lesson's test class should be present from target/test-classes,
     *   which is where Maven compiles src/test/java.
     * - Boot launcher and Spring context classes should be present because of
     *   spring-boot-starter.
     * - Logging classes should be present because the base starter brings the
     *   default logging stack.
     * - Lombok and Vavr should be present because this lab declared them as
     *   direct helper dependencies.
     * - JUnit and AssertJ should be present because spring-boot-starter-test is
     *   on the test classpath.
     */
    private static final List<ExpectedClass> EXPECTED_CLASSES = List.of(
            new ExpectedClass(
                    "SpringBootLabApplication",
                    "io.github.fengyanglin09.springbootlab.SpringBootLabApplication",
                    "target/classes contains compiled main application classes"
            ),
            new ExpectedClass(
                    "Lesson02ProjectAnatomyTest",
                    "io.github.fengyanglin09.springbootlab.lessons.lesson02_project_anatomy_maven_starters_classpath.Lesson02ProjectAnatomyTest",
                    "target/test-classes contains compiled test classes during test runs"
            ),
            new ExpectedClass(
                    "SpringApplication",
                    "org.springframework.boot.SpringApplication",
                    "spring-boot-starter brings the Boot launcher"
            ),
            new ExpectedClass(
                    "ApplicationContext",
                    "org.springframework.context.ApplicationContext",
                    "spring-boot-starter brings Spring Framework context APIs"
            ),
            new ExpectedClass(
                    "SLF4J LoggerFactory",
                    "org.slf4j.LoggerFactory",
                    "spring-boot-starter includes the logging facade"
            ),
            new ExpectedClass(
                    "Logback LoggerContext",
                    "ch.qos.logback.classic.LoggerContext",
                    "spring-boot-starter includes a default logging runtime"
            ),
            new ExpectedClass(
                    "Lombok RequiredArgsConstructor",
                    "lombok.RequiredArgsConstructor",
                    "lombok is a direct helper dependency for reducing ceremony"
            ),
            new ExpectedClass(
                    "Vavr Try",
                    "io.vavr.control.Try",
                    "vavr is a direct helper dependency for explicit failure values"
            ),
            new ExpectedClass(
                    "JUnit Test",
                    "org.junit.jupiter.api.Test",
                    "spring-boot-starter-test brings JUnit Jupiter"
            ),
            new ExpectedClass(
                    "AssertJ Assertions",
                    "org.assertj.core.api.Assertions",
                    "spring-boot-starter-test brings AssertJ fluent assertions"
            )
    );

    /**
     * Builds one snapshot of the module's resolved classpath.
     *
     * <p>The important lesson idea is that this method does not create any of
     * these libraries. Maven has already resolved them before the test starts.
     * This method only asks, "Can the running code see the classes that the POM
     * said should be available, and where did Java find them?"</p>
     *
     * <p>Classpath means the search list Java uses for classes and resources.
     * In this test, Maven builds that list from compiled project classes,
     * dependency jars, test classes, and test dependency jars.</p>
     */
    public Lesson02DependencySnapshot inspect() {
        /*
         * A ClassLoader is the object Java uses to find classes at runtime.
         * Spring Boot depends on this: component scanning and
         * auto-configuration only work with classes and resources the active
         * ClassLoader can load.
         *
         * We use the current thread's context ClassLoader because test runners,
         * application servers, and frameworks often set it to the loader that
         * represents the active application/test classpath. In this lesson, that
         * means "the classpath Maven built for spring-boot-lab's test run."
         */
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        /*
         * java.class.path is the JVM's launch-time classpath string. Maven
         * Surefire may point this at a small launcher jar instead of listing
         * every dependency jar directly, but it is still the first concrete
         * place to look when someone asks, "Where is the classpath?"
         */
        List<String> classpathRoots = readClasspathRoots();

        /*
         * EXPECTED_CLASSES is our small checklist of "classes we should be able
         * to see if the POM did what we think it did."
         *
         * The stream turns each expectation into a Lesson02ClasspathEntry. Each
         * entry keeps both the technical detail (class name, present or missing)
         * and the learning explanation (why this class matters for the lesson).
         */
        List<Lesson02ClasspathEntry> entries = EXPECTED_CLASSES.stream()
                .map(expectedClass -> expectedClass.toClasspathEntry(classLoader))
                .toList();

        /*
         * SpringBootVersion reads version metadata from Spring Boot's own jar.
         * If this returns a value, it proves the Boot runtime is present and lets
         * the snapshot show which Boot version Maven selected.
         */
        return new Lesson02DependencySnapshot(SpringBootVersion.getVersion(), classpathRoots, entries);
    }

    private List<String> readClasspathRoots() {
        /*
         * java.class.path is the classpath string passed to the current JVM.
         *
         * In this lesson test, Maven Surefire starts a JVM for tests and gives
         * that JVM a classpath. The value may contain real folders such as
         * target/classes and target/test-classes, jar files from ~/.m2, or a
         * Surefire launcher jar that points to the real test classpath.
         *
         * This is not "where Spring Boot searches every time" in a universal
         * sense. It is where this particular Java process was told to search.
         * A packaged Spring Boot executable jar uses Boot's launcher to build a
         * class loader over BOOT-INF/classes and BOOT-INF/lib instead.
         */
        String rawClasspath = System.getProperty("java.class.path", "");

        /*
         * The classpath is one long string with entries separated by the
         * operating system's path separator:
         *
         * - macOS/Linux use ":"
         * - Windows uses ";"
         *
         * File.pathSeparator keeps the lesson portable. Splitting turns the raw
         * string into individual roots we can inspect and explain.
         */
        return Arrays.stream(rawClasspath.split(File.pathSeparator))
                /*
                 * Empty entries are not useful for the lesson output. This can
                 * happen if the classpath string has leading, trailing, or
                 * repeated separators.
                 */
                .filter(entry -> !entry.isBlank())
                .toList();
    }

    /**
     * Describes a class we expect Maven to make visible.
     *
     * <p>This record separates the lesson checklist from the probing logic. The
     * label is human-friendly, {@code className} is the fully qualified Java
     * class to look for, and {@code lessonReason} explains why finding that
     * class matters for this lesson.</p>
     */
    private record ExpectedClass(String label, String className, String lessonReason) {

        private Lesson02ClasspathEntry toClasspathEntry(ClassLoader classLoader) {
            String classpathResource = className.replace('.', '/') + ".class";
            URL resourceUrl = classLoader.getResource(classpathResource);

            return new Lesson02ClasspathEntry(
                    label,
                    className,
                    classpathResource,
                    lessonReason,
                    /*
                     * ClassUtils.isPresent checks whether the class can be found
                     * by this ClassLoader. It is safer for a lesson probe than
                     * directly using Class.forName because it gives us a boolean
                     * answer instead of making the lesson code fail immediately
                     * when a dependency is missing.
                     */
                    ClassUtils.isPresent(className, classLoader),
                    /*
                     * The resource URL is the concrete "where." For a dependency
                     * class it usually points into a jar under Maven's local
                     * repository. For project code it usually points into
                     * target/classes or target/test-classes.
                     */
                    resourceUrl == null ? null : resourceUrl.toString()
            );
        }
    }
}
