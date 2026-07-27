package io.github.fengyanglin09.springintegrationlab.lessons.lesson09_split_aggregate_resequence.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Payload after one line item has been priced.
 */
public record Lesson09PricedLineItem(
        String orderId,
        int lineNumber,
        String sku,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal,
        List<String> lessonTrail
) {
}
