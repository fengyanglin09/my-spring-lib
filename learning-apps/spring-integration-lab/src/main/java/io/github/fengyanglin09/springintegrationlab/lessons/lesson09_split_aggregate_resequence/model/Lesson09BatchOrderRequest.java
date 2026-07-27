package io.github.fengyanglin09.springintegrationlab.lessons.lesson09_split_aggregate_resequence.model;

import java.util.List;

/**
 * One incoming batch order message.
 */
public record Lesson09BatchOrderRequest(
        String orderId,
        List<Lesson09LineItemRequest> lineItems
) {
}
