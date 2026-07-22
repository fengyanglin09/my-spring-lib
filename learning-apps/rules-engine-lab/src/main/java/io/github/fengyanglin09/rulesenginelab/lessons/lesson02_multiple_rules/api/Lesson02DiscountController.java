package io.github.fengyanglin09.rulesenginelab.lessons.lesson02_multiple_rules.api;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson02_multiple_rules.model.Lesson02DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson02_multiple_rules.model.Lesson02DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson02_multiple_rules.service.Lesson02DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP entry point for Lesson 02.
 *
 * <p>Swagger can use this endpoint to exercise the three-rule DRL file without
 * mixing it with later lessons.</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rules-engine/lessons/02/discount")
public class Lesson02DiscountController {

    private final Lesson02DiscountService discountService;

    /**
     * Runs the Lesson 02 discount rules.
     *
     * @param request JSON body converted into the lesson request model
     * @return the result after Drools fires any matching rule
     */
    @PostMapping
    public Lesson02DiscountResult calculate(@RequestBody Lesson02DiscountRequest request) {
        return discountService.calculate(request);
    }
}
