package io.github.fengyanglin09.rulesenginelab.lessons.lesson10_queries.api;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson10_queries.model.Lesson10DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson10_queries.model.Lesson10DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson10_queries.service.Lesson10DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP entry point for Lesson 10.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rules-engine/lessons/10/discount")
public class Lesson10DiscountController {

    private final Lesson10DiscountService discountService;

    /**
     * Runs Lesson 10 rules and returns query-driven audit messages.
     *
     * @param request JSON body converted into the lesson request model
     * @return result containing validation state, discount, trace, and audits
     */
    @PostMapping
    public Lesson10DiscountResult calculate(@RequestBody Lesson10DiscountRequest request) {
        return discountService.calculate(request);
    }
}
