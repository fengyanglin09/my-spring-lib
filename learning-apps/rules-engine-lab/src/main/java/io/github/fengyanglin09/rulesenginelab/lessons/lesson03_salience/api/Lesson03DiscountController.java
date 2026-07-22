package io.github.fengyanglin09.rulesenginelab.lessons.lesson03_salience.api;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson03_salience.model.Lesson03DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson03_salience.model.Lesson03DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson03_salience.service.Lesson03DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP entry point for Lesson 03.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rules-engine/lessons/03/discount")
public class Lesson03DiscountController {

    private final Lesson03DiscountService discountService;

    /**
     * Runs the Lesson 03 salience demo rules.
     *
     * @param request JSON body converted into the lesson request model
     * @return result including final discount and firing order
     */
    @PostMapping
    public Lesson03DiscountResult calculate(@RequestBody Lesson03DiscountRequest request) {
        return discountService.calculate(request);
    }
}
