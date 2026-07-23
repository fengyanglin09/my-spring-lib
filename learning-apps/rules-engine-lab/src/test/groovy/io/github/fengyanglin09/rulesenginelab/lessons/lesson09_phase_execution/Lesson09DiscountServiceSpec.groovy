package io.github.fengyanglin09.rulesenginelab.lessons.lesson09_phase_execution

import io.github.fengyanglin09.rulesenginelab.lessons.lesson09_phase_execution.model.Lesson09DiscountRequest
import io.github.fengyanglin09.rulesenginelab.lessons.lesson09_phase_execution.service.Lesson09DiscountService
import spock.lang.Specification

class Lesson09DiscountServiceSpec extends Specification {

    Lesson09DiscountService discountService = new Lesson09DiscountService()

    def "valid request runs validation phase then discount phase"() {
        given:
        Lesson09DiscountRequest request = request("VIP", "600")

        when:
        def result = discountService.calculate(request)

        then:
        result.valid
        result.discountPercent == 15
        result.reason == "Large order"

        and:
        result.matchedRules.contains("Large order discount")
        result.matchedRules.contains("VIP discount")
        result.firedRules == ["Large order discount"]
    }

    def "invalid request stops after validation phase"() {
        given:
        Lesson09DiscountRequest request = request("VIP", null)

        when:
        def result = discountService.calculate(request)

        then:
        !result.valid
        result.validationErrors == ["orderAmount is required"]
        result.reason == "Invalid request"
        result.discountPercent == 0

        and:
        result.firedRules == ["Missing order amount"]
        !result.matchedRules.contains("VIP discount")
        !result.matchedRules.contains("Large order discount")
    }

    private static Lesson09DiscountRequest request(String customerType, String orderAmount) {
        Lesson09DiscountRequest request = new Lesson09DiscountRequest()
        request.customerType = customerType
        request.orderAmount = orderAmount == null ? null : new BigDecimal(orderAmount)
        request
    }
}
