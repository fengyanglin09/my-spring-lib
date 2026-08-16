package io.github.fengyanglin09.springbootlab.lessons.lesson08_error_handling.api;

import io.github.fengyanglin09.springbootlab.lessons.lesson08_error_handling.model.Lesson08ApiErrorResponse;
import io.github.fengyanglin09.springbootlab.lessons.lesson08_error_handling.model.Lesson08FieldError;
import io.github.fengyanglin09.springbootlab.lessons.lesson08_error_handling.service.Lesson08OutOfStockException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Central error mapper for Lesson 08's REST API.
 */
/*
 * @RestControllerAdvice is from Spring MVC.
 *
 * It is similar to @RestController:
 *
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 *
 * @ControllerAdvice means this class can apply shared behavior to controllers.
 *
 * @ResponseBody is the important REST/API part.
 *
 * An HTTP response has pieces like this:
 *
 * HTTP status:
 *     400 Bad Request
 *
 * HTTP headers:
 *     Content-Type: application/json
 *
 * HTTP response body:
 *     {
 *       "code": "validation.failed",
 *       "message": "Request body did not pass validation"
 *     }
 *
 * "Response body" means the actual content sent back to the caller after the
 * status and headers. For REST APIs, that content is usually JSON.
 *
 * Without @ResponseBody, Spring MVC may interpret a handler method's return
 * value as something else, such as a page/view name.
 *
 * With @ResponseBody, Spring MVC interprets the handler method's return value
 * as the actual response content. In this class, the handler methods return
 * ResponseEntity<Lesson08ApiErrorResponse>, so Spring writes the
 * Lesson08ApiErrorResponse object into the HTTP response body as JSON.
 *
 * assignableTypes keeps this advice scoped to the Lesson 08 controller.
 *
 * Without assignableTypes:
 *
 *     @RestControllerAdvice
 *
 * this advice would be global. Its @ExceptionHandler methods could handle
 * matching exceptions from any controller in the application.
 *
 * With assignableTypes:
 *
 *     @RestControllerAdvice(assignableTypes = Lesson08OrderReservationController.class)
 *
 * this advice only applies to Lesson08OrderReservationController.
 *
 * In a real app, global advice is common because one application often wants
 * one consistent error response style. In this learning lab, scoped advice is
 * cleaner because future lessons can teach different error strategies without
 * this Lesson 08 class accidentally handling every controller.
 */
@RestControllerAdvice(assignableTypes = Lesson08OrderReservationController.class)
public class Lesson08ApiExceptionHandler {

    private static final Comparator<Lesson08FieldError> BY_FIELD_NAME =
            Comparator.comparing(Lesson08FieldError::field);

    /*
     * @ExceptionHandler is from Spring MVC.
     *
     * This method runs when Spring MVC throws MethodArgumentNotValidException.
     * That exception is the common result of @Valid failing on an @RequestBody
     * object.
     *
     * The controller method body did not run in this case. Spring failed the
     * request before the application service was called.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Lesson08ApiErrorResponse> handleValidationFailure(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        /*
         * The exception contains a BindingResult, which is Spring's object for
         * collected validation/binding problems.
         *
         * getFieldErrors() gives errors tied to individual request fields. This
         * lesson maps those Spring objects into our own stable API field-error
         * record.
         */
        List<Lesson08FieldError> fieldErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toFieldError)
                .sorted(BY_FIELD_NAME)
                .toList();

        Lesson08ApiErrorResponse response = Lesson08ApiErrorResponse.validationFailure(
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.badRequest().body(response);
    }

    /*
     * This handler maps an application/domain failure.
     *
     * The request may be valid JSON and pass Bean Validation, but the business
     * rule still says "we cannot do that right now." In HTTP terms, this lesson
     * represents that as 409 Conflict.
     *
     * Important distinction:
     *
     * - exception.getCause() would return another Throwable that caused this
     *   exception, if there was one
     * - exception.getSku(), exception.getRequestedQuantity(), and
     *   exception.getAvailableQuantity() return custom data that our exception
     *   stored when it was created
     *
     * Spring MVC gives this method the same exception object the service threw,
     * so all of that custom data is still accessible here.
     */
    @ExceptionHandler(Lesson08OutOfStockException.class)
    public ResponseEntity<Lesson08ApiErrorResponse> handleOutOfStock(
            Lesson08OutOfStockException exception,
            HttpServletRequest request
    ) {
        /*
         * The details map is built from custom exception fields, not from
         * exception.getCause().
         *
         * This proves that a domain exception can carry structured context to
         * the API error handler. The handler can then choose which parts are
         * safe and useful to expose to the caller.
         */
        Map<String, Object> details = Map.<String, Object>of(
                "sku", exception.getSku(),
                "requestedQuantity", exception.getRequestedQuantity(),
                "availableQuantity", exception.getAvailableQuantity()
        );

        Lesson08ApiErrorResponse response = Lesson08ApiErrorResponse.conflict(
                "inventory.out-of-stock",
                exception.getMessage(),
                request.getRequestURI(),
                details
        );

        return ResponseEntity.status(response.status()).body(response);
    }

    private Lesson08FieldError toFieldError(FieldError fieldError) {
        /*
         * getDefaultMessage() returns the message from the validation annotation
         * when one is available.
         *
         * Example:
         *
         * @NotBlank(message = "sku is required")
         *
         * becomes:
         *
         * "sku is required"
         */
        String message = fieldError.getDefaultMessage() == null
                ? "Invalid value"
                : fieldError.getDefaultMessage();

        return new Lesson08FieldError(fieldError.getField(), message);
    }
}
