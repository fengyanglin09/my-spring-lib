package io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows

import io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows.gateway.Lesson13OrderReviewGateway
import io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows.model.Lesson13OrderDraft
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.integration.test.context.SpringIntegrationTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

// @SpringBootTest tells Spring Boot:
// "Start the application context for this spec."
//
// That means this test uses the real lesson 13 gateway, channel, flow, router,
// and handler beans.
@SpringBootTest
// @SpringIntegrationTest tells Spring Integration:
// "Enable Spring Integration's test support for this test context."
//
// In this lesson, we still use the real flow. In later or larger tests, this
// annotation is also what lets a test use Spring Integration testing tools such
// as MockIntegrationContext to replace or control endpoints.
@SpringIntegrationTest
// @ActiveProfiles("test") tells Spring:
// "Load test-profile configuration from application-test.yml."
//
// This keeps test configuration separate from normal application configuration.
@ActiveProfiles("test")
class Lesson13TestingFlowsSpec extends Specification {

    @Autowired
    Lesson13OrderReviewGateway orderReviewGateway

    def "flow test verifies approved path through the gateway"() {
        given:
        // This is a full flow test.
        //
        // Calling the gateway sends the message through:
        //
        // gateway -> input channel -> transform -> router -> approved branch
        def draft = new Lesson13OrderDraft(
                "order-1303",
                "standard",
                new BigDecimal("125.00"),
                true
        )

        when:
        def result = orderReviewGateway.review(draft)

        then:
        result.accepted()
        result.outcome() == "APPROVED"
        result.reviewTrail() == [
                "handler:normalized-order",
                "handler:selected-approved",
                "router:approved-branch"
        ]
    }

    def "flow test verifies rejected path through the gateway"() {
        given:
        // An unverified customer should be rejected by the business rules.
        //
        // The test still enters through the gateway so it also proves the router
        // sends REJECTED messages to the rejected branch.
        def draft = new Lesson13OrderDraft(
                "order-1304",
                "standard",
                new BigDecimal("80.00"),
                false
        )

        when:
        def result = orderReviewGateway.review(draft)

        then:
        !result.accepted()
        result.outcome() == "REJECTED"
        result.reviewTrail().last() == "router:rejected-branch"
    }

    def "flow test verifies manual review path through the gateway"() {
        given:
        // A high-value verified order should not be approved automatically.
        //
        // This test checks the observable result of the whole flow instead of
        // trying to inspect every internal Spring Integration endpoint.
        def draft = new Lesson13OrderDraft(
                "order-1305",
                "vip",
                new BigDecimal("1200.00"),
                true
        )

        when:
        def result = orderReviewGateway.review(draft)

        then:
        !result.accepted()
        result.outcome() == "MANUAL_REVIEW"
        result.reviewTrail().last() == "router:manual-review-branch"
    }
}
