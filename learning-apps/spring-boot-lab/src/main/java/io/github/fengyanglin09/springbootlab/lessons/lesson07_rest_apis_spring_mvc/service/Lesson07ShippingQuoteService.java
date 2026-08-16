package io.github.fengyanglin09.springbootlab.lessons.lesson07_rest_apis_spring_mvc.service;

import io.github.fengyanglin09.springbootlab.lessons.lesson07_rest_apis_spring_mvc.model.Lesson07ShippingOptionResponse;
import io.github.fengyanglin09.springbootlab.lessons.lesson07_rest_apis_spring_mvc.model.Lesson07ShippingQuoteRequest;
import io.github.fengyanglin09.springbootlab.lessons.lesson07_rest_apis_spring_mvc.model.Lesson07ShippingQuoteResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/**
 * Small application service used by the Lesson 07 REST controller.
 *
 * <p>The calculations are intentionally simple. The lesson is not really about
 * shipping; it is about keeping HTTP mapping in the controller and ordinary
 * application decisions in a service.</p>
 */
/*
 * @Service is from Spring Framework.
 *
 * It is a stereotype annotation, like @Component, but it communicates the role
 * of this class more clearly: this bean holds application/service-layer logic.
 *
 * Because this class is under SpringBootLabApplication's root package,
 * component scanning can find it and create it as a Spring bean. The controller
 * can then receive this service through constructor injection.
 */
@Service
public class Lesson07ShippingQuoteService {

    /*
     * BigDecimal is used for money-like values because decimal amounts should
     * not be calculated with double when exact cents matter.
     *
     * These constants are deliberately boring so the lesson stays focused on
     * REST flow, not shipping math.
     */
    private static final BigDecimal BASE_COST = new BigDecimal("4.99");
    private static final BigDecimal COST_PER_ITEM = new BigDecimal("1.50");
    private static final BigDecimal EXPEDITED_FEE = new BigDecimal("9.00");

    public Lesson07ShippingOptionResponse optionFor(String destination) {
        /*
         * This service method returns an ordinary Java record. It knows nothing
         * about HTTP status codes, headers, or JSON. That is the controller's
         * job.
         */
        return new Lesson07ShippingOptionResponse(
                destination,
                true,
                5,
                2,
                "Shipping options are available for %s".formatted(destination)
        );
    }

    public Lesson07ShippingQuoteResponse createQuote(Lesson07ShippingQuoteRequest request) {
        /*
         * This minimal guard keeps the service from producing nonsense. Lesson
         * 08 will teach proper validation and API error responses with more
         * care, so Lesson 07 stays focused on the happy-path REST flow.
         */
        if (request.itemCount() < 1) {
            throw new IllegalArgumentException("itemCount must be positive");
        }

        int estimatedDays = request.expedited() ? 2 : 5;

        /*
         * Cost formula for the sample quote:
         *
         * base cost + item count cost + optional expedited fee
         *
         * setScale(2, RoundingMode.HALF_UP) rounds the result to two decimal
         * places, which is what we usually expect when displaying a dollar
         * amount.
         */
        BigDecimal estimatedCost = BASE_COST
                .add(COST_PER_ITEM.multiply(BigDecimal.valueOf(request.itemCount())))
                .add(request.expedited() ? EXPEDITED_FEE : BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);

        /*
         * Locale.ROOT means "use a stable, language-neutral uppercase rule."
         * That keeps generated ids predictable regardless of the computer's
         * user locale.
         */
        String destinationCode = request.destination().toUpperCase(Locale.ROOT);
        String speedCode = request.expedited() ? "EXP" : "STD";

        /*
         * The quote id is deterministic so the test can assert it exactly.
         *
         * Example:
         *
         * destination = "austin"
         * itemCount = 3
         * expedited = true
         *
         * quoteId = "L07-AUSTIN-03-EXP"
         */
        String quoteId = "L07-%s-%02d-%s".formatted(destinationCode, request.itemCount(), speedCode);

        return new Lesson07ShippingQuoteResponse(
                quoteId,
                request.destination(),
                request.itemCount(),
                request.expedited(),
                estimatedDays,
                estimatedCost,
                "Quote %s is ready".formatted(quoteId)
        );
    }
}
