package io.github.fengyanglin09.springbootlab.lessons.lesson08_error_handling.model;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

/**
 * Stable JSON error response shape for Lesson 08.
 */
public record Lesson08ApiErrorResponse(
        /*
         * Numeric HTTP status, such as 400 or 409.
         */
        int status,

        /*
         * Human-friendly HTTP reason phrase, such as "Bad Request" or
         * "Conflict".
         */
        String error,

        /*
         * Machine-readable application error code.
         *
         * Clients should prefer this kind of code over parsing the message
         * string, because messages often change for readability.
         */
        String code,

        /*
         * Human-readable explanation for logs, UI messages, or debugging.
         */
        String message,

        /*
         * The request path that failed.
         */
        String path,

        /*
         * Field-level validation errors. This is empty for non-validation
         * failures so callers can rely on one response shape.
         */
        List<Lesson08FieldError> fieldErrors,

        /*
         * Extra machine-readable context for this particular error.
         *
         * Validation failures do not need extra details in this lesson, so they
         * return an empty map.
         *
         * The out-of-stock handler uses this map to prove an important point:
         * custom fields stored on an exception are still available later when
         * @ExceptionHandler catches that exception.
         */
        Map<String, Object> details
) {

    /*
     * A compact constructor runs after Java receives the record component
     * values but before the record is fully created.
     *
     * List.copyOf(...) protects the response from later accidental changes to
     * the list that was passed in.
     *
     * Map.copyOf(...) does the same thing for the optional details map.
     */
    public Lesson08ApiErrorResponse {
        fieldErrors = List.copyOf(fieldErrors);
        details = Map.copyOf(details);
    }

    public static Lesson08ApiErrorResponse validationFailure(
            String path,
            List<Lesson08FieldError> fieldErrors
    ) {
        return new Lesson08ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "validation.failed",
                "Request body did not pass validation",
                path,
                fieldErrors,
                Map.of()
        );
    }

    public static Lesson08ApiErrorResponse conflict(
            String code,
            String message,
            String path,
            Map<String, Object> details
    ) {
        return new Lesson08ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                code,
                message,
                path,
                List.of(),
                details
        );
    }
}
