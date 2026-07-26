package io.github.fengyanglin09.springintegrationlab.lessons.lesson03_message_channels.handler;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson03_message_channels.model.Lesson03BroadcastReceipt;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson03_message_channels.model.Lesson03DeliveryRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Records which broadcast subscribers received a message.
 *
 * <p>This stateful recorder is here only to make the lesson test easy to read.
 * Production event subscribers would usually perform real work instead of
 * storing receipts in memory.</p>
 *
 * <p>In this lesson, both publish-subscribe handlers write to the same
 * {@code receipts} list. That shared list lets the test prove that one broadcast
 * message was delivered to more than one subscriber.</p>
 */
@Component
public class Lesson03BroadcastRecorder {

    // This list is shared by both broadcast subscriber methods below.
    //
    // ArrayList is not thread-safe. Lesson 03 uses a synchronous channel, so both
    // subscribers currently run in the caller's thread. We still use synchronized
    // methods here because later channel types can introduce other threads, and
    // this makes the shared test recorder safe if that changes.
    private final List<Lesson03BroadcastReceipt> receipts = new ArrayList<>();

    // synchronized means:
    // "A thread must hold this object's lock before it can run this method."
    //
    // Important: synchronized locks the object, not just one method.
    // recordAudit(...), recordNotification(...), receiptsFor(...), and clear()
    // all use the same lock because they are synchronized instance methods on
    // the same Lesson03BroadcastRecorder object.
    //
    // If two subscribers tried to record at the same time, one would finish
    // changing the receipts list before the other starts changing it.
    public synchronized void recordAudit(Lesson03DeliveryRequest request) {
        record(request, "audit-subscriber");
    }

    // This is the second broadcast subscriber.
    // It writes to the same receipts list as recordAudit(...), so it is also synchronized.
    public synchronized void recordNotification(Lesson03DeliveryRequest request) {
        record(request, "notification-subscriber");
    }

    // The test calls this method after broadcasting a message.
    // synchronized makes sure the test reads a stable view of the receipts list.
    public synchronized List<Lesson03BroadcastReceipt> receiptsFor(String messageId) {
        return receipts.stream()
                .filter(receipt -> receipt.messageId().equals(messageId))
                .toList();
    }

    // The test clears old receipts before each broadcast scenario.
    // synchronized prevents clearing the list while another synchronized method
    // is reading from or writing to it.
    public synchronized void clear() {
        receipts.clear();
    }

    // This helper assumes the caller already entered through a synchronized method.
    // That keeps the locking rule in one place: public methods protect the shared list.
    private void record(Lesson03DeliveryRequest request, String subscriberName) {
        receipts.add(new Lesson03BroadcastReceipt(
                request.messageId(),
                subscriberName,
                Thread.currentThread().getName()
        ));
    }
}
