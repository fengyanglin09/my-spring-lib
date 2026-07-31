package io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.support;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.model.Lesson15CourierInboundFrame;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * Inbound adapter source for the fake courier protocol.
 */
// @Component tells Spring:
// "Create one Lesson15CourierFrameSource object during startup."
//
// MessageSource means:
// "Something Spring Integration can poll to receive the next message."
//
// implements MessageSource<Lesson15CourierInboundFrame> means:
// "This class promises to provide a receive() method that returns either:
//
// - Message<Lesson15CourierInboundFrame> when data is available
// - null when no data is available right now
//
// The generic type Lesson15CourierInboundFrame tells Spring Integration:
// "When this source creates a message, the payload type is
// Lesson15CourierInboundFrame."
//
// We use MessageSource here because this lesson wants to control the full
// message created at the adapter boundary:
//
// - payload: the courier frame object the next flow step should transform
// - headers: metadata copied from the courier frame, such as frame id, remote
//   system, and content type
//
// If this class only returned a plain Lesson15CourierInboundFrame object, the
// lesson would not be as explicit about where those headers are created.
@Component
public class Lesson15CourierFrameSource implements MessageSource<Lesson15CourierInboundFrame> {

    private final Lesson15CourierProtocolSandbox courierProtocolSandbox;

    public Lesson15CourierFrameSource(Lesson15CourierProtocolSandbox courierProtocolSandbox) {
        this.courierProtocolSandbox = courierProtocolSandbox;
    }

    @Override
    public Message<Lesson15CourierInboundFrame> receive() {
        // receive() is called by the inbound channel adapter's poller.
        //
        // If there is no external frame, return null. For an inbound adapter,
        // null means:
        // "No message is available right now."
        Lesson15CourierInboundFrame frame = courierProtocolSandbox.pollInboundFrame();
        if (frame == null) {
            return null;
        }

        // This is the adapter boundary.
        //
        // The external protocol gives us one envelope object. The adapter turns
        // that envelope into a Spring Integration message:
        //
        // - payload = the object the next endpoint should work on
        // - headers = protocol metadata the flow may need later
        //
        // In this lesson, we keep the whole Lesson15CourierInboundFrame as the
        // payload for the first transform. That makes the first transform
        // responsible for reading frame.body() and converting it into an
        // internal Lesson15ShipmentCommand.
        //
        // Real adapters do this kind of mapping too. For example, a file, SFTP,
        // JMS, Kafka, or HTTP adapter usually maps protocol metadata into Spring
        // message headers.
        return MessageBuilder.withPayload(frame)
                .setHeader(Lesson15HeaderNames.FRAME_ID, frame.frameId())
                .setHeader(Lesson15HeaderNames.REMOTE_SYSTEM, frame.remoteSystem())
                .setHeader(Lesson15HeaderNames.CONTENT_TYPE, frame.contentType())
                .build();
    }
}
