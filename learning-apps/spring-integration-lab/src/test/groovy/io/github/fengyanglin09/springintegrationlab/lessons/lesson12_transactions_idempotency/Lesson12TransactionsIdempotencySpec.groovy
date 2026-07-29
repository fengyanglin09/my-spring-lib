package io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency

import io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.gateway.Lesson12ChargeGateway
import io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.handler.Lesson12ChargeLedger
import io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.model.Lesson12ChargeCommand
import io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.support.Lesson12IdempotencyRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest
@ActiveProfiles("test")
class Lesson12TransactionsIdempotencySpec extends Specification {

    @Autowired
    Lesson12ChargeGateway chargeGateway

    @Autowired
    Lesson12ChargeLedger chargeLedger

    @Autowired
    Lesson12IdempotencyRepository idempotencyRepository

    def setup() {
        // Start each test with no remembered command ids and no ledger entries.
        //
        // This keeps one test's idempotency state from affecting the next test.
        idempotencyRepository.clear()
        chargeLedger.clear()
    }

    def "duplicate command id is skipped before the side effect runs twice"() {
        given:
        // This command id is the idempotency key.
        //
        // Sending the same command object twice simulates a retry or duplicate
        // delivery. The important question is:
        // "Does the side effect happen once or twice?"
        def command = new Lesson12ChargeCommand(
                "command-1201",
                "account-12",
                new BigDecimal("19.99")
        )

        when:
        // The first call should be accepted by the idempotent receiver.
        def firstResult = chargeGateway.charge(command)

        // The second call uses the same command id, so it should be treated as
        // a duplicate before the ledger handler can add another entry.
        def duplicateResult = chargeGateway.charge(command)

        then:
        // The first result proves the normal path ran.
        firstResult.charged()
        firstResult.status() == "CHARGED"
        firstResult.ledgerEntryCount() == 1

        and:
        // The duplicate result proves the alternate duplicate path ran.
        //
        // charged() is false because the duplicate flow returns a useful reply,
        // but intentionally skips the side effect.
        !duplicateResult.charged()
        duplicateResult.status() == "DUPLICATE_SKIPPED"
        duplicateResult.ledgerEntryCount() == 1
        duplicateResult.lessonTrail() == [
                "idempotent-receiver:rejected-duplicate-command-id",
                "discard-channel:sent-to-duplicate-flow",
                "handler:skipped-ledger-side-effect",
                "gateway:duplicate-skipped-result"
        ]

        and:
        // The ledger has only one entry even though the gateway was called
        // twice. This is the behavior idempotency is protecting.
        chargeLedger.entries().size() == 1
        chargeLedger.entries().first().commandId() == "command-1201"

        // The metadata store remembers one accepted command id.
        idempotencyRepository.acceptedCommandCount() == 1
    }

    def "different command ids are treated as different operations"() {
        given:
        def firstCommand = new Lesson12ChargeCommand(
                "command-1202",
                "account-12",
                new BigDecimal("10.00")
        )
        def secondCommand = new Lesson12ChargeCommand(
                "command-1203",
                "account-12",
                new BigDecimal("15.00")
        )

        when:
        def firstResult = chargeGateway.charge(firstCommand)
        def secondResult = chargeGateway.charge(secondCommand)

        then:
        // Different command ids represent different business operations, so
        // both should be accepted.
        firstResult.charged()
        secondResult.charged()

        // Groovy's spread operator, *., means:
        // "Call commandId() on each ledger entry and collect the results into a
        // list."
        chargeLedger.entries()*.commandId() == ["command-1202", "command-1203"]
        idempotencyRepository.acceptedCommandCount() == 2
    }
}
