package io.github.fengyanglin09.springintegrationlab.lessons.lesson10_polling_scheduling_backpressure

import io.github.fengyanglin09.springintegrationlab.lessons.lesson10_polling_scheduling_backpressure.handler.Lesson10WorkRecorder
import io.github.fengyanglin09.springintegrationlab.lessons.lesson10_polling_scheduling_backpressure.model.Lesson10WorkItem
import io.github.fengyanglin09.springintegrationlab.lessons.lesson10_polling_scheduling_backpressure.support.Lesson10EndpointIds
import io.github.fengyanglin09.springintegrationlab.lessons.lesson10_polling_scheduling_backpressure.support.Lesson10WorkIntake
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.integration.endpoint.PollingConsumer
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

@SpringBootTest
@ActiveProfiles("test")
class Lesson10PollingSchedulingBackpressureSpec extends Specification {

    @Autowired
    ApplicationContext applicationContext

    @Autowired
    Lesson10WorkIntake workIntake

    @Autowired
    Lesson10WorkRecorder workRecorder

    PollingConsumer pollingWorker

    // Spock automatically runs setup() before each test method in this class.
    //
    // In Spock vocabulary, each test method is often called a "feature method."
    //
    // This class has two feature methods:
    //
    // - "bounded queue accepts only up to its capacity while poller is stopped"
    // - "poller drains only max messages per poll"
    //
    // setup() runs once before the first feature method and once again before
    // the second feature method.
    def setup() {
        // The polling worker was given a stable id in the flow:
        // Lesson10EndpointIds.POLLING_WORKER.
        //
        // We fetch it from the Spring context so the test can start and stop
        // polling on purpose.
        pollingWorker = applicationContext.getBean(
                Lesson10EndpointIds.POLLING_WORKER,
                PollingConsumer
        )

        // Each test starts from a clean state:
        //
        // - poller stopped
        // - queue empty
        // - recorder empty
        //
        // Why is the poller stopped?
        //
        // In the flow, we configured .autoStartup(false). That means Spring
        // creates the polling endpoint, but it does not begin polling when the
        // test application context starts.
        //
        // We also call stop() here to be extra explicit before each test. If a
        // previous test started the poller, this makes sure the next test does
        // not inherit that running poller.
        //
        // This matters because the queue and recorder are Spring beans shared
        // by the test application context.
        pollingWorker.stop()
        workIntake.clearQueue()
        workRecorder.clear()
    }

    // Spock automatically runs cleanup() after each test method in this class.
    //
    // If a test passes, cleanup() runs.
    // If a test fails, cleanup() still runs.
    //
    // That makes cleanup() a good place to stop background work that should not
    // leak into the next test.
    def cleanup() {
        // Stop the poller after each test so it cannot keep draining messages
        // in the background after the test has finished.
        pollingWorker.stop()
    }

    def "bounded queue accepts only up to its capacity while poller is stopped"() {
        when:
        // (1..4) is a Groovy range. It produces the numbers:
        //
        // 1, 2, 3, 4
        //
        // collect { ... } runs the block once for each number and returns a new
        // List containing the return values.
        //
        // So this creates four work items and stores the four enqueue results
        // in the results List.
        def results = (1..4).collect { number ->
            workIntake.submit(new Lesson10WorkItem("work-${number}", "queued work ${number}"))
        }

        then:
        // results*.accepted() uses Groovy's spread operator: *.
        //
        // It means:
        // "Call accepted() on every item in the results List and give me a new
        // List of those accepted values."
        //
        // It is shorthand for:
        //
        // results.collect { result -> result.accepted() }
        //
        // The queue capacity is 3 and the poller is stopped, so:
        //
        // - first submit is accepted
        // - second submit is accepted
        // - third submit is accepted
        // - fourth submit is rejected because the queue is full
        results*.accepted() == [true, true, true, false]

        // results.last() is the fourth enqueue result.
        //
        // We check its trail so the test documents why that fourth item was
        // rejected.
        results.last().lessonTrail() == ["backpressure:rejected-because-queue-is-full"]

        // and: is a Spock block label.
        //
        // It is not required for the test to work. We could remove this line
        // and the assertions below would still run as part of the then: block.
        //
        // Use and: when you want to visually separate another group of related
        // facts.
        //
        // Here, the then: block first checks the enqueue results:
        //
        // - accepted
        // - accepted
        // - accepted
        // - rejected
        //
        // The and: block then checks the queue state after those enqueue
        // attempts:
        //
        // - queue has 3 waiting messages
        // - queue has 0 remaining capacity
        // - recorder is still empty because the poller is stopped
        and:
        // The queue is full:
        //
        // - queueSize() is 3 because three messages are waiting
        // - remainingCapacity() is 0 because the queue cannot accept another
        //   message right now
        workIntake.snapshot().queueSize() == 3
        workIntake.snapshot().remainingCapacity() == 0

        // The poller is still stopped in this test.
        //
        // Because it is stopped, it has not pulled messages out of the
        // QueueChannel and it has not called Lesson10WorkRecorder.record(...).
        //
        // That is why no queued messages should have been handled yet.
        workRecorder.processedWork().isEmpty()
    }

