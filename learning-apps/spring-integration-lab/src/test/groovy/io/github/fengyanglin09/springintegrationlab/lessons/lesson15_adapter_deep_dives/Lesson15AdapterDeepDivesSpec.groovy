package io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives

import io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.model.Lesson15CourierInboundFrame
import io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.support.Lesson15AdapterIds
import io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.support.Lesson15CourierProtocolSandbox
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.integration.endpoint.SourcePollingChannelAdapter
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

// @SpringBootTest tells Spring Boot:
// "Start the application context for this spec."
//
// This test needs the real lesson 15 inbound adapter, flow, mapper, and
// outbound adapter beans.
@SpringBootTest
// @ActiveProfiles("test") tells Spring:
// "Load test-profile configuration from application-test.yml."
@ActiveProfiles("test")
class Lesson15AdapterDeepDivesSpec extends Specification {

    @Autowired
    ApplicationContext applicationContext

    @Autowired
    Lesson15CourierProtocolSandbox courierProtocolSandbox

    SourcePollingChannelAdapter inboundAdapter

    // Spock automatically runs setup() before each test method in this class.
    //
    // In Spock vocabulary, each test method is often called a "feature method."
    def setup() {
        // The inbound adapter was given a stable id in the flow:
        // Lesson15AdapterIds.INBOUND_ADAPTER.
        //
        // We fetch it from the Spring context so the test can start and stop
        // polling on purpose.
        inboundAdapter = applicationContext.getBean(
                Lesson15AdapterIds.INBOUND_ADAPTER,
                SourcePollingChannelAdapter
        )

        // Each test starts from a clean state:
        //
        // - inbound adapter stopped
        // - fake external inbound queue empty
        // - fake external outbound records empty
        //
        // This keeps one test from accidentally using messages left behind by
        // another test.
        inboundAdapter.stop()
        courierProtocolSandbox.clear()
    }

    // Spock automatically runs cleanup() after each test method in this class.
    //
    // cleanup() runs whether the test passes or fails, so it is a good place to
    // stop background polling work.
    def cleanup() {
        inboundAdapter.stop()
    }

    def "inbound adapter maps external envelope to payload and headers for outbound adapter"() {
        given:
        // This object represents something outside Spring Integration.
        //
        // Think of it like one record read from a protocol such as SFTP, JMS,
        // Kafka, TCP, or HTTP.
        //
        // The important detail is that the external envelope has two kinds of
        // information:
        //
        // - body: business data the flow will transform
        // - frameId, remoteSystem, contentType: metadata the outbound side will
        //   need later
        courierProtocolSandbox.submitInboundFrame(new Lesson15CourierInboundFrame(
                "frame-1501",
                "courier-alpha",
                "text/courier-shipment",
                "shipment-1501|Dallas|EXP"
        ))

        when:
        // Starting the inbound adapter lets its poller call
        // Lesson15CourierFrameSource.receive().
        //
        // If receive() returns a Message, the flow runs.
        // If receive() returns null, there is no message for that poll.
        inboundAdapter.start()

        then:
        // The adapter poller runs on a Spring scheduler thread, not directly on
        // the test thread.
        //
        // PollingConditions repeatedly checks the assertion until it passes or
        // the timeout expires.
        //
        // timeout: 2 means:
        // "Keep retrying for up to 2 seconds."
        //
        // If one outbound frame appears within those 2 seconds, eventually
        // passes and the test continues immediately.
        //
        // If no outbound frame appears after 2 seconds, eventually fails the
        // test.
        new PollingConditions(timeout: 2).eventually {
            assert courierProtocolSandbox.outboundFrames().size() == 1
        }

        and:
        def outboundFrame = courierProtocolSandbox.outboundFrames().first()

        // The outbound adapter created a new external frame id for the reply.
        outboundFrame.frameId() == "ack-frame-1501"

        // The outbound adapter copied the original inbound frame id into a
        // correlation field.
        //
        // This proves the original frame id survived as a message header while
        // the payload changed from:
        //
        // Lesson15CourierInboundFrame
        // -> Lesson15ShipmentCommand
        // -> Lesson15PartnerExport
        outboundFrame.correlationFrameId() == "frame-1501"

        // These values also came from headers created by the inbound adapter.
        outboundFrame.remoteSystem() == "courier-alpha"
        outboundFrame.contentType() == "text/courier-shipment"

        // The body came from the transformed payload, not directly from the
        // original header values.
        outboundFrame.body() == "ACK|shipment-1501|EXPEDITED"

        // adapterTrail is lesson-only teaching data.
        //
        // It lets the test prove the message crossed the expected steps in the
        // expected order.
        outboundFrame.adapterTrail() == [
                "inbound-adapter:external-frame-to-message",
                "transform:frame-body-to-internal-command",
                "transform:internal-command-to-outbound-export",
                "outbound-adapter:message-to-external-frame"
        ]
    }

    def "inbound adapter creates no message when external source has no frame"() {
        when:
        inboundAdapter.start()

        then:
        // The sandbox has no inbound frame.
        //
        // That means Lesson15CourierFrameSource.receive() returns null when the
        // poller asks for data.
        //
        // For an inbound adapter, null means:
        // "No message is available right now."
        //
        // Since no message is created, the flow has nothing to transform and
        // the outbound adapter has nothing to send.
        Thread.sleep(100)
        courierProtocolSandbox.outboundFrames().isEmpty()
    }
}
