package io.github.fengyanglin09.rulesenginelab.lessons.lesson07_spring_properties.api;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson07_spring_properties.model.Lesson07DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson07_spring_properties.model.Lesson07DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson07_spring_properties.service.Lesson07DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP entry point for Lesson 07.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rules-engine/lessons/07/discount")
public class Lesson07DiscountController {

    private final Lesson07DiscountService discountService;

    /**
     * Runs Lesson 07 rules using Spring-loaded discount properties.
     *
     * @param request JSON body converted into the lesson request model
     * @return result containing validation state and any selected discount
     */
    @PostMapping
    public Lesson07DiscountResult calculate(@RequestBody Lesson07DiscountRequest request) {
        return discountService.calculate(request);
    }
}
