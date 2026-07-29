package io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.model;

import java.math.BigDecimal;

/**
 * In-memory side effect recorded by the lesson 12 ledger.
 */
public record Lesson12LedgerEntry(
        String commandId,
        String accountId,
        BigDecimal amount
) {
}
