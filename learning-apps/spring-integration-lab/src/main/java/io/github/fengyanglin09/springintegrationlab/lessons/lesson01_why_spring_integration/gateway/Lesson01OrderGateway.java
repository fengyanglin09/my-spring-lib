package io.github.fengyanglin09.springintegrationlab.lessons.lesson01_why_spring_integration.gateway;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson01_why_spring_integration.flow.Lesson01WhySpringIntegrationFlow;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson01_why_spring_integration.model.Lesson01OrderRequest;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson01_why_spring_integration.model.Lesson01OrderResult;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

/**
 * The typed entry point into the lesson 01 integration flow.
 *
 * <p>There is no class that implements this interface in our source code.
 * Spring Integration creates the implementation at runtime. That generated
 * implementation is called a gateway proxy.</p>
 *
 * <p>Callers use this as if it were a normal Java service. Behind the scenes,
 * Spring Integration turns the method argument into a message and sends it to
 * the configured request channel.</p>
 */
// @MessagingGateway tells Spring Integration:
// "Create a Spring bean that implements this interface for me."
@MessagingGateway
public interface Lesson01OrderGateway {

    // @Gateway tells Spring Integration what to do when route(...) is called:
    //
    // 1. Take the Lesson01OrderRequest argument.
     // 2. Wrap it in a Spring Integration Message.
     // 3. Send that message to the channel named lesson01OrderRequests.
    // 4. Wait for the flow's handler method to return a Lesson01OrderResult.
    // 5. Return that Lesson01OrderResult to the original Java caller.
    //
    // The gateway can return Lesson01OrderResult because the flow eventually
    // calls Lesson01OrderHandler.route(...), and that handler method also
    // returns Lesson01OrderResult.
     //
     // requestChannel must match the channel name used by IntegrationFlow.from(...).
    @Gateway(requestChannel = Lesson01WhySpringIntegrationFlow.INPUT_CHANNEL)
    Lesson01OrderResult route(Lesson01OrderRequest request);
}
