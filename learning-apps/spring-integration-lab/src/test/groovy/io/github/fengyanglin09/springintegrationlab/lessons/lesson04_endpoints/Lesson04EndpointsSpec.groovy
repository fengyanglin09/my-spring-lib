package io.github.fengyanglin09.springintegrationlab.lessons.lesson04_endpoints

import io.github.fengyanglin09.springintegrationlab.lessons.lesson04_endpoints.gateway.Lesson04EndpointGateway
import io.github.fengyanglin09.springintegrationlab.lessons.lesson04_endpoints.model.Lesson04OrderRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest
@ActiveProfiles("test")
class Lesson04EndpointsSpec extends Specification {

    @Autowired
    Lesson04EndpointGateway endpointGateway

    def "transformer endpoint changes the payload before service activator handles it"() {
        given:
        def request = new Lesson04OrderRequest("order-4001", " vip ", new BigDecimal("250.00"))

        when:
        def report = endpointGateway.process(request)

        then:
        report.orderId() == "order-4001"
        report.normalizedCustomerType() == "VIP"
        report.orderAmount() == new BigDecimal("250.00")
        report.endpointTrail() == ["transformer:normalize", "service-activator:report"]
    }

    def "endpoint flow can handle missing customer type as ordinary business logic"() {
        given:
        def request = new Lesson04OrderRequest("order-4002", null, new BigDecimal("40.00"))

        when:
        def report = endpointGateway.process(request)

        then:
        report.normalizedCustomerType() == "UNKNOWN"
        report.endpointTrail() == ["transformer:normalize", "service-activator:report"]
    }
}
