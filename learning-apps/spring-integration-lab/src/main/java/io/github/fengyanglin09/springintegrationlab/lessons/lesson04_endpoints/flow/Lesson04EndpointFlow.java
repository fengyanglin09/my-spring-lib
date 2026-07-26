package io.github.fengyanglin09.springintegrationlab.lessons.lesson04_endpoints.flow;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson04_endpoints.handler.Lesson04OrderNormalizer;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson04_endpoints.handler.Lesson04OrderReporter;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson04_endpoints.support.Lesson04Channels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;

// @Configuration tells Spring to read this class during startup.
//
// This class does not do business work itself. It describes how messages move
// from one channel to one endpoint, then to another channel and endpoint.
@Configuration
public class Lesson04EndpointFlow {

    // @Bean tells Spring to create this IntegrationFlow during startup.
    //
    // The method name becomes the bean name unless we give @Bean an explicit
    // name. We use a method name that is different from the configuration class
    // name so Spring does not try to register two beans with the same name.
    //
    // IntegrationFlow is Spring Integration's Java builder for saying:
    // "Start from this channel, then do these message-processing steps."
    @Bean
    IntegrationFlow lesson04EndpointsIntegrationFlow(
            Lesson04OrderNormalizer normalizer,
            Lesson04OrderReporter reporter
    ) {
        return IntegrationFlow.from(Lesson04Channels.RAW_ORDERS)
                // This creates a transformer endpoint.
                //
                // Endpoint means:
                // "A Spring Integration component attached to a channel so it
                // can do work when a message reaches that point in the flow."
                //
                // Transformer means:
                // "Call normalizer.normalize(...) and replace the current
                // payload with that method's return value."
                //
                // Before this step, the payload is Lesson04OrderRequest.
                // After this step, the payload is Lesson04NormalizedOrder
                // because normalize(...) returns Lesson04NormalizedOrder.
                .transform(normalizer, "normalize")
                // Send the transformed message to the channel named
                // "lesson04NormalizedOrders".
                //
                // That channel is created in Lesson04ChannelConfiguration by
                // the @Bean method lesson04NormalizedOrders().
                //
                // We could connect the transformer directly to the next step,
                // but naming the middle channel makes it easier to see the
                // handoff between two endpoints.
                .channel(Lesson04Channels.NORMALIZED_ORDERS)
                // This creates a service-activator endpoint.
                //
                // Service activator means:
                // "Call ordinary application code to do work for this message."
                //
                // Here, the ordinary application code is reporter.report(...).
                //
                // reporter.report(...) returns Lesson04EndpointReport, so the
                // gateway receives that report as the method return value.
                .handle(reporter, "report")
                // get() finishes the builder chain and gives Spring the
                // IntegrationFlow object it should register.
                .get();
    }
}
