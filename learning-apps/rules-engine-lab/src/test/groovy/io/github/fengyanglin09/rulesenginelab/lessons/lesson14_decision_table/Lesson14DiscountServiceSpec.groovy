package io.github.fengyanglin09.rulesenginelab.lessons.lesson14_decision_table

import io.github.fengyanglin09.rulesenginelab.lessons.lesson14_decision_table.model.Lesson14DiscountRequest
import io.github.fengyanglin09.rulesenginelab.lessons.lesson14_decision_table.service.Lesson14DecisionTableLoader
import io.github.fengyanglin09.rulesenginelab.lessons.lesson14_decision_table.service.Lesson14DiscountService
import spock.lang.Specification

class Lesson14DiscountServiceSpec extends Specification {

    Lesson14DiscountService discountService = new Lesson14DiscountService(new Lesson14DecisionTableLoader())

    def "decision-table rows choose the best matching discount"() {
        given:
        Lesson14DiscountRequest request = request(customerType, orderAmountCents)

        when:
        def result = discountService.calculate(request)

        then:
        result.valid
        result.discountPercent == expectedDiscount
        result.reason == expectedReason
        result.matchedDecisionRow == expectedDecisionRow

        where:
        customerType | orderAmountCents || expectedDiscount | expectedReason          | expectedDecisionRow
        "VIP"        | 60_000           || 15               | "Large order over 500" | "Large order"
        "VIP"        | 15_000           || 10               | "VIP customer over 100"| "VIP customer"
        "NORMAL"     | 25_000           || 5                | "Normal customer over 200" | "Normal customer"
        "GUEST"      | 10_000           || 0                | "No decision row matched" | null
    }

    def "loader turns CSV rows into decision row facts"() {
        when:
        def rows = new Lesson14DecisionTableLoader().loadRows()

        then:
        rows*.rowName == ["Large order", "VIP customer", "Normal customer"]
        rows*.discountPercent == [15, 10, 5]
    }

    private static Lesson14DiscountRequest request(String customerType, int orderAmountCents) {
        Lesson14DiscountRequest request = new Lesson14DiscountRequest()
        request.customerType = customerType
        request.orderAmountCents = orderAmountCents
        request
    }
}
