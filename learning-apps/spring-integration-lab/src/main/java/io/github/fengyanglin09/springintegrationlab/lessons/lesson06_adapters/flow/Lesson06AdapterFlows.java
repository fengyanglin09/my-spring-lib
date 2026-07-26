package io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters.flow;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters.handler.Lesson06AdapterTranslator;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters.model.Lesson06PartnerOrderRequest;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters.support.Lesson06AdapterIds;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters.support.Lesson06Channels;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters.support.Lesson06ExternalOrderInbox;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters.support.Lesson06PartnerOrderOutbox;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;

import java.time.Duration;

// @Configuration tells Spring:
// "Read this class during startup because it contains bean definitions."
@Configuration
public class Lesson06AdapterFlows {

    // This flow starts with an inbound channel adapter.
    //
    // Inbound adapter means:
    // "Something outside Spring Integration is checked or listened to, and when
    // data is found, that data becomes a Spring Integration message."
    @Bean
    IntegrationFlow lesson06InboundAdapterFlow(
            Lesson06ExternalOrderInbox externalInbox,
            Lesson06AdapterTranslator translator
    ) {
        return IntegrationFlow.fromSupplier(
                        externalInbox::poll,
                        adapter -> adapter
                                // id(...) gives the adapter endpoint a stable
                                // bean name. The test uses this name to start
                                // the adapter only when it is ready.
                                .id(Lesson06AdapterIds.INBOUND_ADAPTER)
                                // autoStartup(false) means:
                                // "Create this adapter during Spring startup,
                                // but do not begin polling immediately."
                                //
                                // This keeps the lesson test deterministic.
                                .autoStartup(false)
                                // A poller is a small scheduler.
                                //
                                // It repeatedly asks the source method
                                // externalInbox.poll() whether there is new
                                // external data to turn into a message.
                                .poller(Pollers.fixedDelay(Duration.ofMillis(25))
                                        .maxMessagesPerPoll(1))
                )
                // At this point, the payload is Lesson06ExternalOrderRecord.
                //
                // This channel is the first named point inside Spring Integration.
                .channel(Lesson06Channels.EXTERNAL_ORDER_RECORDS)
                // Convert the external source shape into our internal shape.
                .transform(translator, "toInternalOrder")
                // Convert our internal shape into the partner-facing shape.
                .transform(translator, "toPartnerOrderRequest")
                // Send the partner-facing request to the channel named
                // "lesson06PartnerOrderRequests".
                //
                // Important:
                // This inbound flow does NOT directly call the outbound flow.
                //
                // It only sends a message to a channel. The outbound flow below
                // starts from this same channel, so it receives the message when
                // the channel delivers it.
                //
                // Analogy:
                // This flow puts a package on the lesson06PartnerOrderRequests
                // conveyor belt. The outbound flow is the worker standing at
                // that conveyor belt.
                .channel(Lesson06Channels.PARTNER_ORDER_REQUESTS)
                .get();
    }

    // This flow ends with an outbound channel adapter.
    //
    // Outbound adapter means:
    // "A message from a Spring Integration channel is sent to something outside
    // the messaging system."
    @Bean
    IntegrationFlow lesson06OutboundAdapterFlow(Lesson06PartnerOrderOutbox partnerOutbox) {
        // from(PARTNER_ORDER_REQUESTS) means:
        // "Start this outbound flow whenever a message is sent to the channel
        // named lesson06PartnerOrderRequests."
        //
        // In this lesson, the inbound flow above sends to that channel at its
        // final .channel(...) step. That channel handoff is what connects the
        // two flows.
        return IntegrationFlow.from(Lesson06Channels.PARTNER_ORDER_REQUESTS)
                // This handle(...) step is being used as an outbound adapter.
                //
                // We use the typed lambda form instead of:
                //
                // .handle(partnerOutbox, "send")
                //
                // because the string "send" feels magical when you are learning.
                // With the typed lambda, Java knows the payload should be a
                // Lesson06PartnerOrderRequest.
                //
                // The lambda parameters are:
                //
                // - request: the message payload
                // - headers: the message headers
                //
                // We do not use headers in this lesson, but Spring Integration
                // provides them in case the handler needs metadata such as a
                // correlation id, source name, or trace id.
                .handle(Lesson06PartnerOrderRequest.class, (request, headers) -> {
                    partnerOutbox.send(request);

                    // Returning null means:
                    // "This step does not produce a reply message."
                    //
                    // That is correct here because this is the end of a one-way
                    // outbound adapter flow. There is no later step waiting for
                    // another payload.
                    return null;
                })
                .get();
    }
}
