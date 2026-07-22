package io.github.fengyanglin09.rulesenginelab.lessons.lesson08_rule_tracing

import io.github.fengyanglin09.rulesenginelab.lessons.lesson08_rule_tracing.model.Lesson08DiscountRequest
import io.github.fengyanglin09.rulesenginelab.lessons.lesson08_rule_tracing.service.Lesson08DiscountService
import spock.lang.Specification

class Lesson08DiscountServiceSpec extends Specification {

    Lesson08DiscountService discountService = new Lesson08DiscountService()

    def "listener records matched and fired rules for overlapping discount rules"() {
        given:
        Lesson08DiscountRequest request = request("VIP", "600")

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

    def "listener records validation rule firing"() {
        given:
        Lesson08DiscountRequest request = request("VIP", null)

        when:
        def result = discountService.calculate(request)

        then:
        !result.valid
        result.validationErrors == ["orderAmount is required"]
        result.matchedRules.contains("Missing order amount")
        result.firedRules == ["Missing order amount"]
    }

    private static Lesson08DiscountRequest request(String customerType, String orderAmount) {
        Lesson08DiscountRequest request = new Lesson08DiscountRequest()
        request.customerType = customerType
        request.orderAmount = orderAmount == null ? null : new BigDecimal(orderAmount)
        request
    }
}
