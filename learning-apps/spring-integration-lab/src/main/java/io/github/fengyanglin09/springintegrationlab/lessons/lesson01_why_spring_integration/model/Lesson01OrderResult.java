package io.github.fengyanglin09.springintegrationlab.lessons.lesson01_why_spring_integration.model;

/**
 * The reply payload returned after the message is handled by the flow.
 */
public record Lesson01OrderResult(
        String orderId,
        String route,
        String message
) {
}
