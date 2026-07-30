package io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.model;

import java.util.List;

/**
 * Reply returned by the normal shipment flow.
 */
public record Lesson14ShipmentResult(
        String shipmentId,
        String status,
        String lane,
        List<String> lessonTrail
) {
}
