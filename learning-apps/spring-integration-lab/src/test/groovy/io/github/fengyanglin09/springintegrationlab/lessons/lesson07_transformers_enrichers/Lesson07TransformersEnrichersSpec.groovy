package io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers

import io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.gateway.Lesson07ShapeGateway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest
@ActiveProfiles("test")
class Lesson07TransformersEnrichersSpec extends Specification {

    @Autowired
    Lesson07ShapeGateway shapeGateway

    def "flow transforms raw text, enriches payload, and enriches headers"() {
        when:
        def report = shapeGateway.shape(
                "order-7001, cust-vip, 1200.00, sku-1",
                "csv-upload"
        )

        then:
        report.orderId() == "order-7001"
        report.sourceSystem() == "csv-upload"
        report.customerTier() == "VIP"
        report.region() == "NORTH"
        report.orderAmount() == new BigDecimal("1200.00")
        report.valueBand() == "HIGH_VALUE"
        report.lessonName() == "lesson07-transformers-enrichers"
        report.shapeTrail() == [
                "transform:raw-csv-to-order-draft",
                "transform:add-customer-profile-to-payload",
                "header-enricher:add-message-metadata",
                "handle:build-shape-report"
        ]
    }

    def "unknown customer still receives default profile enrichment"() {
        when:
        def report = shapeGateway.shape(
                "order-7002, cust-new, 35.50, sku-2",
                "manual-entry"
        )

        then:
        report.sourceSystem() == "manual-entry"
        report.customerTier() == "STANDARD"
        report.region() == "UNKNOWN_REGION"
        report.valueBand() == "STANDARD_VALUE"
    }
}
