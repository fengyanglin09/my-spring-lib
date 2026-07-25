package io.github.fengyanglin09.springintegrationlab.lessons.lesson02_messages.handler;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson02_messages.model.Lesson02MessageReport;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson02_messages.model.Lesson02OrderRequest;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson02_messages.support.Lesson02Headers;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

/**
 * Reads the payload and headers from the full Spring Integration message.
 */
// @Component tells Spring:
// "Create one Lesson02MessageInspector bean and make it available to the flow."
@Component
public class Lesson02MessageInspector {

    public Lesson02MessageReport inspect(Message<Lesson02OrderRequest> message) {
        // A Message has two main parts:
        // - payload: the main business object
        // - headers: metadata about delivery, tracing, routing, source, tenant, etc.
        //
        // These correspond to the @Payload and @Header annotations on
        // Lesson02MessageGateway.inspect(...).
        Lesson02OrderRequest payload = message.getPayload();
        MessageHeaders headers = message.getHeaders();

        // These two headers were supplied by the gateway method arguments marked @Header.
        String tenantId = headers.get(Lesson02Headers.TENANT_ID, String.class);
        String sourceSystem = headers.get(Lesson02Headers.SOURCE_SYSTEM, String.class);

        // Spring also adds framework headers such as id and timestamp.
        // We do not care about their exact values in this lesson; we only prove
        // that Spring created them as part of the Message wrapper.
        boolean messageIdPresent = headers.containsKey(MessageHeaders.ID);
        boolean timestampPresent = headers.containsKey(MessageHeaders.TIMESTAMP);

        // Returning a value from the handler makes that value the reply payload.
        // The gateway returns this Lesson02MessageReport to the original caller.
        return new Lesson02MessageReport(
                payload.orderId(),
                payload.customerId(),
                payload.orderAmount(),
                tenantId,
                sourceSystem,
                payload.getClass().getSimpleName(),
                messageIdPresent,
                timestampPresent
        );
    }
}
