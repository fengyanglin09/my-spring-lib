package io.github.fengyanglin09.rulesenginelab.lessons.lesson09_phase_execution.api;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson09_phase_execution.model.Lesson09DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson09_phase_execution.model.Lesson09DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson09_phase_execution.service.Lesson09DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP entry point for Lesson 09.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rules-engine/lessons/09/discount")
public class Lesson09DiscountController {

    private final Lesson09DiscountService discountService;

    /**
     * Runs Lesson 09 rules in validation and discount phases.
     *
     * @param request JSON body converted into the lesson request model
     * @return result containing validation state, discount, and rule trace
     */
    @PostMapping
    public Lesson09DiscountResult calculate(@RequestBody Lesson09DiscountRequest request) {
        return discountService.calculate(request);
    }
}
