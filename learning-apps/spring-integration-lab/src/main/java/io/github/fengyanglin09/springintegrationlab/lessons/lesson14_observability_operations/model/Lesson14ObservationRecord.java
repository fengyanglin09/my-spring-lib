package io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.model;

import java.util.List;

/**
 * A small operational record captured from the wire-tapped message copy.
 */
public record Lesson14ObservationRecord(
        String shipmentId,
        String lane,
        List<String> messageHistory,
        List<String> lessonTrail
) {
}