    def "poller drains only max messages per poll"() {
        given:
        // Put three messages into the queue before starting the poller.
        //
        // each { ... } runs the block once for each number but does not build a
        // new List like collect { ... } does.
        //
        // The assert inside the loop makes the test fail immediately if any
        // submit is rejected unexpectedly.
        (1..3).each { number ->
            assert workIntake.submit(new Lesson10WorkItem("work-${number}", "queued work ${number}")).accepted()
        }

        when:
        // Starting the polling worker allows the flow to begin draining the
        // QueueChannel.
        //
        // The flow uses an initial delay of Duration.ZERO, so the first poll can
        // run as soon as the endpoint starts.
        pollingWorker.start()

        then:
        // The poller runs on a Spring scheduler thread, not directly on the
        // test thread.
        //
        // PollingConditions repeatedly checks the assertion until it passes or
        // the timeout expires. This is better than sleeping for a fixed amount
        // of time because the test can continue as soon as the condition is
        // true.
        //
        // timeout: 2 means:
        // "Keep retrying for up to 2 seconds."
        //
        // If workRecorder.processedWork().size() becomes 2 within those 2
        // seconds, eventually { ... } passes and the test continues immediately.
        //
        // If the size is still not 2 after 2 seconds, eventually { ... } fails
        // the test.
        new PollingConditions(timeout: 2).eventually {
            assert workRecorder.processedWork().size() == 2
        }

        when:
        // Stop the poller after the first poll has processed two messages.
        //
        // The flow's fixed delay is long enough that a second poll should not
        // run before this stop call.
        pollingWorker.stop()

        then:
        // processedWork()*.workId() is another use of the Groovy spread
        // operator.
        //
        // It means:
        // "Call workId() on every processed record and compare the resulting
        // List to the expected List."
        //
        // maxMessagesPerPoll(2) means only two queued messages are handled in
        // the first poll.
        workRecorder.processedWork()*.workId() == ["work-1", "work-2"]

        // processedWork()*.submittedBy() returns the submittedBy value from
        // every processed record.
        //
        // unique() removes duplicates. If both records have the same submitted
        // by header, unique() returns one value.
        //
        // This proves the header set by Lesson10WorkIntake survived the queue
        // handoff and reached the polling handler.
        workRecorder.processedWork()*.submittedBy().unique() == ["lesson10-work-intake"]

        // every { ... } means:
        // "The condition inside the block must be true for every item."
        //
        // In Groovy, a non-empty String is treated as true. So this checks that
        // every processed record captured some poller thread name.
        workRecorder.processedWork().every { it.pollerThreadName() }

        // This and: is also optional.
        //
        // It separates the assertions about processed records above from the
        // assertions about queue state below.
        and:
        // Three messages were queued, but maxMessagesPerPoll(2) allowed only
        // two messages to be handled during the first poll.
        //
        // That leaves one message waiting in the queue.
        workIntake.snapshot().queueSize() == 1
        workIntake.snapshot().remainingCapacity() == 2
    }
}
