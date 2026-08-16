/**
 * Lesson 07 objective: build REST endpoints with Spring MVC and understand why
 * REST APIs usually use {@code @RestController} instead of a regular
 * {@code @Controller}.
 *
 * <p>Study path:</p>
 *
 * <ol>
 *     <li>Add Spring MVC to the lab with the Boot web MVC starter.</li>
 *     <li>Create a {@code @RestController} as the HTTP boundary.</li>
 *     <li>Compare {@code @RestController} with regular {@code @Controller}:
 *     {@code @RestController} writes return values to the response body,
 *     while {@code @Controller} is commonly used for page/view rendering unless
 *     {@code @ResponseBody} is added.</li>
 *     <li>Treat {@code @Controller} as an important contrast here, but leave
 *     full page-rendering MVC topics such as templates, {@code Model}, forms,
 *     and redirects for a possible future server-rendered MVC lesson.</li>
 *     <li>Use {@code @RequestMapping}, {@code @GetMapping}, and
 *     {@code @PostMapping} to map URLs and HTTP methods.</li>
 *     <li>Use {@code @PathVariable} for values from the URL path.</li>
 *     <li>Use {@code @RequestBody} for JSON request bodies.</li>
 *     <li>Return JSON response records and a {@code ResponseEntity} with
 *     status code and Location header.</li>
 *     <li>Test the API through Spring MVC with {@code MockMvc}.</li>
 * </ol>
 */
package io.github.fengyanglin09.springbootlab.lessons.lesson07_rest_apis_spring_mvc;
