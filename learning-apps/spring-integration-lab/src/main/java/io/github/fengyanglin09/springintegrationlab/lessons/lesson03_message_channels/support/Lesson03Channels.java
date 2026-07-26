package io.github.fengyanglin09.springintegrationlab.lessons.lesson03_message_channels.support;

/**
 * Channel bean names used by lesson 03.
 *
 * <p>A channel name is how producers and consumers agree on the same in-memory
 * message path without directly referencing each other.</p>
 */
public final class Lesson03Channels {

    public static final String DIRECT_ORDERS = "lesson03DirectOrders";
    public static final String BROADCAST_EVENTS = "lesson03BroadcastEvents";

    private Lesson03Channels() {
    }
}
