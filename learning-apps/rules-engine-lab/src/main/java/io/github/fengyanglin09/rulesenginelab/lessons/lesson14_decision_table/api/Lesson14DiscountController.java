package io.github.fengyanglin09.rulesenginelab.lessons.lesson14_decision_table.api;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson14_decision_table.model.Lesson14DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson14_decision_table.model.Lesson14DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson14_decision_table.service.Lesson14DiscountService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP entry point for Lesson 14.
 */
@RestController
@RequestMapping("/lessons/14/discount")
public class Lesson14DiscountController {

    private final Lesson14DiscountService discountService;

    /**
     * Creates the controller with the lesson service.
     *
     * @param discountService service that runs Lesson 14 rules
     */
    public Lesson14DiscountController(Lesson14DiscountService discountService) {
        this.discountService = discountService;
    }

    /**
     * Calculates a discount using CSV-backed decision rows.
     *
     * @param request discount request body
     * @return rule result
     */
    @PostMapping
    public Lesson14DiscountResult calculate(@RequestBody Lesson14DiscountRequest request) {
        return discountService.calculate(request);
    }
}
