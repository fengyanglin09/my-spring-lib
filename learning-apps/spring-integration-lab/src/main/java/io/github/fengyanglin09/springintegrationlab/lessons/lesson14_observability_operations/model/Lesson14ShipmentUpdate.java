package io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.model;

import java.util.List;

/**
 * Normalized shipment payload after the lesson 14 transform step.
 */
public record Lesson14ShipmentUpdate(
        String shipmentId,
        String destination,
        String lane,
        List<String> lessonTrail
) {
}
