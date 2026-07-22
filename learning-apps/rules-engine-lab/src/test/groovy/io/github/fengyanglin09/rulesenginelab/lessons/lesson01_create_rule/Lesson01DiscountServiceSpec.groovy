package io.github.fengyanglin09.rulesenginelab.lessons.lesson01_create_rule

import io.github.fengyanglin09.rulesenginelab.lessons.lesson01_create_rule.model.Lesson01DiscountRequest
import io.github.fengyanglin09.rulesenginelab.lessons.lesson01_create_rule.service.Lesson01DiscountService
import spock.lang.Specification

class Lesson01DiscountServiceSpec extends Specification {

    Lesson01DiscountService discountService = new Lesson01DiscountService()

    def "VIP customer gets the first lesson discount"() {
        given:
        Lesson01DiscountRequest request = new Lesson01DiscountRequest()
        request.customerType = "VIP"
        request.orderAmount = new BigDecimal("150")

        when:
        def result = discountService.calculate(request)

        then:
        result.discountPercent == 10
        result.reason == "VIP customer"
    }

    def "non VIP customer does not match the only rule"() {
        given:
        Lesson01DiscountRequest request = new Lesson01DiscountRequest()
        request.customerType = "NORMAL"
        request.orderAmount = new BigDecimal("150")

        when:
        def result = discountService.calculate(request)

        then:
        result.discountPercent == 0
        result.reason == null
    }
}
