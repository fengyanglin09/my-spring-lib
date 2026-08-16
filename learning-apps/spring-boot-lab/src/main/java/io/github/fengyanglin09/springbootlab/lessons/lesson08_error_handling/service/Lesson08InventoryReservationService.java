package io.github.fengyanglin09.springbootlab.lessons.lesson08_error_handling.service;

import io.github.fengyanglin09.springbootlab.lessons.lesson08_error_handling.model.Lesson08OrderReservationRequest;
import io.github.fengyanglin09.springbootlab.lessons.lesson08_error_handling.model.Lesson08OrderReservationResponse;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Tiny service used by Lesson 08 to create success and domain-failure paths.
 */
@Service
public class Lesson08InventoryReservationService {

    private static final int AVAILABLE_QUANTITY = 10;
    private static final String OUT_OF_STOCK_SKU = "OUT-OF-STOCK";

    public Lesson08OrderReservationResponse reserve(Lesson08OrderReservationRequest request) {
        /*
         * By the time this method runs, @Valid has already accepted the basic
         * shape of the request:
         *
         * - sku is not blank
         * - quantity is at least 1
         * - customerId is not blank
         *
         * This service checks a different kind of rule: can the application
         * actually fulfill the request?
         */
        if (OUT_OF_STOCK_SKU.equalsIgnoreCase(request.sku())) {
            throw new Lesson08OutOfStockException(request.sku(), request.quantity(), 0);
        }

        if (request.quantity() > AVAILABLE_QUANTITY) {
            throw new Lesson08OutOfStockException(request.sku(), request.quantity(), AVAILABLE_QUANTITY);
        }

        String normalizedSku = request.sku().toUpperCase(Locale.ROOT);
        String normalizedCustomerId = request.customerId().toUpperCase(Locale.ROOT);
        String reservationId = "L08-%s-%02d-%s".formatted(
                normalizedSku,
                request.quantity(),
                normalizedCustomerId
        );

        return new Lesson08OrderReservationResponse(
                reservationId,
                request.sku(),
                request.quantity(),
                request.customerId(),
                "RESERVED",
                "Reservation %s is ready".formatted(reservationId)
        );
    }
}
