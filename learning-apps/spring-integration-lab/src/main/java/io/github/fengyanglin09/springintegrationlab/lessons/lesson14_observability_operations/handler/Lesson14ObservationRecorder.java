package io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.handler;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.model.Lesson14ObservationRecord;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.model.Lesson14ShipmentUpdate;
import org.springframework.integration.history.MessageHistory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * In-memory observation sink used by lesson 14.
 */
// @Component tells Spring:
// "Create one Lesson14ObservationRecorder object during startup."
//
// @ManagedResource tells Spring's management infrastructure:
// "This bean has operations and attributes that may be exposed for management."
//
// The control bus uses that management metadata to decide which methods are
// safe to call from command messages.
@Component
@ManagedResource(description = "Lesson 14 observation recorder")
public class Lesson14ObservationRecorder {

    // CopyOnWriteArrayList is thread-safe for this lesson's read-mostly use.
    //
    // The flow may add records while a test or operation reads the current
    // records. This collection keeps those reads and writes safe without adding
    // synchronized blocks around the lesson code.
    private final List<Lesson14ObservationRecord> records = new CopyOnWriteArrayList<>();

    // AtomicBoolean is a thread-safe true/false holder.
    //
    // The control bus can change this flag while messages are flowing.
    // AtomicBoolean gives us safe cross-thread visibility:
    //
    // - when one thread calls set(false)
    // - another thread calling get() can reliably see that updated value
    //
    // In this lesson:
    //
    // - true  = record wire-tapped messages
    // - false = ignore wire-tapped messages
    private final AtomicBoolean observationEnabled = new AtomicBoolean(true);

    public void record(Message<?> message) {
        // This method receives the message copy from the wire tap.
        //
        // If observation is stopped, we intentionally ignore the copy. The main
        // shipment flow is not stopped; only this operational recording side
        // path is stopped.
        if (!observationEnabled.get()) {
            return;
        }

        if (message.getPayload() instanceof Lesson14ShipmentUpdate update) {
            records.add(new Lesson14ObservationRecord(
                    update.shipmentId(),
                    update.lane(),
                    messageHistory(message),
                    update.lessonTrail()
            ));
        }
    }

    // @ManagedOperation tells the control bus:
    // "This method is allowed to be called by an operation command message."
    //
    // The lesson sends the command string:
    //
    // lesson14ObservationRecorder.stopObservation
    @ManagedOperation(description = "Stop recording wire-tapped shipment messages")
    public String stopObservation() {
        observationEnabled.set(false);
        return "OBSERVATION_STOPPED";
    }

    // This is the matching command used to resume observation:
    //
    // lesson14ObservationRecorder.startObservation
    @ManagedOperation(description = "Start recording wire-tapped shipment messages")
    public String startObservation() {
        observationEnabled.set(true);
        return "OBSERVATION_STARTED";
    }

    // @ManagedAttribute tells management tools:
    // "This value can be read as operational state."
    @ManagedAttribute(description = "Whether lesson 14 observation recording is enabled")
    public boolean isObservationEnabled() {
        return observationEnabled.get();
    }

    public List<Lesson14ObservationRecord> records() {
        return List.copyOf(records);
    }

    public void clear() {
        records.clear();
    }

    private List<String> messageHistory(Message<?> message) {
        // MessageHistory.read(message) reads the Spring Integration
        // message-history header, if message history is enabled and the message
        // has already passed through tracked components.
        MessageHistory history = MessageHistory.read(message);
        if (history == null || history.isEmpty()) {
            return List.of("message-history:no-tracked-components-yet");
        }

        List<String> entries = new ArrayList<>();
        for (Properties entry : history) {
            String name = entry.getProperty(MessageHistory.NAME_PROPERTY, "unknown-name");
            String type = entry.getProperty(MessageHistory.TYPE_PROPERTY, "unknown-type");
            entries.add(name + "(" + type + ")");
        }
        return List.copyOf(entries);
    }
}
