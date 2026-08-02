package io.github.fengyanglin09.springbootlab.lessons.lesson02_project_anatomy_maven_starters_classpath;

import io.github.fengyanglin09.springbootlab.lessons.lesson02_project_anatomy_maven_starters_classpath.model.Lesson02DependencySnapshot;
import io.github.fengyanglin09.springbootlab.lessons.lesson02_project_anatomy_maven_starters_classpath.support.Lesson02ProjectClasspathInspector;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Lesson 02's executable proof.
 *
 * <p>This is a plain JUnit test because Maven/classpath assembly is the subject.
 * A full Spring context would work, but it would hide the simpler point:
 * dependencies declared in the POM become classes the code can see.</p>
 */
class Lesson02ProjectAnatomyTest {

    private final Lesson02ProjectClasspathInspector classpathInspector =
            new Lesson02ProjectClasspathInspector();

    @Test
    void mavenPomAndSpringBootDependencyManagementCreateTheExpectedClasspath() {
        Lesson02DependencySnapshot snapshot = classpathInspector.inspect();

        // Boot's runtime reports the version selected by the parent build.
        assertThat(snapshot.hasSpringBootVersion()).isTrue();

        // The JVM exposes its launch classpath through java.class.path.
        assertThat(snapshot.hasClasspathRoots()).isTrue();

        // In this Maven test run, the classpath roots should point at tangible folders or jars.
        assertThat(snapshot.hasPhysicalClasspathRoot()).isTrue();

        // Maven compiles src/main/java into target/classes before packaging.
        assertThat(snapshot.targetMainClassesAreAvailable()).isTrue();

        // Maven compiles src/test/java into target/test-classes for test runs only.
        assertThat(snapshot.targetTestClassesAreAvailable()).isTrue();

        // spring-boot-starter supplies the core runtime pieces for a minimal Boot app.
        assertThat(snapshot.starterSuppliedCoreRuntime()).isTrue();

        // Lombok and Vavr are explicit helper choices for lesson code.
        assertThat(snapshot.helperLibrariesAreAvailable()).isTrue();

        // spring-boot-starter-test supplies the test style used by this lab.
        assertThat(snapshot.testStarterSuppliedTestTools()).isTrue();

        assertThat(snapshot.missingEntries()).isEmpty();

        // Each classpath check should point to the jar or compiled folder that supplied the class.
        assertThat(snapshot.entriesWithoutLocations()).isEmpty();
    }
}
