package io.github.fengyanglin09.springintegrationlab.lessons.lesson08_routing

import io.github.fengyanglin09.springintegrationlab.lessons.lesson08_routing.gateway.Lesson08RoutingGateway
import io.github.fengyanglin09.springintegrationlab.lessons.lesson08_routing.model.Lesson08OrderRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest
@ActiveProfiles("test")
class Lesson08RoutingSpec extends Specification {

    @Autowired
    Lesson08RoutingGateway routingGateway

    def "filter rejects unacceptable orders before the router runs"() {
        given:
        def request = new Lesson08OrderRequest(
                "order-8001",
                "vip",
                new BigDecimal("75.00"),
                false,
                false
        )

        when:
        def result = routingGateway.route(request)

        then:
        !result.accepted()
        result.path() == "REJECTED"
        result.routingTrail() == ["filter:rejected-order"]
    }

    def "router sends expedited orders to expedited branch"() {
        given:
        def request = new Lesson08OrderRequest(
                "order-8002",
                "vip",
                new BigDecimal("75.00"),
                true,
                true
        )

        when:
        def result = routingGateway.route(request)

        then:
        result.accepted()
        result.path() == "EXPEDITED_PATH"
        result.routingTrail() == [
                "filter:accepted-order",
                "transform:classify-route-key",
                "router:expedited-branch"
        ]
    }

    def "router sends high value non-expedited orders to review branch"() {
        given:
        def request = new Lesson08OrderRequest(
                "order-8003",
                "standard",
                new BigDecimal("1250.00"),
                false,
                true
        )

        when:
        def result = routingGateway.route(request)

        then:
        result.accepted()
        result.path() == "REVIEW_PATH"
        result.routingTrail().last() == "router:review-branch"
    }

    def "router sends ordinary accepted orders to standard branch"() {
        given:
        def request = new Lesson08OrderRequest(
                "order-8004",
                "standard",
                new BigDecimal("85.00"),
                false,
                true
        )

        when:
        def result = routingGateway.route(request)

        then:
        result.accepted()
        result.path() == "STANDARD_PATH"
        result.routingTrail().last() == "router:standard-branch"
    }
}
