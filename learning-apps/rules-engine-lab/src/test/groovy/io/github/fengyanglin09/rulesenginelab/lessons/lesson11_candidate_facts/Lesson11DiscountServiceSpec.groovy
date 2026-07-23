package io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts

import io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.model.Lesson11DiscountRequest
import io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.service.Lesson11DiscountService
import spock.lang.Specification

class Lesson11DiscountServiceSpec extends Specification {

    Lesson11DiscountService discountService = new Lesson11DiscountService()

    def "VIP large order creates multiple candidate facts"() {
        given:
        Lesson11DiscountRequest request = request("VIP", "600")

        when:
        def result = discountService.calculate(request)

        then:
        result.valid
        result.reason == null
        result.firedRules.contains("Large order candidate")
        result.firedRules.contains("VIP candidate")

        and:
        result.candidates*.ruleName as Set == ["Large order candidate", "VIP candidate"] as Set
        result.candidates*.discountPercent as Set == [15, 10] as Set
        result.candidates*.reason as Set == ["Large order", "VIP customer"] as Set
    }

    def "normal order creates one normal customer candidate"() {
        given:
        Lesson11DiscountRequest request = request("NORMAL", "250")

        when:
        def result = discountService.calculate(request)

        then:
        result.valid
        result.candidates.size() == 1
        result.candidates[0].ruleName == "Normal customer candidate"
        result.candidates[0].discountPercent == 5
        result.candidates[0].reason == "Normal customer"
    }

    def "invalid request creates no discount candidates"() {
        given:
        Lesson11DiscountRequest request = request("VIP", null)

        when:
        def result = discountService.calculate(request)

        then:
        !result.valid
        result.validationErrors == ["orderAmount is required"]
        result.firedRules == ["Missing order amount"]
        result.candidates == []
    }

    private static Lesson11DiscountRequest request(String customerType, String orderAmount) {
        Lesson11DiscountRequest request = new Lesson11DiscountRequest()
        request.customerType = customerType
        request.orderAmount = orderAmount == null ? null : new BigDecimal(orderAmount)
        request
    }
}
