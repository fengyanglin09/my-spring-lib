package io.github.fengyanglin09.springbootlab.lessons.lesson07_rest_apis_spring_mvc;

import io.github.fengyanglin09.springbootlab.lessons.lesson07_rest_apis_spring_mvc.api.Lesson07ShippingQuoteController;
import io.github.fengyanglin09.springbootlab.lessons.lesson07_rest_apis_spring_mvc.model.Lesson07ShippingQuoteRequest;
import io.github.fengyanglin09.springbootlab.lessons.lesson07_rest_apis_spring_mvc.service.Lesson07ShippingQuoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Lesson 07's executable proof.
 *
 * <p>This test exercises the controller through Spring MVC instead of directly
 * calling controller methods. That means request mapping, path variables,
 * JSON conversion, HTTP status codes, and headers are all part of the test.</p>
 */
/*
 * @WebMvcTest is from Spring Boot's Spring MVC test support.
 *
 * It creates a focused MVC test context instead of the whole application
 * context. Spring Boot configures the Spring MVC infrastructure, Jackson, and
 * MockMvc, then limits component scanning to MVC-related beans.
 *
 * This is useful for Lesson 07 because the lesson question is:
 *
 * "Does the HTTP boundary behave correctly?"
 *
 * not:
 *
 * "Can the entire application start?"
 */
@WebMvcTest(controllers = Lesson07ShippingQuoteController.class)
/*
 * @Import is from Spring Framework.
 *
 * @WebMvcTest loads the controller but does not automatically load every
 * @Service bean. The controller needs Lesson07ShippingQuoteService, so the test
 * imports the real service explicitly.
 *
 * Later lessons can show mock collaborators. Here, using the real tiny service
 * keeps the API flow easier to follow.
 */
@Import(Lesson07ShippingQuoteService.class)
class Lesson07RestApisSpringMvcTest {

    /*
     * MockMvc is from Spring Test.
     *
     * It lets the test perform HTTP-like requests through Spring MVC without
     * starting a real server or opening a network port. The request still goes
     * through DispatcherServlet and controller method mapping.
     */
    @Autowired
    private MockMvc mockMvc;

    /*
     * ObjectMapper is from Jackson.
     *
     * In this Spring Boot 4 project, Jackson 3 uses the tools.jackson package
     * name. Older Spring Boot 3 examples often import ObjectMapper from
     * com.fasterxml.jackson.databind instead.
     *
     * Spring Boot configures it for JSON serialization/deserialization. The test
     * uses the same mapper to turn a request record into JSON before sending the
     * POST request.
     */
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getShippingOptionReadsPathVariableAndReturnsJson() throws Exception {
        /*
         * This simulates:
         *
         * GET /lesson07/shipping/options/austin
         *
         * No real HTTP server starts. MockMvc builds a mock request and sends it
         * through Spring MVC's DispatcherServlet.
         */
        mockMvc.perform(get("/lesson07/shipping/options/{destination}", "austin"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.destination").value("austin"))
                .andExpect(jsonPath("$.supportsExpedited").value(true))
                .andExpect(jsonPath("$.standardDays").value(5))
                .andExpect(jsonPath("$.expeditedDays").value(2));
    }

    @Test
    void postShippingQuoteReadsJsonRequestBodyAndReturnsCreatedResponse() throws Exception {
        Lesson07ShippingQuoteRequest request = new Lesson07ShippingQuoteRequest("austin", 3, true);

        /*
         * objectMapper.writeValueAsString(...) converts the Java record into the
         * same kind of JSON a real HTTP client would send.
         */
        String jsonRequest = objectMapper.writeValueAsString(request);

        /*
         * This simulates:
         *
         * POST /lesson07/shipping/quotes
         * Content-Type: application/json
         *
         * {
         *   "destination": "austin",
         *   "itemCount": 3,
         *   "expedited": true
         * }
         *
         * The controller should respond with:
         *
         * - HTTP 201 Created
         * - Location header for the created quote
         * - JSON response body
         */
        mockMvc.perform(post("/lesson07/shipping/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/lesson07/shipping/quotes/L07-AUSTIN-03-EXP"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.quoteId").value("L07-AUSTIN-03-EXP"))
                .andExpect(jsonPath("$.destination").value("austin"))
                .andExpect(jsonPath("$.itemCount").value(3))
                .andExpect(jsonPath("$.expedited").value(true))
                .andExpect(jsonPath("$.estimatedDays").value(2))
                .andExpect(jsonPath("$.estimatedCost").value(18.49));
    }
}
