package io.github.fengyanglin09.droolssandbox

import io.github.fengyanglin09.droolssandbox.drools.models.CustomerType
import io.github.fengyanglin09.droolssandbox.drools.models.DiscountMode
import io.github.fengyanglin09.droolssandbox.drools.models.DiscountRequest
import io.github.fengyanglin09.droolssandbox.drools.models.DiscountResult
import io.github.fengyanglin09.droolssandbox.drools.service.DiscountService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification
import spock.lang.Unroll

@SpringBootTest
@ActiveProfiles("test")
class DiscountServiceSpec extends Specification{

    @Autowired
    DiscountService discountService

//    @SpringBean
//    DiscountConfig discountConfig = Stub()
//
//    def setup() {
//        discountConfig.largeOrderPercent >> 30
//        discountConfig.vipPercent >> 25
//        discountConfig.normalPercent >> 10
//
//        discountConfig.largeOrderMinimumAmount >> new BigDecimal("500")
//        discountConfig.vipMinimumAmount >> new BigDecimal("100")
//        discountConfig.normalMinimumAmount >> new BigDecimal("200")
//    }

//    @SpringBean
//    DiscountConfig discountConfig = new DiscountConfig(
//            30,
//            25,
//            10,
//            new BigDecimal("500"),
//            new BigDecimal("100"),
//            new BigDecimal("200")
//    )



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

}
