package io.github.fengyanglin09.springintegrationlab.lessons.lesson01_why_spring_integration

import io.github.fengyanglin09.springintegrationlab.lessons.lesson01_why_spring_integration.gateway.Lesson01OrderGateway
import io.github.fengyanglin09.springintegrationlab.lessons.lesson01_why_spring_integration.model.Lesson01OrderRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

// @SpringBootTest starts the real Spring application context for the test.
// That means this test uses the actual gateway bean, actual channel, actual
// IntegrationFlow, and actual handler.
@SpringBootTest
// @ActiveProfiles("test") activates the test profile.
// Spring then loads test-profile configuration such as application-test.yml.
@ActiveProfiles("test")
class Lesson01WhySpringIntegrationSpec extends Specification {

    // @Autowired asks Spring for a Lesson01OrderGateway bean.
    // The bean is the gateway proxy that Spring Integration generated from the interface.
    @Autowired
    Lesson01OrderGateway orderGateway

    def "VIP order enters through the gateway and returns from the flow"() {
        given:
        def request = new Lesson01OrderRequest("order-1001", "VIP", new BigDecimal("150.00"))

        when:
        // This looks like a plain Java method call, but it exercises the whole path:
        //
        // orderGateway.route(...)
        //   -> channel named lesson01OrderRequests
        //   -> IntegrationFlow
        //   -> Lesson01OrderHandler.route(...)
        //   -> handler returns Lesson01OrderResult
        //   -> gateway returns that same result here
        def result = orderGateway.route(request)

        then:
        result.orderId() == "order-1001"
        result.route() == "priority-billing"
        result.message() == "VIP order routed through the lesson 01 integration flow"
    }

    def "standard order uses the same flow with a different business result"() {
        given:
        def request = new Lesson01OrderRequest("order-1002", "STANDARD", new BigDecimal("75.00"))

        when:
        def result = orderGateway.route(request)

        then:
        // This uses the same gateway, channel, flow, and handler.
        // Only the request payload is different, so the business result changes.
        result.orderId() == "order-1002"
        result.route() == "standard-billing"
        result.message() == "Standard order routed through the lesson 01 integration flow"
    }
}
