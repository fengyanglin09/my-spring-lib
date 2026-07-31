package io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.support;

/**
 * Channel bean names used by lesson 15.
 */
public final class Lesson15Channels {

    // These constants are only channel names.
    //
    // This line does not create a MessageChannel object by itself:
    //
    // public static final String INBOUND_COURIER_FRAMES = ...
    //
    // It only gives us one safe place to store the exact string
    // "lesson15InboundCourierFrames".
    //
    // The actual channel is created later when Spring Integration reads the
    // IntegrationFlow and sees:
    //
    // .channel(Lesson15Channels.INBOUND_COURIER_FRAMES)
    //
    // If a MessageChannel bean with that name already exists, Spring
    // Integration uses the existing bean.
    //
    // If no bean with that name exists, the Java DSL registers a DirectChannel
    // bean with that name for us.
    //
    // Lesson 15 does not need a special channel type, so we let the DSL create
    // the default DirectChannel.
    //
    // That is why lesson 15 does not have a config/ class for these channels.
    // A config/ class would be useful if we wanted explicit channel behavior,
    // such as:
    //
    // - QueueChannel: messages wait in a queue until a poller receives them
    // - PublishSubscribeChannel: one message is broadcast to multiple handlers
    // - ExecutorChannel: messages are handed off to another thread pool
    //
    // This lesson only needs a simple direct handoff from one endpoint to the
    // next endpoint, so the implicit DirectChannel is enough.
    public static final String INBOUND_COURIER_FRAMES = "lesson15InboundCourierFrames";
    public static final String INTERNAL_SHIPMENTS = "lesson15InternalShipments";

    private Lesson15Channels() {
    }
}
