package io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters.model;

/**
 * Data shape used by the simulated external source.
 *
 * <p>This is intentionally not the same as our internal order shape. Real
 * external systems often use different names, codes, and string formats.</p>
 */
public record Lesson06ExternalOrderRecord(
        String externalOrderId,
        String customerCode,
        String amountText,
        String shippingCode
) {
}
