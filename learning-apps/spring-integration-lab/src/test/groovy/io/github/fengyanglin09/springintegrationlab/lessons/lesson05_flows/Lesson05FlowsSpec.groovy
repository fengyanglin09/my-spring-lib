package io.github.fengyanglin09.springintegrationlab.lessons.lesson05_flows

import io.github.fengyanglin09.springintegrationlab.lessons.lesson05_flows.gateway.Lesson05FlowGateway
import io.github.fengyanglin09.springintegrationlab.lessons.lesson05_flows.model.Lesson05OrderRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest
@ActiveProfiles("test")
class Lesson05FlowsSpec extends Specification {

    @Autowired
    Lesson05FlowGateway flowGateway

    def "flow reads top to bottom from raw request to final result"() {
        given:
        def request = new Lesson05OrderRequest("order-5001", " vip ", new BigDecimal("250.00"), true)

        when:
        def result = flowGateway.prepare(request)

        then:
        result.orderId() == "order-5001"
        result.customerType() == "VIP"
        result.orderAmount() == new BigDecimal("250.00")
        result.handlingLane() == "EXPEDITED"
        result.flowSteps() == [
                "1 normalize raw order",
                "2 assign handling lane",
                "3 build final result"
        ]
    }

    def "business decisions stay in named handler methods instead of the flow wiring"() {
        given:
        def request = new Lesson05OrderRequest("order-5002", "standard", new BigDecimal("1200.00"), false)

        when:
        def result = flowGateway.prepare(request)

        then:
        result.customerType() == "STANDARD"
        result.handlingLane() == "REVIEW"
        result.flowSteps() == [
                "1 normalize raw order",
                "2 assign handling lane",
                "3 build final result"
        ]
    }
}
