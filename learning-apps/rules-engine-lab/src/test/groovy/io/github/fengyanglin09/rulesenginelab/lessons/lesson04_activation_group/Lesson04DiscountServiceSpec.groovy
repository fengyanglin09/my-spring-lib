package io.github.fengyanglin09.rulesenginelab.lessons.lesson04_activation_group

import io.github.fengyanglin09.rulesenginelab.lessons.lesson04_activation_group.model.Lesson04DiscountRequest
import io.github.fengyanglin09.rulesenginelab.lessons.lesson04_activation_group.service.Lesson04DiscountService
import spock.lang.Specification

class Lesson04DiscountServiceSpec extends Specification {

    Lesson04DiscountService discountService = new Lesson04DiscountService()

    def "VIP large order fires only the large order winner rule"() {
        given:
        Lesson04DiscountRequest request = new Lesson04DiscountRequest()
        request.customerType = "VIP"
        request.orderAmount = new BigDecimal("600")

        when:
        def result = discountService.calculate(request)

        then:
        result.firedRules == ["Large order discount"]
        result.discountPercent == 15
        result.reason == "Large order"
    }

    def "VIP small order fires only the VIP rule"() {
        given:
        Lesson04DiscountRequest request = new Lesson04DiscountRequest()
        request.customerType = "VIP"
        request.orderAmount = new BigDecimal("150")

        when:
        def result = discountService.calculate(request)

        then:
        result.firedRules == ["VIP discount"]
        result.discountPercent == 10
        result.reason == "VIP customer"
    }

    def "normal small order fires only the normal rule"() {
        given:
        Lesson04DiscountRequest request = new Lesson04DiscountRequest()
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
