package io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Reply returned after a charge command is accepted or skipped as duplicate.
 */
public record Lesson12ChargeResult(
        String commandId,
        String accountId,
        BigDecimal amount,
        boolean charged,
        String status,
        int ledgerEntryCount,
        List<String> lessonTrail
) {
}
