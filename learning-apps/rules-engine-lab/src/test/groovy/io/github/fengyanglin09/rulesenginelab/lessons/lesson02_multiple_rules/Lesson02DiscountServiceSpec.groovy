package io.github.fengyanglin09.rulesenginelab.lessons.lesson02_multiple_rules

import io.github.fengyanglin09.rulesenginelab.lessons.lesson02_multiple_rules.model.Lesson02DiscountRequest
import io.github.fengyanglin09.rulesenginelab.lessons.lesson02_multiple_rules.service.Lesson02DiscountService
import spock.lang.Specification
import spock.lang.Unroll

class Lesson02DiscountServiceSpec extends Specification {

    Lesson02DiscountService discountService = new Lesson02DiscountService()

    @Unroll
    def "calculates lesson 02 discount for customerType=#customerType orderAmount=#orderAmount"() {
        given:
        Lesson02DiscountRequest request = new Lesson02DiscountRequest()
        request.customerType = customerType
        request.orderAmount = new BigDecimal(orderAmount)

        when:
        def result = discountService.calculate(request)

        then:
        result.discountPercent == expectedDiscount
        result.reason == expectedReason

        where:
        // In a Spock data table, || is only a visual divider.
        // Left side = test inputs. Right side = expected outputs.
        customerType | orderAmount || expectedDiscount | expectedReason
        "VIP"        | "150"       || 10               | "VIP customer"
        "NORMAL"     | "150"       || 5                | "Normal customer"
        "GUEST"      | "600"       || 15               | "Large guest order"
        "GUEST"      | "100"       || 0                | null
    }
}
