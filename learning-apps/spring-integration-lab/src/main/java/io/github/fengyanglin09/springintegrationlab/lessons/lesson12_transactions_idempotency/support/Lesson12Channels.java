package io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.support;

/**
 * Channel bean names used by lesson 12.
 */
public final class Lesson12Channels {

    public static final String CHARGE_COMMANDS = "lesson12ChargeCommands";
    public static final String DUPLICATE_CHARGE_COMMANDS = "lesson12DuplicateChargeCommands";

    private Lesson12Channels() {
    }
}
