package io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.support;

/**
 * Header names created by the lesson 15 inbound adapter.
 */
public final class Lesson15HeaderNames {

    // These constants are the exact names used when Lesson15CourierFrameSource
    // creates message headers.
    //
    // Using constants avoids typing the same string in several places. That
    // matters for headers because a typo creates a different header name.
    //
    // Example:
    //
    // - "lesson15FrameId" is the real header.
    // - "lesson15FramId" would be a different missing header.
    public static final String FRAME_ID = "lesson15FrameId";
    public static final String REMOTE_SYSTEM = "lesson15RemoteSystem";
    public static final String CONTENT_TYPE = "lesson15ContentType";

    private Lesson15HeaderNames() {
    }
}
