package io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters.model;

import java.util.List;

/**
 * Record kept by the simulated partner system after the outbound adapter sends.
 */
public record Lesson06PartnerOrderReceipt(
        String partnerOrderId,
        String deliveryMode,
        List<String> adapterTrail
) {
}
