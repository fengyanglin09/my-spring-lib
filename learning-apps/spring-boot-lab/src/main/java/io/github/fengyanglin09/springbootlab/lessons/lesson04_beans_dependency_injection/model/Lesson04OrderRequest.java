package io.github.fengyanglin09.springbootlab.lessons.lesson04_beans_dependency_injection.model;

/**
 * Input for the tiny order workflow used by Lesson 04.
 *
 * <p>This is a Java record, not a Spring bean. Spring does not need to manage
 * every object in an application. Short-lived data objects like requests,
 * commands, and results are often created normally with {@code new}. Long-lived
 * collaborators such as services, formatters, repositories, and clients are
 * better candidates for Spring beans.</p>
 */
public record Lesson04OrderRequest(String customerId, int itemCount) {

    public Lesson04OrderRequest {
        /*
         * These guards keep the lesson example honest without turning Lesson 04
         * into a validation lesson. We will cover request validation later near
         * the REST/API lessons.
         */
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("customerId is required");
        }

        if (itemCount < 1) {
            throw new IllegalArgumentException("itemCount must be positive");
        }
    }
}
