package io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.support;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.model.Lesson15CourierInboundFrame;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.model.Lesson15CourierOutboundFrame;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Small in-memory stand-in for an external protocol system.
 */
// @Component tells Spring:
// "Create one Lesson15CourierProtocolSandbox object during startup."
//
// This is not meant to be a real protocol implementation. It gives the lesson
// something external-looking to adapt without adding SFTP, JMS, Kafka, or HTTP
// infrastructure.
@Component
public class Lesson15CourierProtocolSandbox {

    // The inbound side behaves like an external system waiting to be polled.
    //
    // We use a Queue for inbound frames because inbound data is waiting work.
    //
    // The flow should process each inbound frame once:
    //
    // - submitInboundFrame(...) adds a frame to the back of the line
    // - pollInboundFrame() takes the next frame from the front of the line
    // - after pollInboundFrame() returns that frame, the frame is removed from
    //   the queue
    //
    // That matches an inbox/check-out-line model: once the adapter receives a
    // frame, that same frame should not be received again.
    //
    // ConcurrentLinkedQueue is thread-safe, so one thread can add a frame while
    // another thread polls a frame without corrupting the queue's internal
    // state.
    //
    // That matters here because the test thread calls submitInboundFrame(...),
    // while the Spring Integration poller runs on a scheduler thread and calls
    // pollInboundFrame().
    private final Queue<Lesson15CourierInboundFrame> inboundFrames = new ConcurrentLinkedQueue<>();

    // The outbound side records what the outbound adapter sent.
    //
    // We use a List for outbound frames because outbound data is history for
    // this lesson.
    //
    // The test does not want to consume and remove outbound frames. It wants to
    // inspect everything the outbound adapter sent:
    //
    // - first sent frame
    // - second sent frame
    // - all sent frames in order
    //
    // That matches a sent-mail/audit-log model: keep the sent records available
    // so the lesson can verify them.
    //
    // CopyOnWriteArrayList is also thread-safe. It is a good fit for this
    // small lesson recorder because writes are rare and reads are simple.
    //
    // "Copy on write" means:
    //
    // - When code reads the list, it reads from the current internal array.
    // - When code adds a frame, CopyOnWriteArrayList does not change that
    //   current array in place.
    // - Instead, it creates a new internal array that contains the old items
    //   plus the new frame.
    // - Then it publishes that new array as the current list contents.
    //
    // That makes reads thread-safe because a reader never sees a half-changed
    // array. A reader either sees the old complete array or the new complete
    // array.
    //
    // That also makes writes more expensive because every add copies the array.
    // This would be too costly for a busy production outbox with many writes,
    // but it keeps a small teaching recorder simple and safe for tests.
    private final List<Lesson15CourierOutboundFrame> outboundFrames = new CopyOnWriteArrayList<>();

    public void submitInboundFrame(Lesson15CourierInboundFrame frame) {
        inboundFrames.add(frame);
    }

    public Lesson15CourierInboundFrame pollInboundFrame() {
        return inboundFrames.poll();
    }

    public void sendOutboundFrame(Lesson15CourierOutboundFrame frame) {
        outboundFrames.add(frame);
    }

    public List<Lesson15CourierOutboundFrame> outboundFrames() {
        return List.copyOf(outboundFrames);
    }

    public void clear() {
        inboundFrames.clear();
        outboundFrames.clear();
    }
}
