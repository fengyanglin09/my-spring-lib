package io.github.fengyanglin09.rulesenginelab.lessons.lesson04_activation_group.api;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson04_activation_group.model.Lesson04DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson04_activation_group.model.Lesson04DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson04_activation_group.service.Lesson04DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP entry point for Lesson 04.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rules-engine/lessons/04/discount")
public class Lesson04DiscountController {

    private final Lesson04DiscountService discountService;

    /**
     * Runs the Lesson 04 activation-group demo rules.
     *
     * @param request JSON body converted into the lesson request model
     * @return result containing the one winning discount rule
     */
    @PostMapping
    public Lesson04DiscountResult calculate(@RequestBody Lesson04DiscountRequest request) {
        return discountService.calculate(request);
    }
}
