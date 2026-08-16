# Lesson 08: Validation And API Error Responses

## What This Lesson Teaches

This lesson teaches what happens when a REST API request cannot be processed.

Lesson 07 showed the happy path:

```text
valid HTTP request
-> @RestController method
-> service
-> success response
```

Lesson 08 adds the failure path:

```text
invalid HTTP request
-> Spring MVC validation fails before the controller body runs
-> @RestControllerAdvice handles the validation exception
-> consistent JSON error response
```

It also covers a domain failure:

```text
valid HTTP request
-> @RestController method
-> service throws a domain exception
-> @RestControllerAdvice handles the domain exception
-> consistent JSON error response
```

## Objective

Turn validation failures and domain exceptions into consistent API error
responses.

## Files

```text
lesson08_error_handling/
├── README.md
├── package-info.java
├── api/
│   ├── Lesson08ApiExceptionHandler.java
│   └── Lesson08OrderReservationController.java
├── model/
│   ├── Lesson08ApiErrorResponse.java
│   ├── Lesson08FieldError.java
│   ├── Lesson08OrderReservationRequest.java
│   └── Lesson08OrderReservationResponse.java
└── service/
    ├── Lesson08InventoryReservationService.java
    └── Lesson08OutOfStockException.java
```

The test lives here:

```text
src/test/java/.../lesson08_error_handling/
└── Lesson08ErrorHandlingTest.java
```

## Why The Validation Starter Was Added

Spring MVC knows when to run validation, but the actual Bean Validation
implementation comes from the validation starter:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

That starter provides Jakarta Validation annotations such as `@NotBlank` and
`@Min`, plus Hibernate Validator as the validation engine.

Without this dependency, annotations may compile only if the API is present
somewhere else, but the application will not have the normal Spring Boot
validation setup.

## Class-Level Request Mapping

The controller starts with:

```java
@RequestMapping(path = "/lesson08/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
```

At the class level, `@RequestMapping` sets defaults shared by every handler
method in that controller.

`path = "/lesson08/reservations"` means:

```text
Every endpoint in this controller starts with /lesson08/reservations.
```

Because the `reserve(...)` method uses:

```java
@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
```

with no extra method-level path, the final endpoint is:

```text
POST /lesson08/reservations
```

`produces = MediaType.APPLICATION_JSON_VALUE` means:

```text
This controller sends JSON responses.
```

`MediaType.APPLICATION_JSON_VALUE` is Spring's constant for the string:

```text
application/json
```

The `_VALUE` version is used because `produces` expects a string value.

Important distinction:

- `produces`: what the endpoint sends back in the HTTP response.
- `consumes`: what the endpoint accepts in the HTTP request body.

So in this lesson:

```text
Client sends JSON request body
-> consumes = application/json

Controller sends JSON response body
-> produces = application/json
```

## Method-Level POST Mapping

The controller method starts with:

```java
@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
```

`@PostMapping` means:

```text
This method handles HTTP POST requests.
```

There is no path inside this `@PostMapping`, so the method uses the class-level
path:

```text
POST /lesson08/reservations
```

`consumes = MediaType.APPLICATION_JSON_VALUE` means:

```text
This endpoint accepts a request body with Content-Type: application/json.
```

So a matching request looks like this:

```text
POST /lesson08/reservations
Content-Type: application/json

{
  "sku": "sku-123",
  "quantity": 2,
  "customerId": "cust-9"
}
```

This pairs with `@RequestBody`:

```text
consumes = application/json
-> this endpoint accepts JSON input

@RequestBody
-> read that JSON input and convert it into a Java object
```

If the client sends a different content type, Spring MVC can reject the request
before this method body runs because the request does not match what the handler
method says it consumes.

## Request Validation

The request record declares the rules:

```java
public record Lesson08OrderReservationRequest(
        @NotBlank(message = "sku is required") String sku,
        @Min(value = 1, message = "quantity must be at least 1") int quantity,
        @NotBlank(message = "customerId is required") String customerId
) {
}
```

Those annotations describe what a valid request body must contain.

The controller turns those rules on with `@Valid`:

```java
public ResponseEntity<Lesson08OrderReservationResponse> reserve(
        @Valid @RequestBody Lesson08OrderReservationRequest request
) {
    ...
}
```

Read that as:

