package io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.model;

/**
 * Raw shipment event used as the input payload for lesson 14.
 */
public record Lesson14ShipmentEvent(
        String shipmentId,
        String destination,
        boolean priority
) {
}
