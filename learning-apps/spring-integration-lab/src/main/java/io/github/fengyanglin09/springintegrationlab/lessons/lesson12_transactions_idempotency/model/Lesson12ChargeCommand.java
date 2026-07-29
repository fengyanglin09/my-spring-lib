package io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.model;

import java.math.BigDecimal;

/**
 * Command payload asking the flow to charge an account.
 */
public record Lesson12ChargeCommand(
        String commandId,
        String accountId,
        BigDecimal amount
) {
}
