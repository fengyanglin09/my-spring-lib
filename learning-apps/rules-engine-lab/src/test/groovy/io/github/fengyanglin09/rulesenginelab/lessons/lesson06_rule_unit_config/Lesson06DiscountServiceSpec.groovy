package io.github.fengyanglin09.rulesenginelab.lessons.lesson06_rule_unit_config

import io.github.fengyanglin09.rulesenginelab.lessons.lesson06_rule_unit_config.model.Lesson06DiscountConfig
import io.github.fengyanglin09.rulesenginelab.lessons.lesson06_rule_unit_config.model.Lesson06DiscountRequest
import io.github.fengyanglin09.rulesenginelab.lessons.lesson06_rule_unit_config.service.Lesson06DiscountService
import spock.lang.Specification

class Lesson06DiscountServiceSpec extends Specification {

    def "default config drives the large order discount"() {
        given:
        Lesson06DiscountService discountService = new Lesson06DiscountService()
        Lesson06DiscountRequest request = request("VIP", "600")

        when:
        def result = discountService.calculate(request)

        then:
        result.valid
        result.firedRules == ["Large order discount"]
        result.discountPercent == 15
        result.reason == "Large order"
    }

    def "custom config changes which rule wins and what percent is returned"() {
        given:
        Lesson06DiscountConfig config = new Lesson06DiscountConfig(
                25,
                12,
                7,
                new BigDecimal("700"),
                new BigDecimal("100"),
                new BigDecimal("200")
        )
        Lesson06DiscountService discountService = new Lesson06DiscountService(config)
        Lesson06DiscountRequest request = request("VIP", "600")

        when:
        def result = discountService.calculate(request)

        then:
        result.valid
        result.firedRules == ["VIP discount"]
        result.discountPercent == 12
        result.reason == "VIP customer"
    }

    def "validation still protects discount rules before config thresholds are used"() {
        given:
        Lesson06DiscountService discountService = new Lesson06DiscountService()
        Lesson06DiscountRequest request = request("VIP", null)

        when:
        def result = discountService.calculate(request)

        then:
        !result.valid
        result.validationErrors == ["orderAmount is required"]
        result.firedRules == ["Missing order amount"]
        result.discountPercent == 0
        result.reason == "Invalid request"
    }

    private static Lesson06DiscountRequest request(String customerType, String orderAmount) {
        Lesson06DiscountRequest request = new Lesson06DiscountRequest()
        request.customerType = customerType
        request.orderAmount = orderAmount == null ? null : new BigDecimal(orderAmount)
        request
    }
}
