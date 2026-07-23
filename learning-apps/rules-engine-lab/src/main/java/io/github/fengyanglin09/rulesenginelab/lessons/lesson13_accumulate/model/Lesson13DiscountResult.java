package io.github.fengyanglin09.rulesenginelab.lessons.lesson13_accumulate.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Output fact for Lesson 13.
 */
@Getter
@Setter
public class Lesson13DiscountResult {
    private boolean valid = true;
    private boolean totalCalculated;
    private int totalCents;
    private int discountPercent;
    private String reason;
    private final List<String> validationErrors = new ArrayList<>();
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
     * Called from DRL to make the rule order visible in the API result.
     *
     * @param ruleName human-readable rule name
     */
    public void addFiredRule(String ruleName) {
        firedRules.add(ruleName);
    }
}
