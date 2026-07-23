package io.github.fengyanglin09.rulesenginelab.lessons.lesson12_candidate_selection

import io.github.fengyanglin09.rulesenginelab.lessons.lesson12_candidate_selection.model.Lesson12DiscountRequest
import io.github.fengyanglin09.rulesenginelab.lessons.lesson12_candidate_selection.service.Lesson12DiscountService
import spock.lang.Specification

class Lesson12DiscountServiceSpec extends Specification {

    Lesson12DiscountService discountService = new Lesson12DiscountService()

    def "VIP large order creates multiple candidates and selects the highest percent"() {
        given:
        Lesson12DiscountRequest request = request("VIP", "600")

        when:
        def result = discountService.calculate(request)

        then:
        result.valid
        result.candidates*.ruleName as Set == ["Large order candidate", "VIP candidate"] as Set

        and:
        result.selectedCandidate.ruleName == "Large order candidate"
        result.discountPercent == 15
        result.reason == "Large order"
    }

    def "normal order selects the only candidate"() {
        given:
        Lesson12DiscountRequest request = request("NORMAL", "250")

        when:
        def result = discountService.calculate(request)

        then:
        result.valid
        result.candidates.size() == 1
        result.selectedCandidate.ruleName == "Normal customer candidate"
        result.discountPercent == 5
        result.reason == "Normal customer"
    }

    def "valid request with no candidates returns a no match reason"() {
        given:
        Lesson12DiscountRequest request = request("GUEST", "100")

        when:
        def result = discountService.calculate(request)

        then:
        result.valid
        result.candidates == []
        result.selectedCandidate == null
        result.discountPercent == 0
        result.reason == "No discount candidate matched"
    }

    def "invalid request does not select candidates"() {
        given:
        Lesson12DiscountRequest request = request("VIP", null)

        when:
        def result = discountService.calculate(request)

        then:
        !result.valid
        result.validationErrors == ["orderAmount is required"]
        result.firedRules == ["Missing order amount"]
        result.candidates == []
        result.selectedCandidate == null
        result.discountPercent == 0
        result.reason == "Invalid request"
    }

    private static Lesson12DiscountRequest request(String customerType, String orderAmount) {
        Lesson12DiscountRequest request = new Lesson12DiscountRequest()
        request.customerType = customerType
        request.orderAmount = orderAmount == null ? null : new BigDecimal(orderAmount)
        request
    }
}
