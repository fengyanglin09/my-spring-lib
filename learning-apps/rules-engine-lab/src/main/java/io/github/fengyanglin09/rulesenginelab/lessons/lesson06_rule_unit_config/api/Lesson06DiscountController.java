package io.github.fengyanglin09.rulesenginelab.lessons.lesson06_rule_unit_config.api;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson06_rule_unit_config.model.Lesson06DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson06_rule_unit_config.model.Lesson06DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson06_rule_unit_config.service.Lesson06DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP entry point for Lesson 06.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rules-engine/lessons/06/discount")
public class Lesson06DiscountController {

    private final Lesson06DiscountService discountService;

    /**
     * Runs Lesson 06 validation and configurable discount rules.
     *
     * @param request JSON body converted into the lesson request model
     * @return result containing validation state and any selected discount
     */
    @PostMapping
    public Lesson06DiscountResult calculate(@RequestBody Lesson06DiscountRequest request) {
        return discountService.calculate(request);
    }
}
