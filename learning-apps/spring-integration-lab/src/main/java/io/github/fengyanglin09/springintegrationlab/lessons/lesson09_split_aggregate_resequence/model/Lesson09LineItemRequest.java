package io.github.fengyanglin09.springintegrationlab.lessons.lesson09_split_aggregate_resequence.model;

import java.math.BigDecimal;

/**
 * One raw line item inside the batch order request.
 */
public record Lesson09LineItemRequest(
        int lineNumber,
        String sku,
        int quantity,
        BigDecimal unitPrice
) {
}
