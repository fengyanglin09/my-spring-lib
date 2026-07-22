package io.github.fengyanglin09.rulesenginelab.lessons.lesson02_multiple_rules.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Output fact for Lesson 02.
 *
 * <p>Each matching rule modifies this same result object. In this lesson, the
 * sample rules are written so the test cases usually match one rule at a time.
 * Later lessons intentionally create overlapping matches.</p>
 */
@Getter
@Setter
public class Lesson02DiscountResult {
    /**
     * Discount percentage selected by whichever rule modifies the result.
     */
    private int discountPercent;

    /**
     * Short explanation of the matched business rule.
     */
    private String reason;
}
