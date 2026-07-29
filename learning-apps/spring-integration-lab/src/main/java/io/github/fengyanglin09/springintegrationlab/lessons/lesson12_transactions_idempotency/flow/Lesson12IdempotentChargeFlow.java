package io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.flow;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.handler.Lesson12ChargeLedger;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.model.Lesson12ChargeCommand;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.support.Lesson12Channels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.advice.IdempotentReceiverInterceptor;

// @Configuration tells Spring:
// "Read this class during startup because it contains bean definitions."
@Configuration
public class Lesson12IdempotentChargeFlow {

    // @Bean tells Spring:
    // "Create this normal charge IntegrationFlow during startup."
    @Bean
    IntegrationFlow lesson12ChargeIntegrationFlow(
            Lesson12ChargeLedger ledger,
            IdempotentReceiverInterceptor lesson12IdempotentReceiverInterceptor
    ) {
        // This is the normal path:
        //
        // charge command
        // -> idempotent receiver checks commandId
        // -> first command for that id reaches the ledger handler
        // -> ledger side effect is applied once
        return IntegrationFlow.from(Lesson12Channels.CHARGE_COMMANDS)
                // handle(...) creates the endpoint that would apply the side
                // effect.
                //
                // The typed lambda form says:
                //
                // - expect the payload to be Lesson12ChargeCommand
                // - call ledger.applyCharge(command)
                // - use the returned Lesson12ChargeResult as the reply payload
                .handle(
                        Lesson12ChargeCommand.class,
                        (command, headers) -> ledger.applyCharge(command),
                        endpoint -> endpoint
                                // advice(...) attaches extra behavior around
                                // this endpoint.
                                //
                                // The idempotent receiver advice runs before
                                // ledger.applyCharge(command).
                                //
                                // If this is the first time the commandId was
                                // seen, the advice lets the message continue to
                                // the handler.
                                //
                                // If the commandId was already seen, the advice
                                // does not call the handler. Instead, it sends
                                // the duplicate message to the discard channel
                                // configured in Lesson12IdempotencyConfiguration.
                                .advice(lesson12IdempotentReceiverInterceptor))
                .get();
    }

    // @Bean tells Spring:
    // "Create this duplicate-command IntegrationFlow during startup."
    @Bean
    IntegrationFlow lesson12DuplicateChargeIntegrationFlow(Lesson12ChargeLedger ledger) {
        // This is the duplicate path:
        //
        // duplicate charge command
        // -> return skipped result
        // -> do not apply ledger side effect again
        return IntegrationFlow.from(Lesson12Channels.DUPLICATE_CHARGE_COMMANDS)
                // This handler receives duplicate messages from the idempotent
                // receiver's discard channel.
                //
                // It returns Lesson12ChargeResult, so the gateway caller still
                // receives a normal reply even though the side effect was
                // skipped.
                .handle(Lesson12ChargeCommand.class, (command, headers) -> ledger.skipDuplicate(command))
                .get();
    }
}
