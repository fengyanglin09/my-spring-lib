package io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.model;

import java.util.List;

/**
 * External protocol envelope sent back to the fake courier system.
 */
public record Lesson15CourierOutboundFrame(
        String frameId,
        String correlationFrameId,
        String remoteSystem,
        String contentType,
        String body,
        List<String> adapterTrail
) {
}
