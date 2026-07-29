package io.github.fengyanglin09.springintegrationlab.lessons.lesson12_transactions_idempotency.support;

import org.springframework.integration.metadata.ConcurrentMetadataStore;
import org.springframework.integration.metadata.SimpleMetadataStore;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Owns the in-memory idempotency state used by lesson 12.
 */
// @Component tells Spring:
// "Create one Lesson12IdempotencyRepository object during startup."
@Component
public class Lesson12IdempotencyRepository {

    // This map stores command ids that have already been accepted.
    //
    // ConcurrentHashMap is thread-safe for concurrent reads and writes. That
    // matters because message flows can run on different threads in real
    // applications.
    private final ConcurrentMap<String, String> acceptedCommandIds = new ConcurrentHashMap<>();

    public ConcurrentMetadataStore metadataStore() {
        // SimpleMetadataStore adapts this ConcurrentMap to Spring Integration's
        // ConcurrentMetadataStore contract.
        //
        // ConcurrentMetadataStore supports atomic operations such as
        // putIfAbsent(...). Atomic means the "check if key exists" and "store
        // key if missing" decision happens as one safe operation.
        //
        // That atomic check-and-store behavior is what makes duplicate
        // detection reliable when two messages with the same key arrive close
        // together.
        return new SimpleMetadataStore(acceptedCommandIds);
    }

    public int acceptedCommandCount() {
        return acceptedCommandIds.size();
    }

    public void clear() {
        acceptedCommandIds.clear();
    }
}
