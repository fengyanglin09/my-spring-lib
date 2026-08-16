package io.github.fengyanglin09.springbootlab.lessons.lesson08_error_handling.api;

import io.github.fengyanglin09.springbootlab.lessons.lesson08_error_handling.model.Lesson08OrderReservationRequest;
import io.github.fengyanglin09.springbootlab.lessons.lesson08_error_handling.model.Lesson08OrderReservationResponse;
import io.github.fengyanglin09.springbootlab.lessons.lesson08_error_handling.service.Lesson08InventoryReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * REST controller used by Lesson 08 to demonstrate validation and error
 * handling.
 */
/*
 * This lesson stays with @RestController because the endpoint returns JSON, not
 * a server-rendered page.
 *
 * Lesson 08's new focus is what happens when the request is invalid or when the
 * service cannot complete the request.
 */
@RestController
/*
 * @RequestMapping is from Spring MVC.
 *
 * At the class level, it sets defaults that every handler method in this
 * controller inherits.
 *
 * path = "/lesson08/reservations"
 *     -> every endpoint in this controller starts with this URL path
 *
 *     Because the reserve(...) method below has @PostMapping with no extra
 *     path, the full endpoint is:
 *
 *     POST /lesson08/reservations
 *
 * produces = MediaType.APPLICATION_JSON_VALUE
 *     -> this controller's responses are JSON
 *
 *     MediaType.APPLICATION_JSON_VALUE is Spring's constant for the String
 *     "application/json". The produces attribute expects a String, so this code
 *     uses the _VALUE constant instead of MediaType.APPLICATION_JSON.
 *
 * Important distinction:
 *
 * - produces describes what this endpoint sends back in the HTTP response
 * - consumes, used below on @PostMapping, describes what this endpoint accepts
 *   in the HTTP request body
 */
@RequestMapping(path = "/lesson08/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class Lesson08OrderReservationController {

    /*
     * The controller delegates the application decision to a service.
     *
     * If the request is invalid, Spring MVC validation stops before this service
     * is called. If the request is valid but inventory cannot be reserved, the
     * service throws a lesson-specific domain exception.
     */
    private final Lesson08InventoryReservationService reservationService;

    /*
     * @Valid is from Jakarta Validation.
     *
     * @RequestBody first asks Spring MVC/Jackson to convert the JSON body into a
     * Lesson08OrderReservationRequest record.
     *
     * @Valid then asks Bean Validation to check the annotations on that record.
     *
     * If validation fails:
     *
     * - the method body below does not run
     * - Spring MVC throws MethodArgumentNotValidException
     * - Lesson08ApiExceptionHandler turns that exception into a JSON error
     *
     * If validation passes:
     *
     * - this method body runs
     * - the service is called
     */
    /*
     * @PostMapping is from Spring MVC.
     *
     * It says this method handles HTTP POST requests.
     *
     * There is no path value inside @PostMapping here, so this method uses only
     * the class-level path from @RequestMapping:
     *
     * POST /lesson08/reservations
     *
     * consumes = MediaType.APPLICATION_JSON_VALUE means:
     *
     * "This endpoint accepts an HTTP request body whose Content-Type is
     * application/json."
     *
     * Example request header:
     *
     * Content-Type: application/json
     *
     * This pairs with @RequestBody below. The endpoint accepts JSON, then Spring
     * MVC/Jackson converts that JSON into Lesson08OrderReservationRequest.
     *
     * If a client sends a different Content-Type, Spring MVC can reject the
     * request before this method body runs because the request does not match
     * what this handler method says it consumes.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Lesson08OrderReservationResponse> reserve(
            @Valid @RequestBody Lesson08OrderReservationRequest request
    ) {
        Lesson08OrderReservationResponse response = reservationService.reserve(request);

        /*
         * Successful creation returns 201 Created and a Location header, just
         * like Lesson 07.
         *
         * Lesson 08 adds the other side of the API contract: what the caller
         * receives when the request is not successful.
         */
        /*
         * URI is from java.net.
         *
         * A URI is a resource identifier. Here, it represents the URL path for
         * the reservation that was just created.
         *
         * URI.create(...) converts the String path into a URI object because
         * ResponseEntity.created(...) expects a URI.
         *
         * Example:
         *
         * response.reservationId() = "L08-SKU-123-02-CUST-9"
         *
         * location =
         * "/lesson08/reservations/L08-SKU-123-02-CUST-9"
         *
         * ResponseEntity.created(location) uses this URI to set the HTTP
         * Location header. The header tells the client where the newly created
         * resource can be found.
         *
         * This lesson does not implement a GET endpoint for that URI yet. It is
         * showing the common REST pattern:
         *
         * POST creates something
         * -> return 201 Created
         * -> include Location: /path/to/the/new/resource
         *
         * Example HTTP response:
         *
         * HTTP/1.1 201 Created
         * Location: /lesson08/reservations/L08-SKU-123-02-CUST-9
         * Content-Type: application/json
         *
         * {
         *   "reservationId": "L08-SKU-123-02-CUST-9",
         *   "sku": "sku-123",
         *   "quantity": 2,
         *   "customerId": "cust-9",
         *   "status": "RESERVED",
         *   "message": "Reservation L08-SKU-123-02-CUST-9 is ready"
         * }
         *
         * A frontend can read the Location header separately from the JSON body.
         */
        URI location = URI.create("/lesson08/reservations/" + response.reservationId());
        return ResponseEntity.created(location).body(response);
    }
}
