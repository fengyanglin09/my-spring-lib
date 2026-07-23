package io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.api;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.model.Lesson11DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.model.Lesson11DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.service.Lesson11DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP entry point for Lesson 11.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rules-engine/lessons/11/discount")
public class Lesson11DiscountController {

    private final Lesson11DiscountService discountService;

    /**
     * Runs Lesson 11 rules and returns generated discount candidates.
     *
     * @param request JSON body converted into the lesson request model
     * @return result containing validation state, trace, and candidate facts
     */
    @PostMapping
    public Lesson11DiscountResult calculate(@RequestBody Lesson11DiscountRequest request) {
        return discountService.calculate(request);
    }
}
