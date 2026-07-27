package io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Reply returned by the lesson 07 gateway.
 */
public record Lesson07ShapeReport(
        String orderId,
        String sourceSystem,
        String customerTier,
        String region,
        BigDecimal orderAmount,
        String valueBand,
        String lessonName,
        List<String> shapeTrail
) {
}
