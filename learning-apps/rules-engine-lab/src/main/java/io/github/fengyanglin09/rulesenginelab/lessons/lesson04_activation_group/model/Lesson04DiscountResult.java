package io.github.fengyanglin09.rulesenginelab.lessons.lesson04_activation_group.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Output fact for Lesson 04.
 *
 * <p>The {@code firedRules} list should contain only one discount rule when
 * multiple rules in the same activation group match.</p>
 */
@Getter
@Setter
public class Lesson04DiscountResult {
    /**
     * Discount percentage selected by the winning rule.
     */
    private int discountPercent;

    /**
     * Explanation set by the winning rule.
     */
    private String reason;

    /**
     * Names of rules that changed this result, in firing order.
     */
    private final List<String> firedRules = new ArrayList<>();

    /**
     * Called from DRL to record rule execution.
     *
     * @param ruleName human-readable rule name
     */
    public void addFiredRule(String ruleName) {
        firedRules.add(ruleName);
    }
}
