package io.github.fengyanglin09.springintegrationlab.lessons.lesson02_messages.support;

/**
 * Header names used by lesson 02.
 *
 * <p>Header names are plain strings. Keeping them as constants avoids spelling
 * the same header name differently in the gateway, handler, and tests.</p>
 */
public final class Lesson02Headers {

    public static final String TENANT_ID = "lesson02_tenantId";
    public static final String SOURCE_SYSTEM = "lesson02_sourceSystem";

    private Lesson02Headers() {
    }
}
