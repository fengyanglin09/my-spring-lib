package io.github.fengyanglin09.spalibdemo.rulesEngineDemo

import io.github.fengyanglin09.spalibdemo.rulesEngineDemo.config.CustomerType
import io.github.fengyanglin09.spalibdemo.rulesEngineDemo.config.DiscountType
import io.github.fengyanglin09.spalibdemo.rulesEngineDemo.model.DiscountRequest
import io.github.fengyanglin09.spalibdemo.rulesEngineDemo.model.DiscountResult
import org.kie.api.KieServices
import org.kie.api.runtime.KieContainer
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class DroolsDiscountServiceSpec extends Specification{

    @Shared
    DroolsDiscountService droolsDiscountService;

    def setupSpec() {
        KieContainer kieContainer = KieServices.Factory.get().getKieClasspathContainer()
        droolsDiscountService = new DroolsDiscountService(kieContainer)
    }

    @Unroll
    def "calculate discount for customerType=#customerType discountType=#discountType amount=#amount" () {
        when:
            DiscountResult result = calculate(customerType, discountType, amount)

        then:
            result.valid == expectedValid
            result.discountPercent == expectedPercent
            result.reason == expectedReason

        where:
            customerType        | discountType             | amount || expectedValid | expectedPercent | expectedReason
            CustomerType.VIP    | DiscountType.AUTO        | "600"  || true          | 20              | "Large order over 500"
            CustomerType.VIP    | DiscountType.VIP_ONLY    | "600"  || true          | 15              | "VIP customer with order over 100"
            CustomerType.NORMAL | DiscountType.AUTO        | "250"  || true          | 8               | "Normal customer with order over 200"
            CustomerType.NORMAL | DiscountType.AUTO        | "50"   || true          | 0               | "No discount rule matched"
            null                | DiscountType.AUTO        | "600"  || false         | 0               | "Customer type is required"
            CustomerType.VIP    | null                     | "600"  || false         | 0               | "Unsupported discount type"
    }

    private DiscountResult calculate(CustomerType customerType, DiscountType discountType, String amount) {
        DiscountRequest request = new DiscountRequest()
        request.customerType = customerType
        request.discountType = discountType
        request.orderAmount = new BigDecimal(amount)

        return droolsDiscountService.calculateDiscount(request)
    }

}
