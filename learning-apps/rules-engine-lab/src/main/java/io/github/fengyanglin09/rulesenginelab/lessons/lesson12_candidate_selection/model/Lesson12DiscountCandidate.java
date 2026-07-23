package io.github.fengyanglin09.rulesenginelab.lessons.lesson12_candidate_selection.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Candidate discount fact created by Lesson 12 rules.
 *
 * <p>Rules create candidates. Java selects the winning candidate after all
 * candidate facts have been queried.</p>
 */
@Getter
@AllArgsConstructor
public class Lesson12DiscountCandidate {
    private final String ruleName;
    private final int discountPercent;
    private final String reason;
}
