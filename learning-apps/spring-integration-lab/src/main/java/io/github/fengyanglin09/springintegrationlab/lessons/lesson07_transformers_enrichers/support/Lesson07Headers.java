package io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.support;

/**
 * Header names used by lesson 07.
 *
 * <p>Spring Integration message headers are stored by name. The name is a
 * String, such as {@code "lesson07SourceSystem"}.</p>
 *
 * <p>We keep those names as constants instead of repeating string literals
 * across the lesson because a typo would create a different header. For example,
 * {@code "lesson07SourceSystem"} and {@code "lesson07sourceSystem"} would not
 * be the same header.</p>
 */
// final means:
// "No other class should extend this class."
//
// This class is not a normal object with behavior. It is only a small holder
// for shared constant names.
public final class Lesson07Headers {

    // SOURCE_SYSTEM is added by the gateway.
    //
    // It tells the flow where the raw order line came from, such as
    // "csv-upload" or "manual-entry".
    public static final String SOURCE_SYSTEM = "lesson07SourceSystem";

    // LESSON_NAME is added by the header enricher in the flow.
    //
    // It is simple metadata that proves the header enricher can add a constant
    // header value without changing the payload.
    public static final String LESSON_NAME = "lesson07LessonName";

    // VALUE_BAND is added by the header enricher in the flow.
    //
    // It is computed from the payload amount, but it is stored as metadata in a
    // header so downstream steps can read it without changing the payload shape.
    public static final String VALUE_BAND = "lesson07ValueBand";

    // SHAPE_STAGE is added by the header enricher in the flow.
    //
    // It marks where the message is in this learning example. In production,
    // similar headers might describe routing stage, source type, or trace data.
    public static final String SHAPE_STAGE = "lesson07ShapeStage";

    // private constructor means:
    // "Do not create Lesson07Headers objects."
    //
    // Since all fields are static constants, code uses them like this:
    //
    // Lesson07Headers.SOURCE_SYSTEM
    //
    // There is no need to call new Lesson07Headers().
    private Lesson07Headers() {
    }
}
