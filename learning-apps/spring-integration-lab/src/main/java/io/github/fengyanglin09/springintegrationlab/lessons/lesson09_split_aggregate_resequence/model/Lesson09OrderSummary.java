package io.github.fengyanglin09.springintegrationlab.lessons.lesson09_split_aggregate_resequence.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Reply returned after all split line-item messages have been aggregated.
 */
public record Lesson09OrderSummary(
        String orderId,
        String messageGroupId,
        int lineCount,
        BigDecimal orderTotal,
        List<Integer> lineNumbersInOrder,
        List<String> skusInOrder,
        List<Integer> sequenceNumbersInOrder,
        List<String> lessonTrail
) {
}
