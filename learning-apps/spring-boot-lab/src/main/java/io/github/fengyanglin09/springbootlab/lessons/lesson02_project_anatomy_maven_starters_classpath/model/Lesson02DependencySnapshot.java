package io.github.fengyanglin09.springbootlab.lessons.lesson02_project_anatomy_maven_starters_classpath.model;

import java.util.List;

/**
 * A small read model for the lesson test.
 *
 * <p>The snapshot turns low-level classpath checks into lesson language:
 * starter-provided runtime pieces, explicitly declared helper libraries, and
 * test tools. The classpath entries are the evidence; the boolean methods below
 * group that evidence into the ideas Lesson 02 wants to teach.</p>
 */
public record Lesson02DependencySnapshot(
        String springBootVersion,
        List<String> classpathRoots,
        List<Lesson02ClasspathEntry> classpathEntries
) {

    /**
     * The Boot version comes from the Boot runtime on the classpath, which is
     * selected by the parent build's dependency management.
     */
    public boolean hasSpringBootVersion() {
        return springBootVersion != null && !springBootVersion.isBlank();
    }

    /**
     * The JVM exposes the launch classpath through the java.class.path system
     * property. Build tools may shorten it with a launcher jar, so individual
     * class resource locations are usually the clearest proof.
     */
    public boolean hasClasspathRoots() {
        return !classpathRoots.isEmpty();
    }

    /**
     * In a Maven test run, at least one classpath root should look like a
     * physical filesystem entry. Build tools may hide the full classpath behind
     * a launcher jar, so this check intentionally stays broad.
     */
    public boolean hasPhysicalClasspathRoot() {
        return classpathRoots.stream()
                .anyMatch(root -> root.endsWith(".jar")
                        || root.contains("target")
                        || root.contains(".m2"));
    }

    /**
     * During Maven tests, compiled production code is loaded from target/classes
     * before it is packaged into a jar.
     */
    public boolean targetMainClassesAreAvailable() {
        return hasPresent("SpringBootLabApplication")
                && foundAtContains("SpringBootLabApplication", "target/classes");
    }

    /**
     * During Maven tests, compiled test code is loaded from target/test-classes.
     * These classes help run tests but are not packaged into the production jar.
     */
    public boolean targetTestClassesAreAvailable() {
        return hasPresent("Lesson02ProjectAnatomyTest")
                && foundAtContains("Lesson02ProjectAnatomyTest", "target/test-classes");
    }

    /**
     * The base starter should provide the Boot launcher, Spring context APIs,
     * and logging facade/runtime pieces.
     */
    public boolean starterSuppliedCoreRuntime() {
        return hasPresent("SpringApplication")
                && hasPresent("ApplicationContext")
                && hasPresent("SLF4J LoggerFactory")
                && hasPresent("Logback LoggerContext");
    }

    /**
     * Lombok and Vavr are direct helper dependencies in this lab. They support
     * lesson code, but they are not the focus of the Spring Boot category.
     */
    public boolean helperLibrariesAreAvailable() {
        return hasPresent("Lombok RequiredArgsConstructor")
                && hasPresent("Vavr Try");
    }

    /**
     * The test starter should provide the default JUnit and AssertJ tools used
     * by this Spring Boot lab.
     */
    public boolean testStarterSuppliedTestTools() {
        return hasPresent("JUnit Test")
                && hasPresent("AssertJ Assertions");
    }

    public List<Lesson02ClasspathEntry> missingEntries() {
        return classpathEntries.stream()
                .filter(entry -> !entry.present())
                .toList();
    }

    public List<Lesson02ClasspathEntry> entriesWithoutLocations() {
        return classpathEntries.stream()
                .filter(entry -> entry.present() && !entry.hasLocation())
                .toList();
    }

    private boolean hasPresent(String label) {
        return classpathEntries.stream()
                .anyMatch(entry -> entry.label().equals(label) && entry.present());
    }

    private boolean foundAtContains(String label, String expectedPathPart) {
        return classpathEntries.stream()
                .filter(entry -> entry.label().equals(label))
                .anyMatch(entry -> entry.foundAt() != null
                        && entry.foundAt().contains(expectedPathPart));
    }
}
