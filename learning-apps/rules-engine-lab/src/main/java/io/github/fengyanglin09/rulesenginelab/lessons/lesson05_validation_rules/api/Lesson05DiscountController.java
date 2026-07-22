package io.github.fengyanglin09.rulesenginelab.lessons.lesson05_validation_rules.api;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson05_validation_rules.model.Lesson05DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson05_validation_rules.model.Lesson05DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson05_validation_rules.service.Lesson05DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP entry point for Lesson 05.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rules-engine/lessons/05/discount")
public class Lesson05DiscountController {

    private final Lesson05DiscountService discountService;

    /**
     * Runs Lesson 05 validation and discount rules.
     *
     * @param request JSON body converted into the lesson request model
     * @return result containing validation state and any selected discount
     */
    @PostMapping
    public Lesson05DiscountResult calculate(@RequestBody Lesson05DiscountRequest request) {
        return discountService.calculate(request);
    }
}
