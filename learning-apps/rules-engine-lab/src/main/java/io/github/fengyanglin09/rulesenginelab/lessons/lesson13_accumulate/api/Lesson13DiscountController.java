package io.github.fengyanglin09.rulesenginelab.lessons.lesson13_accumulate.api;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson13_accumulate.model.Lesson13DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson13_accumulate.model.Lesson13DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson13_accumulate.service.Lesson13DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP entry point for Lesson 13.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rules-engine/lessons/13/discount")
public class Lesson13DiscountController {

    private final Lesson13DiscountService discountService;

    /**
     * Runs Lesson 13 rules and calculates cart totals with accumulate.
     *
     * @param request JSON body converted into the lesson request model
     * @return result containing calculated total and discount
     */
    @PostMapping
    public Lesson13DiscountResult calculate(@RequestBody Lesson13DiscountRequest request) {
        return discountService.calculate(request);
    }
}
