package io.github.fengyanglin09.springintegrationlab.lessons.lesson02_messages

import io.github.fengyanglin09.springintegrationlab.lessons.lesson02_messages.gateway.Lesson02MessageGateway
import io.github.fengyanglin09.springintegrationlab.lessons.lesson02_messages.model.Lesson02OrderRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest
@ActiveProfiles("test")
class Lesson02MessagesSpec extends Specification {

    @Autowired
    Lesson02MessageGateway messageGateway

    def "gateway creates a message with payload and headers"() {
        given:
        def request = new Lesson02OrderRequest("order-2001", "customer-77", new BigDecimal("125.50"))

        when:
        // The first argument becomes the payload.
        // The second and third arguments become headers.
        def report = messageGateway.inspect(request, "tenant-north", "mobile-checkout")

        then:
        report.orderId() == "order-2001"
        report.customerId() == "customer-77"
        report.orderAmount() == new BigDecimal("125.50")
        report.tenantId() == "tenant-north"
        report.sourceSystem() == "mobile-checkout"
        report.payloadType() == "Lesson02OrderRequest"
        report.frameworkMessageIdPresent()
        report.frameworkTimestampPresent()
    }

    def "same payload can travel with different headers"() {
        given:
        def request = new Lesson02OrderRequest("order-2002", "customer-88", new BigDecimal("42.00"))

        when:
        def firstReport = messageGateway.inspect(request, "tenant-east", "batch-import")
        def secondReport = messageGateway.inspect(request, "tenant-west", "admin-tool")

        then:
        // The business payload stayed the same.
        firstReport.orderId() == secondReport.orderId()
        firstReport.orderAmount() == secondReport.orderAmount()

        // The message metadata changed.
        firstReport.tenantId() == "tenant-east"
        firstReport.sourceSystem() == "batch-import"
        secondReport.tenantId() == "tenant-west"
        secondReport.sourceSystem() == "admin-tool"
    }
}
