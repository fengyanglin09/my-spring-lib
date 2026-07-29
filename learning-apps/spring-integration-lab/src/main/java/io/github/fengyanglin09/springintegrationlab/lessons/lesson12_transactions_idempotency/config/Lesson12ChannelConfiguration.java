package io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.config;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.support.Lesson12Channels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

// @Configuration tells Spring:
// "Read this class during startup because it contains bean definitions."
@Configuration
public class Lesson12ChannelConfiguration {

    // This is the normal input channel for charge commands.
    //
    // The gateway sends Lesson12ChargeCommand payloads here. The normal charge
    // flow starts from this same channel.
    @Bean(name = Lesson12Channels.CHARGE_COMMANDS)
    MessageChannel lesson12ChargeCommands() {
        return new DirectChannel();
    }

    // This channel receives duplicate commands.
    //
    // The idempotent receiver interceptor sends a message here when it decides:
    // "This command id has already been accepted before."
    //
    // A separate duplicate flow starts from this channel and returns a skipped
    // result instead of applying the ledger side effect again.
    @Bean(name = Lesson12Channels.DUPLICATE_CHARGE_COMMANDS)
    MessageChannel lesson12DuplicateChargeCommands() {
        return new DirectChannel();
    }
}
