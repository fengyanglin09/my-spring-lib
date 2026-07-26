package io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters.support;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters.model.Lesson06ExternalOrderRecord;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Simulated external system that produces order records.
 */
// @Component tells Spring:
// "Create one Lesson06ExternalOrderInbox object during startup."
//
// This class represents the outside world. It does not know about Spring
// Integration Message objects, channels, or flows. It only stores plain Java
// records until the inbound adapter asks for the next one.
@Component
public class Lesson06ExternalOrderInbox {

    // ConcurrentLinkedQueue is thread-safe.
    //
    // The test thread adds records with submit(...). The inbound adapter's
    // poller thread removes records with poll(). Because those can be different
    // threads, a regular ArrayList would be the wrong tool here.
    private final Queue<Lesson06ExternalOrderRecord> records = new ConcurrentLinkedQueue<>();

    public void submit(Lesson06ExternalOrderRecord record) {
        records.add(record);
    }

    public Lesson06ExternalOrderRecord poll() {
        // The inbound adapter calls this method repeatedly.
        //
        // Returning a record means:
        // "Create a Spring Integration message whose payload is this record."
        //
        // Returning null means:
        // "There is no external data right now, so do not create a message for
        // this poll."
        return records.poll();
    }

    public void clear() {
        records.clear();
    }
}
