package io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.model;

/**
 * External protocol envelope received from the fake courier system.
 */
public record Lesson15CourierInboundFrame(
        String frameId,
        String remoteSystem,
        String contentType,
        String body
) {
}
