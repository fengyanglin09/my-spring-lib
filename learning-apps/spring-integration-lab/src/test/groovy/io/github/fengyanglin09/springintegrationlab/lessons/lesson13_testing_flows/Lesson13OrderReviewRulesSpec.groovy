package io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows

import io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows.handler.Lesson13OrderReviewRules
import io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows.model.Lesson13OrderDraft
import io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows.model.Lesson13ReviewDecision
import io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows.model.Lesson13ReviewedOrder
import spock.lang.Specification

// This spec intentionally has no @SpringBootTest annotation.
//
// That means Spock runs it as a plain Groovy/JVM test. Spring does not create
// channels, gateways, or IntegrationFlow beans for this spec.
class Lesson13OrderReviewRulesSpec extends Specification {

    def rules = new Lesson13OrderReviewRules()

    def "component test classifies a high value verified order without Spring Integration"() {
        given:
        // This is a component test.
        //
        // There is no @SpringBootTest annotation on this spec, so Spock does
        // not start the Spring application context. We are testing one ordinary
        // Java object directly.
        def draft = new Lesson13OrderDraft(
                "order-1301",
                " vip ",
                new BigDecimal("750.00"),
                true
        )

        when:
        def reviewedOrder = rules.normalize(draft)

        then:
        reviewedOrder.normalizedCustomerType() == "VIP"
        reviewedOrder.decision() == Lesson13ReviewDecision.MANUAL_REVIEW
        reviewedOrder.reviewTrail() == [
                "handler:normalized-order",
                "handler:selected-manual-review"
        ]
    }

    def "component test verifies one branch result directly"() {
        given:
        // This test starts after the transform step on purpose.
        //
        // That makes the test smaller: it focuses only on the manual-review
        // branch method and does not care about channels, gateways, or routing.
        def reviewedOrder = new Lesson13ReviewedOrder(
                "order-1302",
                "STANDARD",
                new BigDecimal("900.00"),
                Lesson13ReviewDecision.MANUAL_REVIEW,
                ["handler:normalized-order", "handler:selected-manual-review"]
        )

        when:
        def result = rules.requestManualReview(reviewedOrder)

        then:
        !result.accepted()
        result.outcome() == "MANUAL_REVIEW"
        result.reviewTrail() == [
                "handler:normalized-order",
                "handler:selected-manual-review",
                "router:manual-review-branch"
        ]
    }
}
