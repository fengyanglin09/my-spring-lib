# Lesson 07: REST APIs With `@RestController`

## What This Lesson Teaches

This lesson teaches the first real HTTP boundary in the Spring Boot lab, with
special focus on `@RestController`.

That detail matters because `@RestController` is different from a regular
`@Controller`.

The main idea is:

```text
HTTP request
-> Spring MVC receives it
-> Spring MVC chooses a @RestController method
-> the @RestController method calls a service
-> the service returns ordinary Java data
-> Spring MVC writes that data as an HTTP response, usually JSON
```

Spring Boot makes this feel small because it auto-configures most of the web
infrastructure after the project adds the Spring MVC starter.

## Objective

Build REST endpoints with Spring MVC, understand why REST APIs usually use
`@RestController`, and keep HTTP concerns separate from application logic.

In this lesson, the REST controller owns HTTP details:

- URL paths
- HTTP methods
- request bodies
- response status codes
- response headers
- JSON response bodies

The service owns the application decision:

- what shipping options are available
- how quote cost is calculated
- how the quote id is built

That split is important. A controller should feel like a translator between the
outside HTTP world and the inside Java application.

## `@RestController` Vs `@Controller`

Both annotations are from Spring MVC, but they are usually used for different
kinds of web endpoints.

- `@Controller`: commonly used when the method returns a view name, such as an
  HTML template to render.
- `@RestController`: commonly used when the method returns data directly, such
  as JSON for a REST API.

The important shortcut is:

```text
@RestController
= @Controller
+ @ResponseBody on every request handler method
```

`@ResponseBody` means:

```text
Do not treat the return value as a page/view name.
Write the return value directly into the HTTP response body.
```

So with a regular `@Controller`, this method is usually interpreted as returning
a view name:

```java
@Controller
class PageController {

    @GetMapping("/profile")
    String profilePage() {
        return "profile";
    }
}
```

That means Spring MVC may look for a view/template named `profile`.

With `@RestController`, this method is interpreted as returning response data:

```java
@RestController
class ApiController {

    @GetMapping("/api/profile")
    ProfileResponse profile() {
        return new ProfileResponse("Ada");
    }
}
```

That means Spring MVC writes the returned object to the HTTP response body. With
Jackson available, the object becomes JSON.

You can also build a REST API with regular `@Controller`, but then you must add
`@ResponseBody` yourself:

```java
@Controller
class ApiController {

    @ResponseBody
    @GetMapping("/api/profile")
    ProfileResponse profile() {
        return new ProfileResponse("Ada");
    }
}
```

That works, but `@RestController` is the clearer annotation when every handler
in the class is meant to return API data.

## Should `@Controller` Be A Separate Lesson?

For this Spring Boot learning path, the answer is:

```text
Learn the difference here.
Make a separate @Controller lesson only if we want to study server-rendered web pages.
```

That is because `@Controller` is not just a smaller version of
`@RestController`. Once you use regular `@Controller` in its common style, the
lesson naturally becomes about a different part of Spring MVC:

- returning view names
- rendering HTML templates
- using `Model` to pass data into pages
- handling browser form submissions
- redirecting after a form post
- configuring a template engine such as Thymeleaf

Those are important skills for apps that render HTML on the server. They are not
the center of a backend REST API path.

So Lesson 07 keeps the comparison visible because it helps you choose the right
annotation:

- Use `@RestController` when building JSON APIs.
- Use `@Controller` when building page-rendering MVC flows.
- Use `@Controller` plus `@ResponseBody` only when you specifically want regular
  controller behavior but one method should return data directly.

If this lab later adds a server-rendered MVC branch, `@Controller` deserves its
own lesson there. For now, treating it as an important contrast inside Lesson 07
is the cleanest fit.

## Files

```text
lesson07_rest_apis_spring_mvc/
├── README.md
├── package-info.java
├── api/
│   └── Lesson07ShippingQuoteController.java
├── model/
│   ├── Lesson07ShippingOptionResponse.java
│   ├── Lesson07ShippingQuoteRequest.java
│   └── Lesson07ShippingQuoteResponse.java
└── service/
    └── Lesson07ShippingQuoteService.java
```

The test lives here:

```text
src/test/java/.../lesson07_rest_apis_spring_mvc/
└── Lesson07RestApisSpringMvcTest.java
```

## Why The Web MVC Starter Was Added

Lesson 01-06 can run as non-web Spring Boot code. Lesson 07 needs Spring MVC, so
the module now includes:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>
```

That starter brings in the web framework pieces used by this lesson, including
Spring MVC, JSON support, and the default embedded servlet web stack.

The test dependency is:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc-test</artifactId>
    <scope>test</scope>
</dependency>
```

That gives the lesson `MockMvc`, `@WebMvcTest`, and web-MVC-specific test
support.

## Endpoint 1: Read A Path Variable

```text
GET /lesson07/shipping/options/austin
```

This request means:

```text
HTTP method: GET
Path:        /lesson07/shipping/options/austin
Path value:  destination = "austin"
Body:        none
Response:    JSON shipping option data
Status:      200 OK
```

In the controller:

```java
@GetMapping("/options/{destination}")
public Lesson07ShippingOptionResponse optionForDestination(
        @PathVariable("destination") String destination
) {
    return quoteService.optionFor(destination);
}
```

`{destination}` is a placeholder in the URL pattern. `@PathVariable` tells
Spring MVC to copy that part of the URL into the Java method parameter.

## Endpoint 2: Read A JSON Body

