package io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.flow;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.handler.Lesson15CourierFrameMapper;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.model.Lesson15CourierInboundFrame;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.model.Lesson15PartnerExport;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.model.Lesson15ShipmentCommand;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.support.Lesson15AdapterIds;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.support.Lesson15Channels;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.support.Lesson15CourierFrameSource;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.support.Lesson15CourierOutboundAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;

import java.time.Duration;

// @Configuration tells Spring:
// "Read this class during startup because it contains bean definitions."
@Configuration
public class Lesson15AdapterDeepDiveFlow {

    // @Bean tells Spring:
    // "Create this IntegrationFlow during startup."
    //
    // Lesson 06 introduced the basic words "inbound adapter" and
    // "outbound adapter."
    //
    // Lesson 15 looks more closely at the adapter boundary:
    //
    // - What comes from the external protocol?
    // - What becomes the Spring Integration message payload?
    // - What becomes message headers?
    // - How do those headers survive while the payload changes?
    // - How does the outbound adapter rebuild an external protocol envelope?
    @Bean
    IntegrationFlow lesson15CourierAdapterIntegrationFlow(
            Lesson15CourierFrameSource frameSource,
            Lesson15CourierFrameMapper frameMapper,
            Lesson15CourierOutboundAdapter outboundAdapter
    ) {
        // IntegrationFlow.from(...) means:
        // "This is where the flow starts."
        //
        // Spring Integration has several from(...) overloads, so the parameters
        // can look different in different lessons.
        //
        // Previous examples you have seen:
        //
        // IntegrationFlow.from("someChannel")
        //
        // means:
        // "Start this flow when another part of the app sends a message to the
        // channel named someChannel."
        //
        // IntegrationFlow.fromSupplier(supplier, adapterConfig -> ...)
        //
        // means:
        // "Use a plain Java Supplier as the thing to poll for new payloads."
        //
        // This lesson uses:
        //
        // IntegrationFlow.from(frameSource, adapterConfig -> ...)
        //
        // Parameter 1: frameSource
        //
        // - frameSource implements MessageSource<Lesson15CourierInboundFrame>.
        // - Spring Integration will call frameSource.receive() on a schedule.
        // - If receive() returns null, no message is created for that poll.
        // - If receive() returns Message<Lesson15CourierInboundFrame>, that
        //   message becomes the first message in this flow.
        //
        // Parameter 2: adapterConfig -> ...
        //
        // - This is not business logic.
        // - This is not the message payload.
        // - This is configuration for the inbound polling adapter endpoint.
        //
        // Inside this second parameter we tell Spring:
        //
        // - what bean id to give the adapter endpoint
        // - whether it should start automatically
        // - how often it should poll frameSource.receive()
        //
        // Analogy:
        // frameSource is the mailbox.
        // adapterConfig is the schedule and settings for the person checking
        // that mailbox.
        return IntegrationFlow.from(
                        frameSource,
                        adapterConfig -> adapterConfig
                                // id(...) gives the inbound adapter endpoint a
                                // stable Spring bean name.
                                //
                                // The test uses this id to fetch the adapter
                                // from the ApplicationContext and start it only
                                // when the test has already placed fake courier
                                // data into the sandbox.
                                .id(Lesson15AdapterIds.INBOUND_ADAPTER)
                                // autoStartup(false) means:
                                // "Create the inbound adapter during Spring
                                // startup, but do not start polling yet."
                                //
                                // If this were true, the adapter could begin
                                // polling before the test has arranged its
                                // input data. Turning auto startup off makes
                                // the lesson easier to observe.
                                .autoStartup(false)
                                // poller(...) configures how often this inbound
                                // adapter asks its source for data.
                                //
                                // Here, the source is Lesson15CourierFrameSource.
                                // Spring Integration calls frameSource.receive()
                                // on the poller schedule.
                                //
                                // maxMessagesPerPoll(1) means one poll cycle
                                // will turn at most one courier frame into one
                                // Spring Integration message.
                                .poller(Pollers.fixedDelay(Duration.ofMillis(25))
                                        .maxMessagesPerPoll(1))
                )
                // This named channel is the first point after the inbound
                // adapter has crossed the boundary into Spring Integration.
                //
                // At this point, each message has:
                //
                // - payload: Lesson15CourierInboundFrame
                // - headers: lesson15FrameId, lesson15RemoteSystem,
                //   lesson15ContentType
                //
                // The payload is the main object the next step will work on.
                // The headers are metadata about where the object came from.
                //
                // Where is this channel created?
                //
                // Lesson15Channels.INBOUND_COURIER_FRAMES is just a String
                // constant whose value is "lesson15InboundCourierFrames".
                //
                // There is no separate @Bean method for this channel in lesson
                // 15. When Spring Integration reads this flow during
                // application startup, it sees this .channel("...") step.
                //
                // The Java DSL then checks the Spring application context:
                //
                // - If a MessageChannel bean named lesson15InboundCourierFrames
                //   already exists, use it.
                // - If it does not exist, create and register a default
                //   DirectChannel bean with that name.
                //
                // DirectChannel means the next endpoint is called directly in
                // the same thread that sends the message.
                //
                // Why no explicit channel @Bean in a config/ folder?
                //
                // Because this lesson does not need queueing, broadcasting, or
                // thread handoff at this point. It only needs a simple named
                // connection from the inbound adapter to the first transform.
                // The default DirectChannel gives us that.
                //
                // Where is the handler for this named channel?
                //
                // The channel itself does not contain business logic. It is
                // only the handoff point.
                //
                // The handler is the next endpoint after this line:
                //
                // .transform(Lesson15CourierInboundFrame.class, ...)
                //
                // When the inbound adapter sends a message to
                // lesson15InboundCourierFrames, this next transform endpoint is
                // subscribed to that channel and receives the message.
                //
                // Important refinement:
                // The handler for this named channel is the immediate next
                // endpoint after .channel(...), not "everything after this
                // channel until the next named channel."
                //
                // Here, the immediate next endpoint is the transform below.
                // After that transform returns a new payload, Spring
                // Integration passes that output forward to the next step in
                // the flow.
                //
                // Purpose of this channel:
                //
                // - give this handoff a readable name
                // - mark the point where data has crossed the inbound adapter
                //   boundary
                // - make it possible later to attach channel-level behavior,
                //   such as logging, wire tapping, interceptors, or replacing
                //   the default DirectChannel with a queue/executor channel
                .channel(Lesson15Channels.INBOUND_COURIER_FRAMES)
                // transform(...) means:
                // "Take the current payload, call this lambda, and replace the
                // payload with the lambda return value."
                //
                // This transform endpoint is the handler/subscriber for the
                // lesson15InboundCourierFrames channel above.
                //
                // Its purpose is to handle messages that arrive on that channel
                // by converting the payload from the external courier frame
                // shape into the internal shipment command shape.
                //
                // Before this step:
                //
                // - payload is Lesson15CourierInboundFrame
                //
                // After this step:
                //
                // - payload is Lesson15ShipmentCommand
                //
                // Important:
                // The headers are still attached to the message unless a step
                // intentionally removes them. This is why the frame id, remote
                // system, and content type can still be read by the outbound
                // adapter later even though the payload type changes.
                .transform(
                        Lesson15CourierInboundFrame.class,
                        frame -> frameMapper.toShipmentCommand(frame)
                )
                // This named channel marks the point where the protocol-looking
                // input has become an internal application command.
                //
                // It is not required for the flow to work. Spring Integration
                // can connect the two transforms directly. We name it here so
                // the lesson has a clear checkpoint:
                //
                // "External courier frame has become internal shipment work."
                //
                // This channel follows the same rule as the previous named
                // channel:
                //
                // Lesson15Channels.INTERNAL_SHIPMENTS is the name.
                // The Java DSL creates the default DirectChannel bean at
                // startup because lesson 15 does not define one explicitly.
                //
                // If a future lesson wanted this point to queue messages or
                // hand work to another thread pool, then we would create an
                // explicit channel bean in a config/ class instead of relying
                // on the default DirectChannel.
                //
                // Where is the handler for this named channel?
                //
                // The handler is the next endpoint after this line:
                //
                // .transform(Lesson15ShipmentCommand.class, ...)
                //
                // When a message is sent to lesson15InternalShipments, this
                // next transform endpoint receives it.
                //
                // Again, the handler for this named channel is the immediate
                // next endpoint only. In this case, it is the transform below.
                //
                // The final handle(...) endpoint later in the flow does not
                // directly subscribe to lesson15InternalShipments. It receives
                // a message only after this transform creates its output and
                // Spring Integration passes that output to the next step.
                //
                // Purpose of this channel:
                //
                // - give the middle of the flow a readable name
                // - mark the point where the message payload is now an internal
                //   application command
                // - leave a clear place where we could later add queueing,
                //   metrics, wire taps, or another flow that starts from the
                //   same named channel
                .channel(Lesson15Channels.INTERNAL_SHIPMENTS)
                // This second transform changes the internal command into the
                // payload shape that the outbound adapter needs.
                //
                // This transform endpoint is the handler/subscriber for the
                // lesson15InternalShipments channel above.
                //
                // Before this step:
                //
                // - payload is Lesson15ShipmentCommand
                //
                // After this step:
                //
                // - payload is Lesson15PartnerExport
                //
                // The headers from the inbound adapter are still traveling with
                // the message.
                .transform(
                        Lesson15ShipmentCommand.class,
                        command -> frameMapper.toPartnerExport(command)
                )
                // handle(...) creates a service-activator endpoint.
                //
                // In this lesson, we are using that endpoint as an outbound
                // adapter boundary:
                //
                // "Take the Spring Integration message and send something to an
                // external system."
                //
                // The lambda parameters are:
                //
                // - export: the current payload
                // - headers: the metadata still attached to the message
                //
                // The outbound adapter uses both:
                //
                // - export becomes the outbound frame body
                // - headers provide the original frame id, remote system, and
                //   content type for the outbound envelope
                .handle(Lesson15PartnerExport.class, (export, headers) -> {
                    outboundAdapter.send(export, headers);

                    // Returning null means:
                    // "This handler does not create another message for a next
                    // step."
                    //
                    // There is no next step in this flow because sending the
                    // outbound frame is the final one-way action.
                    //
                    // This does not shut down the adapter. It only ends the
                    // path for the current message. The inbound adapter can
                    // still poll again later and process another courier frame.
                    return null;
                })
                // get() finishes the builder chain and gives Spring the
                // IntegrationFlow object to register.
                .get();
    }
}
