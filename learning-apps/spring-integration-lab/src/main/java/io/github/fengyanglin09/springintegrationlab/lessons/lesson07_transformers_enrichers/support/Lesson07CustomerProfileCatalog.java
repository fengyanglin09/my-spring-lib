package io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.support;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.model.Lesson07CustomerProfile;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Tiny in-memory customer profile lookup used by lesson 07.
 */
// @Component tells Spring:
// "Create one Lesson07CustomerProfileCatalog object during startup."
//
// This catalog stands in for a database or service call. We keep it local so the
// lesson can focus on message transformation and enrichment instead of data
// access.
@Component
public class Lesson07CustomerProfileCatalog {

    private final Map<String, Lesson07CustomerProfile> profiles = Map.of(
            "cust-vip", new Lesson07CustomerProfile("cust-vip", "VIP", "NORTH"),
            "cust-standard", new Lesson07CustomerProfile("cust-standard", "STANDARD", "SOUTH")
    );

    public Lesson07CustomerProfile findByCustomerId(String customerId) {
        return profiles.getOrDefault(
                customerId,
                new Lesson07CustomerProfile(customerId, "STANDARD", "UNKNOWN_REGION")
        );
    }
}
