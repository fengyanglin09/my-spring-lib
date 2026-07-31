package io.github.fengyanglin09.springbootlab.lessons.lesson01_why_spring_boot;

import java.util.List;

/**
 * A tiny read model for the lesson test.
 *
 * <p>The record keeps raw observations separate from the assertions about those
 * observations. That makes the test read like the lesson goal: did Boot start a
 * context, and did component scanning find our beans?</p>
 */
public record Lesson01BootSnapshot(
        String applicationContextId,
        int beanDefinitionCount,
        List<String> activeProfiles,
        boolean applicationClassRegistered,
        boolean lessonBeanRegistered
) {

    /**
     * Proves Boot created a real context instead of the test calling plain Java objects.
     */
    public boolean bootStartedApplicationContext() {
        return applicationContextId != null
                && !applicationContextId.isBlank()
                && beanDefinitionCount > 0;
    }

    /**
     * Proves the root application class and lesson bean were registered by component scanning.
     */
    public boolean componentScanFoundLessonBean() {
        return applicationClassRegistered && lessonBeanRegistered;
    }
}
