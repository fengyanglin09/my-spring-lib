package io.github.fengyanglin09.springintegrationlab.lessons.lesson03_message_channels.model;

/**
 * Reply payload for the DirectChannel example.
 *
 * <p>The thread name is included so the test can show that a DirectChannel
 * invokes its handler in the sender's thread.</p>
 */
public record Lesson03DirectDeliveryReport(
        String messageId,
        String handledBy,
        String handledThreadName
) {
}
