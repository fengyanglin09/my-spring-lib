package io.github.fengyanglin09.rulesenginelab.lessons.lesson05_validation_rules

import io.github.fengyanglin09.rulesenginelab.lessons.lesson05_validation_rules.model.Lesson05DiscountRequest
import io.github.fengyanglin09.rulesenginelab.lessons.lesson05_validation_rules.service.Lesson05DiscountService
import spock.lang.Specification

class Lesson05DiscountServiceSpec extends Specification {

    Lesson05DiscountService discountService = new Lesson05DiscountService()

    def "valid VIP large order gets the large order discount"() {
        given:
        Lesson05DiscountRequest request = request("VIP", "600")

        when:
        def result = discountService.calculate(request)

        then:
        result.valid
        result.validationErrors == []
        result.firedRules == ["Large order discount"]
        result.discountPercent == 15
        result.reason == "Large order"
    }

    def "missing customer type is invalid and no discount rule fires"() {
        given:
        Lesson05DiscountRequest request = request(null, "600")

        when:
        def result = discountService.calculate(request)

        then:
        !result.valid
        result.validationErrors == ["customerType is required"]
        result.firedRules == ["Missing customer type"]
        result.discountPercent == 0
        result.reason == "Invalid request"
    }

    def "missing order amount is invalid and avoids compareTo null failure"() {
        given:
        Lesson05DiscountRequest request = request("VIP", null)

        when:
        def result = discountService.calculate(request)

        then:
        !result.valid
        result.validationErrors == ["orderAmount is required"]
        result.firedRules == ["Missing order amount"]
        result.discountPercent == 0
        result.reason == "Invalid request"
    }

    def "negative order amount is invalid"() {
        given:
        Lesson05DiscountRequest request = request("VIP", "-1")

        when:
        def result = discountService.calculate(request)

        then:
        !result.valid
        result.validationErrors == ["orderAmount must not be negative"]
        result.firedRules == ["Negative order amount"]
        result.discountPercent == 0
        result.reason == "Invalid request"
    }

    private static Lesson05DiscountRequest request(String customerType, String orderAmount) {
        Lesson05DiscountRequest request = new Lesson05DiscountRequest()
        request.customerType = customerType
        request.orderAmount = orderAmount == null ? null : new BigDecimal(orderAmount)
        request
    }
}
