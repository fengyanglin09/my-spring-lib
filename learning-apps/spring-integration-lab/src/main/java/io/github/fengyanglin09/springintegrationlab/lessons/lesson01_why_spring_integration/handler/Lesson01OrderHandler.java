package io.github.fengyanglin09.springintegrationlab.lessons.lesson01_why_spring_integration.handler;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson01_why_spring_integration.model.Lesson01OrderRequest;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson01_why_spring_integration.model.Lesson01OrderResult;
import org.springframework.stereotype.Component;

/**
 * Business behavior called by the integration flow.
 *
 * <p>The flow answers, "Where does the message go next?" This handler answers,
 * "What result should this order produce?" Keeping those two ideas separate is
 * one of the main reasons Spring Integration can keep larger flows readable.</p>
 */
// @Component tells Spring:
// "Create one Lesson01OrderHandler bean and make it available to other beans."
//
// The IntegrationFlow method receives this bean as the handler parameter.
@Component
public class Lesson01OrderHandler {

    public Lesson01OrderResult route(Lesson01OrderRequest request) {
        // This is ordinary Java business logic.
        // Spring Integration delivered the request here, but this method does not
        // need to know anything about channels, gateways, or messages.
        //
        // The returned Lesson01OrderResult is important: Spring Integration sends
        // this value back through the request-reply flow, so it becomes the return
        // value of Lesson01OrderGateway.route(...).
        if ("VIP".equalsIgnoreCase(request.customerType())) {
            return new Lesson01OrderResult(
                    request.orderId(),
                    "priority-billing",
                    "VIP order routed through the lesson 01 integration flow"
            );
        }

        return new Lesson01OrderResult(
                request.orderId(),
                "standard-billing",
                "Standard order routed through the lesson 01 integration flow"
        );
    }
}
