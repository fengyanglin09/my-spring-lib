package io.github.fengyanglin09.rulesenginelab.lessons.lesson05_validation_rules.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Output fact for Lesson 05.
 *
 * <p>The result starts as valid. Validation rules can mark it invalid, add an
 * error message, and prevent discount rules from applying.</p>
 */
@Getter
@Setter
public class Lesson05DiscountResult {
    /**
     * Whether discount rules are allowed to run.
     */
    private boolean valid = true;

    /**
     * Discount percentage selected by a discount rule. It stays 0 for invalid
     * requests.
     */
    private int discountPercent;

    /**
     * Human-readable result message.
     */
    private String reason;

    /**
     * Validation messages added by validation rules.
     */
    private final List<String> validationErrors = new ArrayList<>();

    /**
     * Rule names that changed this result, in firing order.
     */
    private final List<String> firedRules = new ArrayList<>();

    /**
     * Called from DRL when a validation rule finds a problem.
     *
     * @param message validation error message
     */
    public void addValidationError(String message) {
        validationErrors.add(message);
    }

    /**
     * Called from DRL to record rule execution.
     *
     * @param ruleName human-readable rule name
     */
    public void addFiredRule(String ruleName) {
        firedRules.add(ruleName);
    }
}