```text
POST /lesson07/shipping/quotes
Content-Type: application/json

{
  "destination": "austin",
  "itemCount": 3,
  "expedited": true
}
```

This request means:

```text
HTTP method: POST
Path:        /lesson07/shipping/quotes
Body:        JSON quote request
Response:    JSON quote result
Status:      201 Created
Header:      Location: /lesson07/shipping/quotes/L07-AUSTIN-03-EXP
```

In the controller:

```java
@PostMapping(path = "/quotes", consumes = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<Lesson07ShippingQuoteResponse> createQuote(
        @RequestBody Lesson07ShippingQuoteRequest request
) {
    Lesson07ShippingQuoteResponse response = quoteService.createQuote(request);
    URI location = URI.create("/lesson07/shipping/quotes/" + response.quoteId());
    return ResponseEntity.created(location).body(response);
}
```

`@RequestBody` tells Spring MVC to read the HTTP request body. Jackson converts
the JSON fields into the `Lesson07ShippingQuoteRequest` record before the
controller method runs.

`ResponseEntity` is used when the response needs more than a body. This lesson
uses it to return:

- `201 Created`
- a `Location` header
- a JSON response body

Returning the response record directly would also work, but Spring MVC would
usually use `200 OK`. Since quote creation is more naturally a create-style
operation, the lesson intentionally shows `ResponseEntity.created(...)`.

## Important Spring Annotations

- `@RestController`: Marks the class as a Spring MVC controller where method
  return values are written directly to the HTTP response body. With JSON
  support available, records are serialized into JSON.

- `@RequestMapping`: Defines shared request mapping information. At class level
  in this lesson, it sets the shared path prefix `/lesson07/shipping` and says
  the controller produces JSON.

- `@GetMapping`: Shortcut for a request mapping that handles HTTP `GET`.

- `@PostMapping`: Shortcut for a request mapping that handles HTTP `POST`.

- `@PathVariable`: Reads a value from the URL path.

- `@RequestBody`: Reads the HTTP request body and converts it into a Java object.

- `@Service`: Marks a class as an application service bean. Spring component
  scanning finds it, creates it, and makes it injectable into other beans such
  as controllers.

## Records As API Models

This lesson uses Java records for request and response models:

```java
public record Lesson07ShippingQuoteRequest(
        String destination,
        int itemCount,
        boolean expedited
) {
}
```

A record is a compact immutable data carrier. The component names become useful
in two directions:

- incoming JSON field `destination` maps to Java component `destination`
- Java component `destination` serializes back to JSON field `destination`

This makes records a good fit for small API request/response shapes.

## Testing With MockMvc

The test uses:

```java
@WebMvcTest(controllers = Lesson07ShippingQuoteController.class)
@Import(Lesson07ShippingQuoteService.class)
```

`@WebMvcTest` creates a focused web test context. It does not start the whole
application and it does not open a network port. Instead, Spring Boot configures
Spring MVC test infrastructure and gives the test a `MockMvc` object.

`MockMvc` lets the test send HTTP-like requests through Spring MVC:

```java
mockMvc.perform(get("/lesson07/shipping/options/{destination}", "austin"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.destination").value("austin"));
```

The request still goes through the Spring MVC request mapping machinery, but it
runs inside the test process.

The test also injects Jackson's `ObjectMapper` so it can convert the request
record into JSON before calling the POST endpoint. This project uses Spring Boot
4, so the Jackson 3 import is `tools.jackson.databind.ObjectMapper`. Older
Spring Boot 3 examples usually show `com.fasterxml.jackson.databind.ObjectMapper`.

`@Import(Lesson07ShippingQuoteService.class)` is needed because `@WebMvcTest`
loads MVC-related beans, not every application service. The controller needs the
real tiny service, so the test imports it explicitly.

## `@WebMvcTest` Vs `@SpringBootTest`

Use `@WebMvcTest` when the question is:

```text
Does this controller map HTTP requests and responses correctly?
```

Use `@SpringBootTest` when the question is:

```text
Can the whole application context start and work together?
```

Lesson 07 uses `@WebMvcTest` because the focus is the HTTP boundary. Later
lessons can use broader integration tests when more layers need to work
together.

## What To Notice In The Test

The GET test proves:

- `/options/{destination}` matches the URL
- `@PathVariable` receives `austin`
- the response status is `200 OK`
- the response body is JSON
- the JSON fields match the response record

The POST test proves:

- JSON can be sent as a request body
- `@RequestBody` converts JSON into a request record
- the controller returns `201 Created`
- the `Location` header is present
- the response body contains the quote result

## Run This Lesson

Run only Lesson 07:

```bash
./mvnw -q -pl learning-apps/spring-boot-lab -Dtest=Lesson07RestApisSpringMvcTest test
```

Run the whole Spring Boot lab:

```bash
./mvnw -q -pl learning-apps/spring-boot-lab test
```

## Main Takeaway

Spring MVC lets you write normal Java methods and connect them to HTTP:

```text
URL + method + optional body
-> controller method
-> service method
-> Java response object
-> JSON HTTP response
```

The controller should explain how HTTP talks to the app. The service should
explain what the app decides.

## Official References

- [Spring MVC annotated controllers](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller.html)
- [Spring MVC mapping requests](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-requestmapping.html)
- [Spring MVC Test MockMvc](https://docs.spring.io/spring-framework/reference/testing/mockmvc.html)
- [Spring Boot testing Spring Boot applications](https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html)
