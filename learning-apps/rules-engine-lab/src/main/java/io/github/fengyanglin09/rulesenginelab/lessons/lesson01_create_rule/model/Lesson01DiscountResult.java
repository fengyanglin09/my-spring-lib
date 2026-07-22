package io.github.fengyanglin09.rulesenginelab.lessons.lesson01_create_rule.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Output fact for Lesson 01.
 *
 * <p>The service inserts this object into the rule unit before firing rules.
 * The DRL rule modifies this same object, and the service returns it after
 * {@code instance.fire()} completes.</p>
 */
@Getter
@Setter
public class Lesson01DiscountResult {
    /**
     * Discount percentage chosen by the rule. Defaults to {@code 0} if no rule
     * matches.
     */
    private int discountPercent;

    /**
     * Human-readable reason set by the rule. Remains {@code null} if no rule
     * matches.
     */
    private String reason;
}
