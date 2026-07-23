package io.github.fengyanglin09.rulesenginelab.lessons.lesson13_accumulate

import io.github.fengyanglin09.rulesenginelab.lessons.lesson13_accumulate.model.Lesson13CartItem
import io.github.fengyanglin09.rulesenginelab.lessons.lesson13_accumulate.model.Lesson13DiscountRequest
import io.github.fengyanglin09.rulesenginelab.lessons.lesson13_accumulate.service.Lesson13DiscountService
import spock.lang.Specification

class Lesson13DiscountServiceSpec extends Specification {

    Lesson13DiscountService discountService = new Lesson13DiscountService()

    def "accumulate calculates total from multiple cart item facts"() {
        given:
        Lesson13DiscountRequest request = request("GUEST",
                item("BOOK", 12_00, 2),
                item("BAG", 30_00, 1)
        )

        when:
        def result = discountService.calculate(request)

        then:
        result.valid
        result.totalCalculated
        result.totalCents == 54_00
        result.discountPercent == 0
        result.reason == "No cart discount matched"
        result.firedRules == ["Calculate cart total", "No cart discount"]
    }

    def "large cart discount uses accumulated total"() {
        given:
        Lesson13DiscountRequest request = request("GUEST",
                item("DESK", 30_000, 1),
                item("CHAIR", 25_000, 1)
        )

        when:
        def result = discountService.calculate(request)

        then:
        result.valid
        result.totalCents == 55_000
        result.discountPercent == 15
        result.reason == "Large cart total"
        result.firedRules == ["Calculate cart total", "Large cart discount"]
    }

    def "VIP cart below large threshold gets VIP discount"() {
        given:
        Lesson13DiscountRequest request = request("VIP",
                item("BOOK", 10_00, 2)
        )

        when:
        def result = discountService.calculate(request)

        then:
        result.valid
        result.totalCents == 20_00
        result.discountPercent == 10
        result.reason == "VIP cart customer"
        result.firedRules == ["Calculate cart total", "VIP cart discount"]
    }

    def "empty cart is invalid and does not calculate total"() {
        given:
        Lesson13DiscountRequest request = request("VIP")

        when:
        def result = discountService.calculate(request)

        then:
        !result.valid
        !result.totalCalculated
        result.validationErrors == ["at least one cart item is required"]
        result.firedRules == ["Missing cart items"]
    }

    private static Lesson13DiscountRequest request(String customerType, Lesson13CartItem... items) {
        Lesson13DiscountRequest request = new Lesson13DiscountRequest()
        request.customerType = customerType
        request.items = items.toList()
        request
    }

    private static Lesson13CartItem item(String sku, int unitPriceCents, int quantity) {
        Lesson13CartItem item = new Lesson13CartItem()
        item.sku = sku
        item.unitPriceCents = unitPriceCents
        item.quantity = quantity
        item
    }
}
