package io.github.fengyanglin09.rulesenginelab.lessons.lesson08_rule_tracing.api;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson08_rule_tracing.model.Lesson08DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson08_rule_tracing.model.Lesson08DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson08_rule_tracing.service.Lesson08DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP entry point for Lesson 08.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rules-engine/lessons/08/discount")
public class Lesson08DiscountController {

    private final Lesson08DiscountService discountService;

    /**
     * Runs Lesson 08 rules with listener-based tracing.
     *
     * @param request JSON body converted into the lesson request model
     * @return result containing matched and fired rule traces
     */
    @PostMapping
    public Lesson08DiscountResult calculate(@RequestBody Lesson08DiscountRequest request) {
        return discountService.calculate(request);
    }
}
