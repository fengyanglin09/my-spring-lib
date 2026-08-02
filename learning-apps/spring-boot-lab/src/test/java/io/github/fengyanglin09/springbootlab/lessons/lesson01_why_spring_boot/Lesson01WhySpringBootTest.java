package io.github.fengyanglin09.springbootlab.lessons.lesson01_why_spring_boot;

import io.github.fengyanglin09.springbootlab.lessons.lesson01_why_spring_boot.model.Lesson01BootSnapshot;
import io.github.fengyanglin09.springbootlab.lessons.lesson01_why_spring_boot.support.Lesson01BootInspector;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Lesson 01's executable proof.
 *
 * <p>{@link SpringBootTest} starts the real Spring Boot application context, so
 * this test is intentionally heavier than a unit test. That tradeoff is useful
 * here because the lesson is about startup and context assembly.</p>
 */
@SpringBootTest
class Lesson01WhySpringBootTest {

    // If component scanning works, Spring can inject this lesson bean into the test.
    @Autowired
    private Lesson01BootInspector bootInspector;

    @Test
    void springBootStartsTheApplicationContextAndFindsLessonBeans() {
        Lesson01BootSnapshot snapshot = bootInspector.inspect();

        // Boot created an ApplicationContext with managed bean definitions.
        assertThat(snapshot.bootStartedApplicationContext()).isTrue();

        // Component scanning found both the root app class and this lesson's inspector bean.
        assertThat(snapshot.componentScanFoundLessonBean()).isTrue();

        // Lesson 01 does not introduce profiles yet, so the active profile list should be empty.
        assertThat(snapshot.activeProfiles()).isEmpty();
    }
}
