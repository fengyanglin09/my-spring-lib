package io.github.fengyanglin09.rulesenginelab.lessons.lesson03_salience.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Output fact for Lesson 03.
 *
 * <p>The {@code firedRules} list makes salience visible. A final discount value
 * only tells us where rule execution ended; the list tells us the order Drools
 * used to get there.</p>
 */
@Getter
@Setter
public class Lesson03DiscountResult {
    /**
     * Discount percentage set by the most recent rule that modified the result.
     */
    private int discountPercent;

    /**
     * Explanation set by the most recent rule that modified the result.
     */
    private String reason;

    /**
     * Names of rules that changed this result, in firing order.
     */
    private final List<String> firedRules = new ArrayList<>();

    /**
     * Called from DRL to record rule execution order.
     *
     * @param ruleName human-readable rule name
     */
    public void addFiredRule(String ruleName) {
        firedRules.add(ruleName);
    }
}
