package io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data shape expected by the simulated partner system.
 */
public record Lesson06PartnerOrderRequest(
        String partnerOrderId,
        String customerSegment,
        BigDecimal amount,
        String deliveryMode,
        List<String> adapterTrail
) {
}
