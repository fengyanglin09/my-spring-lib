/**
 * Lesson 08 objective: Turn validation failures and domain exceptions into
 * consistent API error responses.
 *
 * <p>Study path:</p>
 *
 * <ol>
 *     <li>Add Spring Boot's validation starter so Jakarta Bean Validation is
 *     available.</li>
 *     <li>Put validation annotations such as {@code @NotBlank} and
 *     {@code @Min} on request records.</li>
 *     <li>Use {@code @Valid @RequestBody} so Spring MVC validates the request
 *     object before the controller method body runs.</li>
 *     <li>Handle {@code MethodArgumentNotValidException} with
 *     {@code @RestControllerAdvice} and {@code @ExceptionHandler}.</li>
 *     <li>Introduce a domain exception for valid requests that cannot be
 *     fulfilled.</li>
 *     <li>Show that custom fields stored on a thrown exception are available
 *     later in the matching {@code @ExceptionHandler}; this is different from
 *     the exception's {@code getCause()}.</li>
 *     <li>Return one stable JSON error response shape for both validation and
 *     domain failures.</li>
 * </ol>
 */
package io.github.fengyanglin09.springbootlab.lessons.lesson08_error_handling;
