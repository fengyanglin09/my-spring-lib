package io.github.fengyanglin09.rulesenginelab.lessons.lesson10_queries

import io.github.fengyanglin09.rulesenginelab.lessons.lesson10_queries.model.Lesson10DiscountRequest
import io.github.fengyanglin09.rulesenginelab.lessons.lesson10_queries.service.Lesson10DiscountService
import spock.lang.Specification

class Lesson10DiscountServiceSpec extends Specification {

    Lesson10DiscountService discountService = new Lesson10DiscountService()

    def "query returns audit fact created by winning discount rule"() {
        given:
        Lesson10DiscountRequest request = request("VIP", "600")

        when:
        def result = discountService.calculate(request)

        then:
        result.valid
        result.discountPercent == 15
        result.reason == "Large order"
        result.firedRules == ["Large order discount"]

        and:
        result.auditMessages == ["Applied large order discount"]
    }

    def "query returns audit fact created by validation rule"() {
        given:
        Lesson10DiscountRequest request = request("VIP", null)

        when:
        def result = discountService.calculate(request)

        then:
        !result.valid
        result.validationErrors == ["orderAmount is required"]
        result.firedRules == ["Missing order amount"]

        and:
        result.auditMessages == ["Validation failed: orderAmount is required"]
    }

    private static Lesson10DiscountRequest request(String customerType, String orderAmount) {
        Lesson10DiscountRequest request = new Lesson10DiscountRequest()
        request.customerType = customerType
        request.orderAmount = orderAmount == null ? null : new BigDecimal(orderAmount)
        request
    }
}