```text
@RequestBody
-> read JSON from the HTTP request body
-> convert JSON into Lesson08OrderReservationRequest

@Valid
-> validate the converted request object
-> if invalid, do not run the controller method body
-> throw MethodArgumentNotValidException
```

That last part is easy to miss. When request-body validation fails, the
controller method body does not run. Spring MVC stops before your service is
called.

## Location Header For Created Resources

On success, the controller does this:

```java
URI location = URI.create("/lesson08/reservations/" + response.reservationId());
return ResponseEntity.created(location).body(response);
```

`URI` is from Java's `java.net` package. A URI is a resource identifier. In this
lesson, it represents the URL path for the reservation that was just created.

`URI.create(...)` converts a string into a `URI` object:

```text
"/lesson08/reservations/"
+ response.reservationId()
= "/lesson08/reservations/L08-SKU-123-02-CUST-9"
```

`ResponseEntity.created(location)` uses that URI to build this part of the HTTP
response:

```text
Status:   201 Created
Header:   Location: /lesson08/reservations/L08-SKU-123-02-CUST-9
Body:     JSON reservation response
```

The `Location` header tells the client where the newly created resource can be
found.

This lesson does not implement a `GET /lesson08/reservations/{reservationId}`
endpoint yet. It is only showing the common REST pattern:

```text
POST creates something
-> return 201 Created
-> include Location header for the new resource
```

## Validation Failure Example

Request:

```text
POST /lesson08/reservations
Content-Type: application/json

{
  "sku": "",
  "quantity": 0,
  "customerId": ""
}
```

Response:

```json
{
  "status": 400,
  "error": "Bad Request",
  "code": "validation.failed",
  "message": "Request body did not pass validation",
  "path": "/lesson08/reservations",
  "fieldErrors": [
    {
      "field": "customerId",
      "message": "customerId is required"
    },
    {
      "field": "quantity",
      "message": "quantity must be at least 1"
    },
    {
      "field": "sku",
      "message": "sku is required"
    }
  ],
  "details": {}
}
```

The exact shape is controlled by `Lesson08ApiErrorResponse`, not by a random
exception string. That is the important design move.

## Domain Exception Example

Some requests are valid JSON and pass validation, but still cannot be fulfilled.

Example:

```json
{
  "sku": "OUT-OF-STOCK",
  "quantity": 2,
  "customerId": "CUST-9"
}
```

That request is structurally valid:

- `sku` is not blank
- `quantity` is at least 1
- `customerId` is not blank

But the service rejects it because the inventory rule says the item is not
available.

The service throws `Lesson08OutOfStockException`, and the API returns:

```json
{
  "status": 409,
  "error": "Conflict",
  "code": "inventory.out-of-stock",
  "message": "Only 0 item(s) are available for sku OUT-OF-STOCK",
  "path": "/lesson08/reservations",
  "fieldErrors": [],
  "details": {
    "sku": "OUT-OF-STOCK",
    "requestedQuantity": 2,
    "availableQuantity": 0
  }
}
```

`409 Conflict` is used because the request is well-formed, but it conflicts with
the current application state.

## Exception Data Vs Exception Cause

`Lesson08OutOfStockException` stores more than a message:

```java
private final String sku;
private final int requestedQuantity;
private final int availableQuantity;
```

Those fields are custom data carried by the exception object.

They are different from the exception's cause:

```text
exception.getCause()
-> another Throwable that caused this exception, if one exists

exception.getSku()
exception.getRequestedQuantity()
exception.getAvailableQuantity()
-> custom domain data stored directly on this exception
```

The flow is:

```text
Lesson08InventoryReservationService
-> throws new Lesson08OutOfStockException("OUT-OF-STOCK", 2, 0)

Spring MVC
-> catches the thrown exception as part of request handling
-> finds the matching @ExceptionHandler method
-> passes that same exception object into the handler

Lesson08ApiExceptionHandler
-> reads exception.getSku()
-> reads exception.getRequestedQuantity()
-> reads exception.getAvailableQuantity()
-> chooses which values to expose in the JSON error response
```

This is useful because the exception message can stay human-readable while the
extra fields remain structured and machine-readable.

One caution: just because an exception carries data does not mean an API should
always expose it. The handler is the gatekeeper. It decides what is safe and
helpful to return to the caller.

