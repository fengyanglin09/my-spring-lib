package io.github.fengyanglin09.springbootlab.lessons.lesson08_error_handling.model;

/**
 * One field-level validation problem returned to the API caller.
 */
public record Lesson08FieldError(
        /*
         * The field name uses the API model's field name, such as "sku" or
         * "quantity". This lets clients connect the error back to an input.
         */
        String field,

        /*
         * The message explains the rule that failed. In production APIs, teams
         * sometimes add a separate machine-readable code per field error too.
         * This lesson keeps the field error small and readable.
         */
        String message
) {
}
