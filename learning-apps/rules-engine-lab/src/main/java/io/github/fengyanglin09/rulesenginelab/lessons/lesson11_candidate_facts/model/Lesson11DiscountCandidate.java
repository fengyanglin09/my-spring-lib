package io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Candidate discount fact created by Lesson 11 rules.
 *
 * <p>A candidate means "this discount is possible." It does not mean the
 * discount has been selected. Lesson 12 will choose the winning candidate.</p>
 */
@Getter
@AllArgsConstructor
public class Lesson11DiscountCandidate {
    /**
     * Rule that proposed this candidate.
     */
    private final String ruleName;

    /**
     * Proposed discount percentage.
     */
    private final int discountPercent;

    /**
     * Proposed result reason.
     */
    private final String reason;
}
