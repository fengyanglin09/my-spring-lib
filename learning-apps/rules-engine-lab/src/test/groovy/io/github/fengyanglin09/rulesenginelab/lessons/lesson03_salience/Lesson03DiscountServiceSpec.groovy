package io.github.fengyanglin09.rulesenginelab.lessons.lesson03_salience

import io.github.fengyanglin09.rulesenginelab.lessons.lesson03_salience.model.Lesson03DiscountRequest
import io.github.fengyanglin09.rulesenginelab.lessons.lesson03_salience.service.Lesson03DiscountService
import spock.lang.Specification

class Lesson03DiscountServiceSpec extends Specification {

    Lesson03DiscountService discountService = new Lesson03DiscountService()

    def "VIP large order fires VIP first because it has higher salience"() {
        given:
        Lesson03DiscountRequest request = new Lesson03DiscountRequest()
        request.customerType = "VIP"
        request.orderAmount = new BigDecimal("600")

        when:
        def result = discountService.calculate(request)

        then:
        result.firedRules == ["VIP discount", "Large order discount"]

        and:
        result.discountPercent == 15
        result.reason == "Large order"
    }

    def "VIP small order only fires VIP rule"() {
        given:
        Lesson03DiscountRequest request = new Lesson03DiscountRequest()
        request.customerType = "VIP"
        request.orderAmount = new BigDecimal("150")

        when:
        def result = discountService.calculate(request)

        then:
        result.firedRules == ["VIP discount"]
        result.discountPercent == 10
        result.reason == "VIP customer"
    }

    def "normal small order only fires normal rule"() {
        given:
        Lesson03DiscountRequest request = new Lesson03DiscountRequest()
        request.customerType = "NORMAL"
        request.orderAmount = new BigDecimal("150")

        when:
        def result = discountService.calculate(request)

        then:
        result.firedRules == ["Normal customer discount"]
        result.discountPercent == 5
        result.reason == "Normal customer"
    }
}
