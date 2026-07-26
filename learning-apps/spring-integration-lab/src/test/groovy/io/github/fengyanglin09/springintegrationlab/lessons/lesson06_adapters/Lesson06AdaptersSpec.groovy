package io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters

import io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters.model.Lesson06ExternalOrderRecord
import io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters.support.Lesson06AdapterIds
import io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters.support.Lesson06ExternalOrderInbox
import io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters.support.Lesson06PartnerOrderOutbox
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.integration.endpoint.SourcePollingChannelAdapter
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

@SpringBootTest
@ActiveProfiles("test")
class Lesson06AdaptersSpec extends Specification {

    @Autowired
    ApplicationContext applicationContext

    @Autowired
    Lesson06ExternalOrderInbox externalInbox

    @Autowired
    Lesson06PartnerOrderOutbox partnerOutbox

    SourcePollingChannelAdapter inboundAdapter

    def setup() {
        inboundAdapter = applicationContext.getBean(
                Lesson06AdapterIds.INBOUND_ADAPTER,
                SourcePollingChannelAdapter
        )
        inboundAdapter.stop()
        externalInbox.clear()
        partnerOutbox.clear()
    }

    def cleanup() {
        inboundAdapter.stop()
    }

    def "inbound adapter turns external data into a message and outbound adapter sends it out"() {
        given:
        externalInbox.submit(new Lesson06ExternalOrderRecord(
                "external-6001",
                " vip ",
                "250.00",
                "EXP"
        ))

        when:
        inboundAdapter.start()

        then:
        new PollingConditions(timeout: 2).eventually {
            assert partnerOutbox.receipts().size() == 1
        }

        and:
        def receipt = partnerOutbox.receipts().first()
        receipt.partnerOrderId() == "external-6001"
        receipt.deliveryMode() == "EXPEDITED"
        receipt.adapterTrail() == [
                "inbound-adapter:poll-external-inbox",
                "transform:external-to-internal",
                "transform:internal-to-partner-request",
                "outbound-adapter:send-to-partner"
        ]
    }

    def "inbound adapter does not create a message when the external source has no data"() {
        when:
        inboundAdapter.start()

        then:
        Thread.sleep(100)
        partnerOutbox.receipts().isEmpty()
    }
}
