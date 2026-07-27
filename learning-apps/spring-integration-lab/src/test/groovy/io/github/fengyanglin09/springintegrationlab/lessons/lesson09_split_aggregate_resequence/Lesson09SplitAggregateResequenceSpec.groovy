package io.github.fengyanglin09.springintegrationlab.lessons.lesson09_split_aggregate_resequence

import io.github.fengyanglin09.springintegrationlab.lessons.lesson09_split_aggregate_resequence.gateway.Lesson09BatchOrderGateway
import io.github.fengyanglin09.springintegrationlab.lessons.lesson09_split_aggregate_resequence.model.Lesson09BatchOrderRequest
import io.github.fengyanglin09.springintegrationlab.lessons.lesson09_split_aggregate_resequence.model.Lesson09LineItemRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest
@ActiveProfiles("test")
class Lesson09SplitAggregateResequenceSpec extends Specification {

    @Autowired
    Lesson09BatchOrderGateway batchOrderGateway

    def "gateway receives one batch request and returns one aggregated summary"() {
        given:
        def request = new Lesson09BatchOrderRequest(
                "order-9001",
                [
                        new Lesson09LineItemRequest(1, "SKU-ALPHA", 2, new BigDecimal("10.00")),
                        new Lesson09LineItemRequest(2, "SKU-BRAVO", 1, new BigDecimal("5.50")),
                        new Lesson09LineItemRequest(3, "SKU-CHARLIE", 3, new BigDecimal("2.00"))
                ]
        )

        when:
        def summary = batchOrderGateway.price(request)

        then:
        summary.orderId() == "order-9001"
        summary.lineCount() == 3
        summary.orderTotal() == new BigDecimal("31.50")
        summary.lineNumbersInOrder() == [1, 2, 3]
        summary.skusInOrder() == ["SKU-ALPHA", "SKU-BRAVO", "SKU-CHARLIE"]
        summary.sequenceNumbersInOrder() == [1, 2, 3]
        summary.messageGroupId()
        summary.lessonTrail() == [
                "split:batch-message-to-line-item-messages",
                "channel:executor-can-process-line-items-in-parallel",
                "transform:price-each-line-item",
                "resequence:release-line-items-in-original-sequence",
                "aggregate:line-items-to-order-summary"
        ]
    }

    def "single line batch still passes through split resequence and aggregate"() {
        given:
        def request = new Lesson09BatchOrderRequest(
                "order-9002",
                [
                        new Lesson09LineItemRequest(1, "SKU-SOLO", 4, new BigDecimal("3.25"))
                ]
        )

        when:
        def summary = batchOrderGateway.price(request)

        then:
        summary.orderId() == "order-9002"
        summary.lineCount() == 1
        summary.orderTotal() == new BigDecimal("13.00")
        summary.lineNumbersInOrder() == [1]
        summary.sequenceNumbersInOrder() == [1]
    }
}
