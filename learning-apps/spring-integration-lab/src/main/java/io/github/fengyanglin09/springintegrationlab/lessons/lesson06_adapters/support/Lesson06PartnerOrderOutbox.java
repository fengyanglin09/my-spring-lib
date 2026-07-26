package io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters.support;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters.model.Lesson06PartnerOrderReceipt;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters.model.Lesson06PartnerOrderRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Simulated external partner system that receives outbound order requests.
 */
// @Component tells Spring:
// "Create one Lesson06PartnerOrderOutbox object during startup."
//
// This class represents an external target system. In real code, this might be
// an HTTP client, file writer, JMS template, Kafka template, or SFTP client.
@Component
public class Lesson06PartnerOrderOutbox {

    // CopyOnWriteArrayList is a thread-safe List.
    //
    // "Thread-safe" means:
    // "Two threads can use this collection at the same time without corrupting
    // the collection's internal state."
    //
    // We use it here because two different threads may touch receipts:
    //
    // - the Spring Integration poller thread can call send(...) and add a receipt
    // - the Spock test thread can call receipts() and read the current receipts
    //
    // CopyOnWriteArrayList gets its name from how it handles changes:
    //
    // - reads are simple and do not need us to write synchronized blocks
    // - each write makes a fresh copy of the underlying array, then adds/removes
    //   the item on that new copy
    //
    // That makes it pleasant for this lesson because the list is tiny and writes
    // are rare. It would be a poor choice for a high-write collection, because
    // copying the array on every write would become expensive.
    //
    // In production, an outbound adapter usually sends to a real external system
    // instead of storing receipts in memory. This list exists only so the test
    // can prove that the outbound adapter received and "sent" the message.
    private final List<Lesson06PartnerOrderReceipt> receipts = new CopyOnWriteArrayList<>();

    public void send(Lesson06PartnerOrderRequest request) {
        // This method returns void on purpose.
        //
        // An outbound adapter is one-way: it sends data out to an external
        // target and does not expect a reply message to continue the flow.
        List<String> adapterTrail = new ArrayList<>(request.adapterTrail());
        adapterTrail.add("outbound-adapter:send-to-partner");

        receipts.add(new Lesson06PartnerOrderReceipt(
                request.partnerOrderId(),
                request.deliveryMode(),
                List.copyOf(adapterTrail)
        ));
    }

    public List<Lesson06PartnerOrderReceipt> receipts() {
        return List.copyOf(receipts);
    }

    public void clear() {
        receipts.clear();
    }
}
