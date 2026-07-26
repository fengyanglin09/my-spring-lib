package io.github.fengyanglin09.springintegrationlab.lessons.lesson03_message_channels

import io.github.fengyanglin09.springintegrationlab.lessons.lesson03_message_channels.gateway.Lesson03ChannelGateway
import io.github.fengyanglin09.springintegrationlab.lessons.lesson03_message_channels.handler.Lesson03BroadcastRecorder
import io.github.fengyanglin09.springintegrationlab.lessons.lesson03_message_channels.model.Lesson03DeliveryRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest
@ActiveProfiles("test")
class Lesson03MessageChannelsSpec extends Specification {

    @Autowired
    Lesson03ChannelGateway channelGateway

    @Autowired
    Lesson03BroadcastRecorder broadcastRecorder

    def "direct channel sends one message to one handler in the caller thread"() {
        given:
        def callerThreadName = Thread.currentThread().name
        def request = new Lesson03DeliveryRequest("message-3001", "direct message")

        when:
        def report = channelGateway.sendDirect(request)

        then:
        report.messageId() == "message-3001"
        report.handledBy() == "direct-order-handler"
        report.handledThreadName() == callerThreadName
    }

    def "publish subscribe channel broadcasts one message to multiple subscribers"() {
        given:
        broadcastRecorder.clear()
        def callerThreadName = Thread.currentThread().name
        def request = new Lesson03DeliveryRequest("message-3002", "broadcast event")

        when:
        channelGateway.broadcast(request)

        then:
        def receipts = broadcastRecorder.receiptsFor("message-3002")
        receipts*.subscriberName() as Set == ["audit-subscriber", "notification-subscriber"] as Set
        receipts.every { it.handledThreadName() == callerThreadName }
    }
}
