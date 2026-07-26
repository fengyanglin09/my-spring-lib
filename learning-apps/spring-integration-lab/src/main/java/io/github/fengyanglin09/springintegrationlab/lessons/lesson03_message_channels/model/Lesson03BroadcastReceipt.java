package io.github.fengyanglin09.springintegrationlab.lessons.lesson03_message_channels.model;

/**
 * Records one subscriber that received a broadcast message.
 */
public record Lesson03BroadcastReceipt(
        String messageId,
        String subscriberName,
        String handledThreadName
) {
}
