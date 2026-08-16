package io.github.fengyanglin09.springbootlab.lessons.lesson07_rest_apis_spring_mvc.api;

import io.github.fengyanglin09.springbootlab.lessons.lesson07_rest_apis_spring_mvc.model.Lesson07ShippingOptionResponse;
import io.github.fengyanglin09.springbootlab.lessons.lesson07_rest_apis_spring_mvc.model.Lesson07ShippingQuoteRequest;
import io.github.fengyanglin09.springbootlab.lessons.lesson07_rest_apis_spring_mvc.model.Lesson07ShippingQuoteResponse;
import io.github.fengyanglin09.springbootlab.lessons.lesson07_rest_apis_spring_mvc.service.Lesson07ShippingQuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * The HTTP entry point for Lesson 07.
 *
 * <p>This is a REST controller, not a page-rendering controller. Its job is to
 * map HTTP requests into Java method calls and map Java return values directly
 * back into HTTP response bodies, usually JSON. The shipping calculation itself
 * stays in Lesson07ShippingQuoteService.</p>
 */
/*
 * @RestController is from Spring MVC.
 *
 * It is different from a regular @Controller.
 *
 * A regular @Controller is often used for server-rendered pages. If a method in
 * a regular @Controller returns a String such as "profile", Spring MVC may
 * treat that String as the name of a view/template to render.
 *
 * A @RestController is for REST/API endpoints. It combines two ideas:
 *
 * - @Controller: this class has request handler methods
 * - @ResponseBody: returned Java objects should be written to the HTTP response
 *   body, usually as JSON when Jackson is on the classpath
 *
 * That means this controller can return Lesson07ShippingOptionResponse or
 * Lesson07ShippingQuoteResponse records directly, and Spring MVC writes those
 * records as JSON instead of looking for an HTML page/template.
 *
 * Because the class is under SpringBootLabApplication's root package, component
 * scanning finds it and registers it as a Spring bean.
 */
@RestController
/*
 * @RequestMapping is from Spring MVC.
 *
 * At the class level, it provides a shared URL prefix and shared response media
 * type for all handler methods in this controller.
 *
 * path = "/lesson07/shipping"
 *     -> every endpoint in this class starts with /lesson07/shipping
 *
 * produces = application/json
 *     -> handler methods return JSON responses
 */
@RequestMapping(path = "/lesson07/shipping", produces = MediaType.APPLICATION_JSON_VALUE)
/*
 * Lombok generates a constructor for the final service field. Spring uses that
 * constructor to inject Lesson07ShippingQuoteService into the controller.
 */
@RequiredArgsConstructor
public class Lesson07ShippingQuoteController {

    /*
     * The REST controller depends on a service instead of doing all work
     * itself.
     *
     * That keeps HTTP concerns here and business-ish decisions in the service.
     * In real apps, this separation makes controllers easier to read and test.
     */
    private final Lesson07ShippingQuoteService quoteService;

    /*
     * @GetMapping is a shortcut for @RequestMapping(method = GET).
     *
     * This endpoint demonstrates reading information from the URL path. It does
     * not need a request body because GET requests usually ask for existing
     * information.
     */
    @GetMapping("/options/{destination}")
    public Lesson07ShippingOptionResponse optionForDestination(
            /*
             * @PathVariable is from Spring MVC.
             *
             * It takes the {destination} part of the URL and passes it into this
             * Java parameter.
             *
             * Example request:
             *
             * GET /lesson07/shipping/options/austin
             *
             * Parameter value:
             *
             * destination = "austin"
             */
            @PathVariable("destination") String destination
    ) {
        /*
         * Returning a record object is enough for a REST controller. Spring MVC
         * and Jackson convert it to JSON for the HTTP response body.
         */
        return quoteService.optionFor(destination);
    }

    /*
     * @PostMapping is a shortcut for @RequestMapping(method = POST).
     *
     * This endpoint demonstrates creating a new resource-like result from a JSON
     * request body. It consumes JSON and returns JSON.
     */
    @PostMapping(path = "/quotes", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Lesson07ShippingQuoteResponse> createQuote(
            /*
             * @RequestBody is from Spring MVC.
             *
             * It tells Spring MVC to read the HTTP request body and convert the
             * JSON into a Lesson07ShippingQuoteRequest object. Jackson performs
             * the JSON-to-record conversion.
             *
             * Example JSON:
             *
             * {
             *   "destination": "austin",
             *   "itemCount": 3,
             *   "expedited": true
             * }
             */
            @RequestBody Lesson07ShippingQuoteRequest request
    ) {
        Lesson07ShippingQuoteResponse response = quoteService.createQuote(request);

        /*
         * ResponseEntity is from Spring Framework.
         *
         * Returning the response object directly would produce HTTP 200 OK.
         * Here, the endpoint is creating a new quote, so the lesson uses
         * ResponseEntity.created(...) to return:
         *
         * - HTTP status 201 Created
         * - Location header pointing at the created quote URL
         * - JSON response body containing the quote details
         */
        URI location = URI.create("/lesson07/shipping/quotes/" + response.quoteId());
        return ResponseEntity.created(location).body(response);
    }
}
