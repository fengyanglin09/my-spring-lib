package io.github.fengyanglin09.springintegrationlab.lessons.lesson01_why_spring_integration.flow;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson01_why_spring_integration.handler.Lesson01OrderHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;

// @Configuration tells Spring:
// "This class contains methods that create beans for the application context."
@Configuration
public class Lesson01WhySpringIntegrationFlow {

    // This is the channel name shared by the gateway and the flow.
    //
    // Think of it as the name of an in-memory mailbox inside this Spring app:
    // - the gateway sends order messages to this channel name
    // - the flow receives order messages from this same channel name
    //
    // We do not create a channel bean manually in lesson 01. Because the flow
    // starts with IntegrationFlow.from(INPUT_CHANNEL), Spring Integration creates
    // a default in-memory DirectChannel with this name during application startup.
    public static final String INPUT_CHANNEL = "lesson01OrderRequests";

    // @Bean tells Spring:
    // "Run this method and register the returned IntegrationFlow as a Spring bean."
    //
    // IntegrationFlow is Spring Integration's Java DSL.
    // DSL means "Domain-Specific Language": a small API made for one job.
    // This DSL's job is to describe how messages move from step to step.
    @Bean
    IntegrationFlow lesson01OrderFlow(Lesson01OrderHandler handler) {
        return IntegrationFlow.from(INPUT_CHANNEL)
                // from(INPUT_CHANNEL) means:
                // "Start this flow when code sends a message to the in-memory
                // channel named lesson01OrderRequests."
                //
                // handle(handler, "route") means:
                // "Call handler.route(...) with the message payload."
                //
                // Because Lesson01OrderHandler.route(...) returns Lesson01OrderResult,
                // Spring Integration uses that return value as the reply payload.
                // That reply becomes the return value of Lesson01OrderGateway.route(...).
                //
                // We keep the if/else business decision inside Lesson01OrderHandler
                // so this flow stays focused on message movement.
                .handle(handler, "route")
                // get() finishes the DSL chain and builds the IntegrationFlow object.
                .get();
    }
}
