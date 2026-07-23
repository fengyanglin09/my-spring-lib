package io.github.fengyanglin09.rulesenginelab.lessons.lesson12_candidate_selection.api;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson12_candidate_selection.model.Lesson12DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson12_candidate_selection.model.Lesson12DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson12_candidate_selection.service.Lesson12DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP entry point for Lesson 12.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rules-engine/lessons/12/discount")
public class Lesson12DiscountController {

    private final Lesson12DiscountService discountService;

    /**
     * Runs Lesson 12 rules and selects the best discount candidate.
     *
     * @param request JSON body converted into the lesson request model
     * @return result containing candidates and the selected winner
     */
    @PostMapping
    public Lesson12DiscountResult calculate(@RequestBody Lesson12DiscountRequest request) {
        return discountService.calculate(request);
    }
}
