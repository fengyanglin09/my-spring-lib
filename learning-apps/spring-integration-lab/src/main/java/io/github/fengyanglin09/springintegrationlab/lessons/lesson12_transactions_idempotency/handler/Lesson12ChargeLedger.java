package io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.handler;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.model.Lesson12ChargeCommand;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.model.Lesson12ChargeResult;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.model.Lesson12LedgerEntry;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * In-memory side-effect target used by lesson 12.
 */
// @Component tells Spring:
// "Create one Lesson12ChargeLedger object during startup."
@Component
public class Lesson12ChargeLedger {

    // This list is the side effect we are protecting.
    //
    // Side effect means:
    // "A change outside the current message object."
    //
    // Here, the side effect is adding a ledger entry. In a real system, this
    // could be charging a card, writing a database row, or calling another
    // service.
    private final List<Lesson12LedgerEntry> entries = new CopyOnWriteArrayList<>();

    public Lesson12ChargeResult applyCharge(Lesson12ChargeCommand command) {
        // This method should run only for the first message with a commandId.
        //
        // The idempotent receiver advice is attached before this handler is
        // called. If the commandId was already accepted, the duplicate message
        // should be sent to the duplicate flow and this method should not run.
        entries.add(new Lesson12LedgerEntry(
                command.commandId(),
                command.accountId(),
                command.amount()
        ));

        return new Lesson12ChargeResult(
                command.commandId(),
                command.accountId(),
                command.amount(),
                true,
                "CHARGED",
                entries.size(),
                List.of(
                        "idempotent-receiver:accepted-first-command-id",
                        "handler:applied-ledger-side-effect",
                        "gateway:charged-result"
                )
        );
    }

    public Lesson12ChargeResult skipDuplicate(Lesson12ChargeCommand command) {
        // This method runs for duplicate commands.
        //
        // Notice that it does not add a ledger entry. That is the whole point of
        // the idempotency guard: a retried or duplicated message can return a
        // useful result without applying the side effect twice.
        return new Lesson12ChargeResult(
                command.commandId(),
                command.accountId(),
                command.amount(),
                false,
                "DUPLICATE_SKIPPED",
                entries.size(),
                List.of(
                        "idempotent-receiver:rejected-duplicate-command-id",
                        "discard-channel:sent-to-duplicate-flow",
                        "handler:skipped-ledger-side-effect",
                        "gateway:duplicate-skipped-result"
                )
        );
    }

    public List<Lesson12LedgerEntry> entries() {
        return List.copyOf(entries);
    }

    public void clear() {
        entries.clear();
    }
}
