package io.github.fengyanglin09.rulesenginelab.examples.advanced_discount_rules

import io.github.fengyanglin09.rulesenginelab.common.config.DroolsDebugProperties
import io.github.fengyanglin09.rulesenginelab.common.config.component.DroolsRuleConfigFactory
import io.github.fengyanglin09.rulesenginelab.examples.advanced_discount_rules.model.DiscountConfig
import io.github.fengyanglin09.rulesenginelab.examples.advanced_discount_rules.model.CustomerType
import io.github.fengyanglin09.rulesenginelab.examples.advanced_discount_rules.model.DiscountMode
import io.github.fengyanglin09.rulesenginelab.examples.advanced_discount_rules.model.DiscountRequest
import io.github.fengyanglin09.rulesenginelab.examples.advanced_discount_rules.model.DiscountResult
import io.github.fengyanglin09.rulesenginelab.examples.advanced_discount_rules.service.AdvancedDiscountService
import spock.lang.Specification
import spock.lang.Unroll

class AdvancedDiscountServiceSpec extends Specification {

    AdvancedDiscountService discountService = new AdvancedDiscountService(
            new DroolsRuleConfigFactory(debugProperties()),
            new DiscountConfig(
                    30,
                    25,
                    10,
                    new BigDecimal("500"),
                    new BigDecimal("100"),
                    new BigDecimal("200")
            )
    )

    @Unroll
    def "calculates discount for customerType=#customerType discountMode=#discountMode orderAmount=#orderAmount"() {
        when:
        DiscountResult result = discountService.calculate(request(customerType, discountMode, orderAmount))

        then:
        result.valid == expectedValid
        result.discountPercent == expectedPercent
        result.reason == expectedReason

        where:
        customerType         | discountMode                  | orderAmount || expectedValid | expectedPercent | expectedReason
        CustomerType.VIP    | DiscountMode.AUTO             | "600"       || true          | 30              | "Large order over 500"
        CustomerType.VIP    | DiscountMode.VIP_ONLY         | "600"       || true          | 25              | "VIP customer with order over 100"
        CustomerType.NORMAL | DiscountMode.AUTO             | "250"       || true          | 10               | "Normal customer with order over 200"
        CustomerType.NORMAL | DiscountMode.AUTO             | "50"        || true          | 0               | "No discount rule matched"
        null                | DiscountMode.AUTO             | "600"       || false         | 0               | "Customer type is required"
        CustomerType.VIP    | null                          | "600"       || false         | 0               | "Discount mode is required"
        CustomerType.VIP    | DiscountMode.AUTO             | null        || false         | 0               | "Order amount is required"
    }

    private static DiscountRequest request(CustomerType customerType, DiscountMode discountMode, String orderAmount) {
        DiscountRequest request = new DiscountRequest()
        request.customerType = customerType
        request.discountMode = discountMode

        if (orderAmount != null) {
            request.orderAmount = new BigDecimal(orderAmount)
        }

        return request
    }

    private static DroolsDebugProperties debugProperties() {
        DroolsDebugProperties properties = new DroolsDebugProperties()
        properties.traceEnabled = true
        return properties
    }

}
