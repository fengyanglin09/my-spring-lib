package io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.gateway;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.model.Lesson07ShapeReport;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.support.Lesson07Channels;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.support.Lesson07Headers;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Typed entry point into the lesson 07 transformer/enricher flow.
 */
// @MessagingGateway tells Spring Integration:
// "Create an implementation of this interface at startup."
//
// Calling shape(...) sends a Spring Integration message into the lesson flow.
@MessagingGateway
public interface Lesson07ShapeGateway {

    // @Gateway says:
    // "When shape(...) is called, send the message to lesson07RawOrderLines."
    //
    // @Payload says:
    // "Use rawOrderLine as the message payload."
    //
    // @Header says:
    // "Add sourceSystem as message metadata under the header name
    // lesson07SourceSystem."
    //
    // This header is different from the payload. The payload is the raw CSV
    // text. The header is metadata about where that text came from.
    @Gateway(requestChannel = Lesson07Channels.RAW_ORDER_LINES)
    Lesson07ShapeReport shape(
            @Payload String rawOrderLine,
            @Header(Lesson07Headers.SOURCE_SYSTEM) String sourceSystem
    );
}
