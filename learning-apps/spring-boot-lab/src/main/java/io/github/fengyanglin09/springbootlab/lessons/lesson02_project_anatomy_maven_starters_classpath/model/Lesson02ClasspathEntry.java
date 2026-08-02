package io.github.fengyanglin09.springbootlab.lessons.lesson02_project_anatomy_maven_starters_classpath.model;

/**
 * One classpath expectation for the lesson.
 *
 * <p>Lesson 02 is about how Maven dependencies become available to application
 * code. Each entry names one class we expect to find and the lesson reason we
 * care about finding it. The {@code foundAt} value makes the classpath visible
 * by showing the jar or compiled output location that supplied the class.</p>
 */
public record Lesson02ClasspathEntry(
        String label,
        String className,
        String classpathResource,
        String lessonReason,
        boolean present,
        String foundAt
) {

    public boolean hasLocation() {
        return foundAt != null && !foundAt.isBlank();
    }
}
