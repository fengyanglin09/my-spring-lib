package io.github.fengyanglin09.rulesenginelab.lessons.lesson07_spring_properties.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Output fact for Lesson 07.
 */
@Getter
@Setter
public class Lesson07DiscountResult {
    private boolean valid = true;
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
     * Called from DRL to record rule execution.
     *
     * @param ruleName human-readable rule name
     */
    public void addFiredRule(String ruleName) {
        firedRules.add(ruleName);
    }
}
