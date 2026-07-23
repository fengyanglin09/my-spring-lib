package io.github.fengyanglin09.rulesenginelab.lessons.lesson14_decision_table.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Output fact for Lesson 14.
 *
 * <p>The result records both the final discount and which decision-table row
 * won. That makes the rule behavior easier to explain when several rows match
 * the same request.</p>
 */
@Getter
@Setter
public class Lesson14DiscountResult {
    private boolean valid = true;
    private int discountPercent;
    private String reason;
    private String matchedDecisionRow;
    private final List<String> firedRules = new ArrayList<>();

    /**
     * Called from DRL so tests and API responses can show which rules fired.
     *
     * @param ruleName human-readable rule name
     */
    public void addFiredRule(String ruleName) {
        firedRules.add(ruleName);
    }
}
