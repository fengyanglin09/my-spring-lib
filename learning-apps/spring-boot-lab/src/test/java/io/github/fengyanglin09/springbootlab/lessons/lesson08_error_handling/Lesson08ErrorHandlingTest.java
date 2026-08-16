package io.github.fengyanglin09.springbootlab.lessons.lesson08_error_handling;

import io.github.fengyanglin09.springbootlab.lessons.lesson08_error_handling.api.Lesson08ApiExceptionHandler;
import io.github.fengyanglin09.springbootlab.lessons.lesson08_error_handling.api.Lesson08OrderReservationController;
import io.github.fengyanglin09.springbootlab.lessons.lesson08_error_handling.service.Lesson08InventoryReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Executable proof for Lesson 08's error-handling behavior.
 */
/*
 * @WebMvcTest keeps this focused on the HTTP boundary.
 *
 * The test imports the service and advice explicitly because @WebMvcTest does
 * not load every application bean. That is useful here: we can see exactly
 * which pieces are needed for this lesson's web slice.
 */
@WebMvcTest(controllers = Lesson08OrderReservationController.class)
@Import({
        Lesson08InventoryReservationService.class,
        Lesson08ApiExceptionHandler.class
})
class Lesson08ErrorHandlingTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void validRequestReturnsCreatedReservation() throws Exception {
        /*
         * This request passes Bean Validation, so the controller method body
         * runs and the service creates a reservation.
         */
        String jsonRequest = """
                {
                  "sku": "sku-123",
                  "quantity": 2,
                  "customerId": "cust-9"
                }
                """;

        mockMvc.perform(post("/lesson08/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                /*
                 * This assertion proves ResponseEntity.created(location) placed
                 * the URI into the HTTP Location header.
                 */
                .andExpect(header().string("Location", "/lesson08/reservations/L08-SKU-123-02-CUST-9"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reservationId").value("L08-SKU-123-02-CUST-9"))
                .andExpect(jsonPath("$.sku").value("sku-123"))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.customerId").value("cust-9"))
                .andExpect(jsonPath("$.status").value("RESERVED"));
    }

    @Test
    void invalidRequestReturnsStableValidationErrorResponse() throws Exception {
        /*
         * This request is JSON, but it violates the validation annotations on
         * Lesson08OrderReservationRequest.
         *
         * The controller method body should not run. Spring MVC raises
         * MethodArgumentNotValidException, and Lesson08ApiExceptionHandler turns
         * it into our stable error response shape.
         */
        String jsonRequest = """
                {
                  "sku": "",
                  "quantity": 0,
                  "customerId": ""
                }
                """;

        mockMvc.perform(post("/lesson08/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.code").value("validation.failed"))
                .andExpect(jsonPath("$.message").value("Request body did not pass validation"))
                .andExpect(jsonPath("$.path").value("/lesson08/reservations"))
                .andExpect(jsonPath("$.fieldErrors.length()").value(3))
                .andExpect(jsonPath("$.details.length()").value(0))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("customerId"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("customerId is required"))
                .andExpect(jsonPath("$.fieldErrors[1].field").value("quantity"))
                .andExpect(jsonPath("$.fieldErrors[1].message").value("quantity must be at least 1"))
                .andExpect(jsonPath("$.fieldErrors[2].field").value("sku"))
                .andExpect(jsonPath("$.fieldErrors[2].message").value("sku is required"));
    }

    @Test
    void domainExceptionReturnsStableConflictErrorResponse() throws Exception {
        /*
         * This request passes Bean Validation, so it reaches the service.
         *
         * The service rejects it with Lesson08OutOfStockException. The advice
         * turns that exception into HTTP 409 Conflict using the same error
         * response record as the validation failure.
         *
         * The final assertions prove that custom fields stored on the exception
         * are still accessible in the @ExceptionHandler method. They are exposed
         * through the response's details object.
         */
        String jsonRequest = """
                {
                  "sku": "OUT-OF-STOCK",
                  "quantity": 2,
                  "customerId": "cust-9"
                }
                """;

        mockMvc.perform(post("/lesson08/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.code").value("inventory.out-of-stock"))
                .andExpect(jsonPath("$.message").value("Only 0 item(s) are available for sku OUT-OF-STOCK"))
                .andExpect(jsonPath("$.path").value("/lesson08/reservations"))
                .andExpect(jsonPath("$.fieldErrors.length()").value(0))
                .andExpect(jsonPath("$.details.sku").value("OUT-OF-STOCK"))
                .andExpect(jsonPath("$.details.requestedQuantity").value(2))
                .andExpect(jsonPath("$.details.availableQuantity").value(0));
    }
}
