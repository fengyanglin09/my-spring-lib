package io.github.fengyanglin09.springintegrationlab.lessons.lesson02_messages.gateway;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson02_messages.flow.Lesson02MessagesFlow;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson02_messages.model.Lesson02MessageReport;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson02_messages.model.Lesson02OrderRequest;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson02_messages.support.Lesson02Headers;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Typed entry point into the lesson 02 message flow.
 *
 * <p>This gateway shows how one Java method call becomes a Spring Integration
 * message with two parts:</p>
 *
 * <ul>
 *     <li>the {@link Lesson02OrderRequest} method argument becomes the payload</li>
 *     <li>the {@code tenantId} and {@code sourceSystem} arguments become headers</li>
 * </ul>
 */
// @MessagingGateway tells Spring Integration:
// "Create a Spring bean that implements this interface for me."
@MessagingGateway
public interface Lesson02MessageGateway {

    // @Gateway says:
    // "When inspect(...) is called, send a message to the lesson02Messages channel."
    //
    // @Payload says:
    // "Use this argument as the message payload."
    // In this lesson, the order request is the payload because it is the main
    // business data the flow is carrying.
    //
    // @Header says:
    // "Put this argument into the message headers under the given header name."
    // Headers are metadata labels on the message. Here, tenantId and sourceSystem
    // describe context about the order message, but they are not the order itself.
    //
    // Calling:
    // messageGateway.inspect(request, "tenant-north", "mobile-checkout")
    //
    // creates a message shaped like:
    //
    // payload:
    //   request
    //
    // headers:
    //   lesson02_tenantId: "tenant-north"
    //   lesson02_sourceSystem: "mobile-checkout"
    //
    // The flow's handler returns Lesson02MessageReport, so this gateway method
    // returns Lesson02MessageReport to the original caller.
    @Gateway(requestChannel = Lesson02MessagesFlow.INPUT_CHANNEL)
    Lesson02MessageReport inspect(
            @Payload Lesson02OrderRequest request,
            @Header(Lesson02Headers.TENANT_ID) String tenantId,
            @Header(Lesson02Headers.SOURCE_SYSTEM) String sourceSystem
    );
}
