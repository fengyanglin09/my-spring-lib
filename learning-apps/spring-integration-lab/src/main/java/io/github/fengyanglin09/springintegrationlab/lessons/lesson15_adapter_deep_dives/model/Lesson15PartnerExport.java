package io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.model;

import java.util.List;

/**
 * Payload ready to be sent through the outbound adapter.
 */
public record Lesson15PartnerExport(
        String shipmentId,
        String destination,
        String serviceLevel,
        String acknowledgementBody,
        List<String> adapterTrail
) {
}
