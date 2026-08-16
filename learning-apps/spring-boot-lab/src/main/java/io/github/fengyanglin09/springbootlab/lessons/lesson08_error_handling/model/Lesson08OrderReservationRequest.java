package io.github.fengyanglin09.springbootlab.lessons.lesson08_error_handling.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * JSON request body for creating an inventory reservation.
 */
public record Lesson08OrderReservationRequest(
        /*
         * @NotBlank is from Jakarta Validation.
         *
         * It rejects:
         *
         * - null
         * - ""
         * - "   "
         *
         * This is stronger than @NotNull for String fields because an empty
         * string is technically not null, but it is still not a useful SKU.
         */
        @NotBlank(message = "sku is required")
        String sku,

        /*
         * @Min is from Jakarta Validation.
         *
         * Because quantity is an int, it cannot be null. If the JSON omits this
         * field, Java's default int value is 0, and this @Min rule catches it.
         */
        @Min(value = 1, message = "quantity must be at least 1")
        int quantity,

        /*
         * This field uses @NotBlank for the same reason as sku: a blank customer
         * id should not be accepted as real input.
         */
        @NotBlank(message = "customerId is required")
        String customerId
) {
}
