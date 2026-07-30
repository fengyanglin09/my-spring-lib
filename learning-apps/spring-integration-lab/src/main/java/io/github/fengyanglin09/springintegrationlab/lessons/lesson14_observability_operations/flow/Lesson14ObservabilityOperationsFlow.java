package io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.flow;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.handler.Lesson14ObservationRecorder;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.handler.Lesson14ShipmentHandler;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.model.Lesson14ShipmentEvent;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.model.Lesson14ShipmentUpdate;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.support.Lesson14Channels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.Message;

// @Configuration tells Spring:
// "Read this class during startup because it contains bean definitions."
@Configuration
public class Lesson14ObservabilityOperationsFlow {

    // @Bean tells Spring:
    // "Create the normal shipment IntegrationFlow during startup."
    @Bean
    IntegrationFlow lesson14ShipmentIntegrationFlow(Lesson14ShipmentHandler shipmentHandler) {
        // This is the main business path:
        //
        // shipment event
        // -> prepare shipment update
        // -> send a copy to the observer
        // -> dispatch shipment
        return IntegrationFlow.from(Lesson14Channels.SHIPMENT_EVENTS)
                .transform(Lesson14ShipmentEvent.class, event -> shipmentHandler.prepare(event))
                // wireTap(...) means:
                // "Send a copy of the current message to another channel, but
                // let the original message continue through this flow."
                //
                // The current payload is Lesson14ShipmentUpdate.
                //
                // The copy goes to lesson14ObservationEvents. The original
                // continues to the dispatch handler below.
                //
                // Analogy:
                // A wire tap is like adding an inspection camera over a
                // conveyor belt. The package keeps moving, but the camera takes
                // a record of what passed by.
                .wireTap(Lesson14Channels.OBSERVATION_EVENTS)
                .handle(
                        Lesson14ShipmentUpdate.class,
                        (update, headers) -> shipmentHandler.dispatch(update)
                )
                .get();
    }

    // @Bean tells Spring:
    // "Create the observation IntegrationFlow during startup."
    @Bean
    IntegrationFlow lesson14ObservationIntegrationFlow(Lesson14ObservationRecorder recorder) {
        // This is the observation side path.
        //
        // It receives copies from the wire tap. It should not change the normal
        // shipment result because the business flow is using the original
        // message, not this copy.
        return IntegrationFlow.from(Lesson14Channels.OBSERVATION_EVENTS)
                .handle((Message<?> message) -> recorder.record(message))
                .get();
    }

    // @Bean tells Spring:
    // "Create the control-bus IntegrationFlow during startup."
    @Bean
    IntegrationFlow lesson14ControlBusIntegrationFlow() {
        // controlBus() means:
        // "Treat the message payload as an operation command."
        //
        // Operation command means:
        // "An admin instruction that tells Spring Integration to call a method
        // on a Spring bean."
        //
        // It is not a normal business message like Lesson14ShipmentEvent.
        // It is a control message for changing or inspecting runtime behavior.
        //
        // In this lesson, the operation command is just a String payload.
        //
        // Example String payload:
        //
        // lesson14ObservationRecorder.stopObservation
        //
        // Spring Integration reads that String as:
        //
        // - bean name: lesson14ObservationRecorder
        // - method:    stopObservation
        //
        // Then it asks Spring for the bean named lesson14ObservationRecorder
        // and calls its @ManagedOperation method named stopObservation().
        //
        // Analogy:
        //
        // - shipment flow = packages moving on the conveyor belt
        // - control bus   = control-room command like "turn off recording"
        return IntegrationFlow.from(Lesson14Channels.OPERATIONS)
                .controlBus()
                .get();
    }
}
