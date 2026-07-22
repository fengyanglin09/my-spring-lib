package io.github.fengyanglin09.rulesenginelab.lessons.lesson01_create_rule.api;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson01_create_rule.model.Lesson01DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson01_create_rule.model.Lesson01DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson01_create_rule.service.Lesson01DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP entry point for Lesson 01.
 *
 * <p>The controller does not know anything about Drools. Its job is only to
 * accept a JSON request, call the lesson service, and return the result. This
 * keeps the web layer separate from the rule-engine layer.</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rules-engine/lessons/01/discount")
public class Lesson01DiscountController {

    private final Lesson01DiscountService discountService;

    /**
     * Runs the first lesson discount rule.
     *
     * @param request JSON body converted into the lesson request model
     * @return the result after Drools has had a chance to modify it
     */
    @PostMapping
    public Lesson01DiscountResult calculate(@RequestBody Lesson01DiscountRequest request) {
        return discountService.calculate(request);
    }
}
