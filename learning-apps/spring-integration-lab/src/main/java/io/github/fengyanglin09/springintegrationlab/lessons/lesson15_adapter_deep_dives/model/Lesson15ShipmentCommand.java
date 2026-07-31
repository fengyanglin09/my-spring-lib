package io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.model;

import java.util.List;

/**
 * Internal payload after the inbound adapter boundary has been crossed.
 */
public record Lesson15ShipmentCommand(
        String shipmentId,
        String destination,
        String serviceLevel,
        List<String> adapterTrail
) {
}
