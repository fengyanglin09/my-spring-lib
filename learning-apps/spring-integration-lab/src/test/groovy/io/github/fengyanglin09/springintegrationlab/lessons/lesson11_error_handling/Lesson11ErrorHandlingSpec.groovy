package io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling

import io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.gateway.Lesson11RecoveringPaymentGateway
import io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.gateway.Lesson11ThrowingPaymentGateway
import io.github.fengyanglin09.springintegrationlab.lessons.lesson11_error_handling.model.Lesson11PaymentRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest
@ActiveProfiles("test")
class Lesson11ErrorHandlingSpec extends Specification {

    @Autowired
    Lesson11RecoveringPaymentGateway recoveringGateway

    @Autowired
    Lesson11ThrowingPaymentGateway throwingGateway

    def "valid payment returns through the normal flow"() {
        given:
        def request = new Lesson11PaymentRequest(
                "payment-1101",
                "customer-11",
                new BigDecimal("125.00"),
                true
        )

        when:
        def result = recoveringGateway.authorize(request)

        then:
        result.approved()
        result.status() == "APPROVED"
        result.reasonCode() == "NONE"
        result.lessonTrail() == [
                "normal-flow:received-payment-request",
                "handler:authorized-payment",
                "gateway:normal-reply"
        ]
    }

    def "recovering gateway maps a thrown exception to a declined result"() {
        given:
        def request = new Lesson11PaymentRequest(
                "payment-1102",
                "customer-11",
                new BigDecimal("75.00"),
                false
        )

        when:
        def result = recoveringGateway.authorize(request)

        then:
        !result.approved()
        result.status() == "DECLINED"
        result.reasonCode() == "MISSING_PAYMENT_TOKEN"
        result.amount() == new BigDecimal("75.00")
        result.lessonTrail() == [
                "normal-flow:handler-threw-exception",
                "gateway:error-channel-received-error-message",
                "error-message:payload-is-throwable",
                "error-flow:exception-to-failure-result"
        ]
    }

    def "gateway without an error channel throws the downstream exception"() {
        given:
        def request = new Lesson11PaymentRequest(
                "payment-1103",
                "customer-11",
                new BigDecimal("900.00"),
                true
        )

        when:
        throwingGateway.authorize(request)

        then:
        def exception = thrown(Exception)
        exception.message.contains("manual review")
    }
}
