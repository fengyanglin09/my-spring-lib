package io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.config;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.model.Lesson12ChargeCommand;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.support.Lesson12Channels;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.support.Lesson12IdempotencyRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.handler.advice.IdempotentReceiverInterceptor;
import org.springframework.integration.metadata.ConcurrentMetadataStore;
import org.springframework.integration.selector.MetadataStoreSelector;
import org.springframework.messaging.Message;

// @Configuration tells Spring:
// "Read this class during startup because it contains bean definitions."
@Configuration
public class Lesson12IdempotencyConfiguration {

    // This metadata store remembers which command ids have already been seen.
    //
    // Metadata store means:
    // "A small key-value store used by Spring Integration components."
    //
    // In this lesson, the key is commandId and the value is managed by
    // MetadataStoreSelector.
    //
    // This is an in-memory metadata store so tests can reset it easily. A
    // production flow would often use a persistent store such as JDBC, Redis, or
    // another shared system so the idempotency memory survives restarts.
    @Bean
    ConcurrentMetadataStore lesson12MetadataStore(Lesson12IdempotencyRepository repository) {
        return repository.metadataStore();
    }

    // This is Spring Integration's idempotent receiver advice.
    //
    // Advice means:
    // "Extra behavior wrapped around an endpoint."
    //
    // Idempotent receiver means:
    // "Let the first message for a stable key through, but treat later messages
    // with the same key as duplicates."
    //
    // Stable key means:
    // "A value that stays the same when the same business operation is retried."
    //
    // In this lesson, commandId is the stable key. If charging command-1201 is
    // sent once, retried, or accidentally delivered again, every copy should
    // still use commandId = command-1201. That lets Spring Integration recognize
    // the later copies as duplicates.
    //
    // A bad idempotency key would be something that changes on each send, such
    // as the current time or a new random id. If the key changes, the receiver
    // cannot tell that two messages represent the same business operation.
    @Bean
    IdempotentReceiverInterceptor lesson12IdempotentReceiverInterceptor(
            ConcurrentMetadataStore lesson12MetadataStore
    ) {
        // MetadataStoreSelector is the decision maker used by the idempotent
        // receiver.
        //
        // It needs a stable key for each message. Here, the key is commandId.
        //
        // The lambda receives the whole Spring Message, not just the payload,
        // because selectors can make decisions using payload and headers.
        MetadataStoreSelector selector = new MetadataStoreSelector(
                (Message<?> message) -> ((Lesson12ChargeCommand) message.getPayload()).commandId(),
                lesson12MetadataStore
        );

        // This creates the idempotent receiver interceptor object.
        //
        // Interceptor means:
        // "Code that runs around another step."
        //
        // The selector above knows how to answer:
        // "Is this message the first one for this commandId, or is it a
        // duplicate?"
        //
        // The interceptor uses that selector when the lesson 12 flow reaches the
        // handler that applies the ledger side effect.
        //
        // Think of it like a gate in front of ledger.applyCharge(command):
        //
        // - selector accepts the message  -> open the gate and call the handler
        // - selector rejects the message  -> do not call the handler
        //
        // The next setting, setDiscardChannelName(...), tells the interceptor
        // where rejected duplicate messages should go.
        IdempotentReceiverInterceptor interceptor = new IdempotentReceiverInterceptor(selector);

        // setDiscardChannelName(...) means:
        // "If the selector rejects the message as a duplicate, send that
        // duplicate message to this channel."
        //
        // Without this discard channel, the interceptor could mark the message
        // with a duplicate header and continue. For this lesson, we want the
        // duplicate to take a visible alternate path that returns a skipped
        // result.
        interceptor.setDiscardChannelName(Lesson12Channels.DUPLICATE_CHARGE_COMMANDS);

        return interceptor;
    }
}
