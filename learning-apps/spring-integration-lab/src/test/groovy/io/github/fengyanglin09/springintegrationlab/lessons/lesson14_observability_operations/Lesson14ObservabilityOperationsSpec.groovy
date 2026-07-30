package io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations

import io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.gateway.Lesson14OperationsGateway
import io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.gateway.Lesson14ShipmentGateway
import io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.handler.Lesson14ObservationRecorder
import io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.model.Lesson14ShipmentEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

// @SpringBootTest tells Spring Boot:
// "Start the application context for this spec."
//
// This test needs the real lesson 14 gateways, channels, wire tap, control bus,
// and handler beans.
@SpringBootTest
// @ActiveProfiles("test") tells Spring:
// "Load test-profile configuration from application-test.yml."
@ActiveProfiles("test")
class Lesson14ObservabilityOperationsSpec extends Specification {

    @Autowired
    Lesson14ShipmentGateway shipmentGateway

    @Autowired
    Lesson14OperationsGateway operationsGateway

    @Autowired
    Lesson14ObservationRecorder observationRecorder

    def setup() {
        // setup() runs before each feature method in this spec.
        //
        // Each test starts with observation turned on and no previously captured
        // records.
        observationRecorder.startObservation()
        observationRecorder.clear()
    }

    def "wire tap records a copy while the main shipment flow returns normally"() {
        given:
        def event = new Lesson14ShipmentEvent(
                "shipment-1401",
                "Chicago",
                true
        )

        when:
        def result = shipmentGateway.process(event)

        then:
        // The main flow still returns the normal shipment result.
        result.status() == "DISPATCHED"
        result.lane() == "EXPEDITED"
        result.lessonTrail() == [
                "handler:prepared-shipment",
                "handler:selected-expedited",
                "handler:dispatched-shipment"
        ]

        and:
        // The wire tap sent a copy to the observation flow.
        observationRecorder.records().size() == 1
        observationRecorder.records().first().shipmentId() == "shipment-1401"
        observationRecorder.records().first().lane() == "EXPEDITED"

        and:
        // Message history comes from Spring Integration's message-history
        // header. The exact generated endpoint names are framework details, so
        // the test checks that lesson 14 history was captured without depending
        // on the full internal name.
        observationRecorder.records().first().messageHistory().any {
            it.contains("lesson14")
        }
    }

    def "control bus can stop and start observation without stopping the shipment flow"() {
        when:
        def stopResult = operationsGateway.operate("lesson14ObservationRecorder.stopObservation")
        def shipmentWhileStopped = shipmentGateway.process(new Lesson14ShipmentEvent(
                "shipment-1402",
                "Austin",
                false
        ))

        then:
        stopResult == "OBSERVATION_STOPPED"
        shipmentWhileStopped.status() == "DISPATCHED"
        shipmentWhileStopped.lane() == "STANDARD"
        observationRecorder.records().isEmpty()

        when:
        def startResult = operationsGateway.operate("lesson14ObservationRecorder.startObservation")
        def shipmentAfterStart = shipmentGateway.process(new Lesson14ShipmentEvent(
                "shipment-1403",
                "Seattle",
                false
        ))

        then:
        startResult == "OBSERVATION_STARTED"
        shipmentAfterStart.status() == "DISPATCHED"
        observationRecorder.records().size() == 1
        observationRecorder.records().first().shipmentId() == "shipment-1403"
    }
}
