package io.github.fengyanglin09.springintegrationlab.lessons.lesson03_message_channels.model;

/**
 * The payload sent through lesson 03 channels.
 */
public record Lesson03DeliveryRequest(
        String messageId,
        String body
) {
}
