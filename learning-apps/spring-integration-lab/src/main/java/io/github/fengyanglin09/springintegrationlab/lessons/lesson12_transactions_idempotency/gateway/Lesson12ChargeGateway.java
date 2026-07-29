package io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.gateway;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.model.Lesson12ChargeCommand;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.model.Lesson12ChargeResult;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.support.Lesson12Channels;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Typed entry point into the lesson 12 idempotent charge flow.
 */
// @MessagingGateway tells Spring Integration:
// "Create an implementation of this interface at startup."
@MessagingGateway
public interface Lesson12ChargeGateway {

    // @Gateway says:
    // "When charge(...) is called, send the payload to lesson12ChargeCommands."
    //
    // @Payload says:
    // "Use this method argument as the message payload."
    //
    // The caller always sends one Lesson12ChargeCommand and receives one
    // Lesson12ChargeResult. The result may be:
    //
    // - CHARGED, when the command id is new
    // - DUPLICATE_SKIPPED, when the command id was already handled
    @Gateway(requestChannel = Lesson12Channels.CHARGE_COMMANDS)
    Lesson12ChargeResult charge(@Payload Lesson12ChargeCommand command);
}
