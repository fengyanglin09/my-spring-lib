package io.github.fengyanglin09.springintegrationlab.lessons.lesson09_split_aggregate_resequence.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Payload after the batch has been split into individual line-item messages.
 */
public record Lesson09LineItemWork(
        String orderId,
        int lineNumber,
        String sku,
        int quantity,
        BigDecimal unitPrice,
        List<String> lessonTrail
) {
}
