package io.github.fengyanglin09.springbootlab.lessons.lesson04_beans_dependency_injection.model;

/**
 * The result produced by the lesson service.
 *
 * <p>This record is intentionally simple. The important Spring concept is not
 * the receipt itself, but how the receipt is produced by a service whose
 * collaborators were injected by Spring.</p>
 */
public record Lesson04OrderReceipt(
        String orderReference,
        String customerId,
        int itemCount,
        String message
) {
}