## `@RestControllerAdvice`

`@RestControllerAdvice` is the error-handling partner of `@RestController`.

It means:

```text
This class contains shared controller behavior.
Its handler method return values should be written directly to the response body.
```

Similar to Lesson 07:

```text
@RestControllerAdvice
= @ControllerAdvice
+ @ResponseBody
```

In this lesson, the advice handles exceptions from
`Lesson08OrderReservationController`:

```java
@RestControllerAdvice(assignableTypes = Lesson08OrderReservationController.class)
public class Lesson08ApiExceptionHandler {
    ...
}
```

The `assignableTypes` setting keeps the lesson scoped.

Without it:

```java
@RestControllerAdvice
public class Lesson08ApiExceptionHandler {
    ...
}
```

the advice is global. Its `@ExceptionHandler` methods can handle matching
exceptions from any controller in the application.

With it:

```java
@RestControllerAdvice(assignableTypes = Lesson08OrderReservationController.class)
public class Lesson08ApiExceptionHandler {
    ...
}
```

the advice only applies to `Lesson08OrderReservationController`.

In a real application, global advice is common because one app often wants one
consistent error response style.

In this learning lab, scoped advice is cleaner. It prevents Lesson 08's error
handler from accidentally changing the behavior of Lesson 07, Lesson 09, or any
future controller.

## `@ExceptionHandler`

`@ExceptionHandler` connects an exception type to a method:

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<Lesson08ApiErrorResponse> handleValidationFailure(...) {
    ...
}
```

Read that as:

```text
If Spring MVC sees MethodArgumentNotValidException,
call this method to build the HTTP response.
```

This lesson has two handlers:

- `MethodArgumentNotValidException`: validation failed before the controller body
  ran, so return `400 Bad Request`.
- `Lesson08OutOfStockException`: the request was valid, but the service could
  not fulfill it, so return `409 Conflict`.

## Why Not Just Let Spring Return Its Default Error?

Spring Boot can produce default error responses, and Spring Framework also has
`ProblemDetail` support for standard problem responses.

This lesson intentionally uses a small custom record because it makes the API
contract visible:

```java
public record Lesson08ApiErrorResponse(
        int status,
        String error,
        String code,
        String message,
        String path,
        List<Lesson08FieldError> fieldErrors,
        Map<String, Object> details
) {
}
```

When you own the response contract, tests and clients can rely on stable fields.

`ProblemDetail` is worth learning later, especially for production APIs that want
RFC-style problem responses. Here, the teaching goal is to see the shape clearly.

## Why No Vavr In This Lesson?

Vavr can be useful for service-layer failure values, such as `Either` or `Try`.
But Lesson 08 is mainly about Spring MVC's exception-to-response flow.

Adding Vavr here would mix two ideas:

- how Spring MVC catches and maps exceptions
- how functional result values model failures

So this lesson keeps failure handling in the Spring MVC style. A later
service-boundary lesson can compare exceptions with Vavr values more cleanly.

## What To Notice In The Test

The success test proves:

- a valid JSON body reaches the controller
- the controller calls the service
- the endpoint returns `201 Created`
- the `Location` header points to the created reservation

The validation test proves:

- invalid JSON data is rejected by Bean Validation
- the service is not needed for invalid input
- `@RestControllerAdvice` turns validation errors into JSON
- field errors are stable and predictable

The domain exception test proves:

- valid input can still fail in the service
- a domain exception can become an intentional HTTP status
- custom data stored on the exception is accessible later in `@ExceptionHandler`
- the error response contract stays the same shape

## Run This Lesson

Run only Lesson 08:

```bash
./mvnw -q -pl learning-apps/spring-boot-lab -Dtest=Lesson08ErrorHandlingTest test
```

Run the whole Spring Boot lab:

```bash
./mvnw -q -pl learning-apps/spring-boot-lab test
```

## Main Takeaway

Validation and exception handling are part of the API contract.

Good REST APIs do not merely fail. They fail in a shape the caller can
understand and handle.

## Official References

- [Spring Boot validation](https://docs.spring.io/spring-boot/reference/io/validation.html)
- [Spring Boot starters](https://docs.spring.io/spring-boot/4.0/reference/using/build-systems.html)
- [Spring MVC validation](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-validation.html)
- [Spring MVC controller advice](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-advice.html)
