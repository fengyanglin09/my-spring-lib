package io.github.fengyanglin09.rulesenginelab.lessons.lesson10_queries.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Output fact for Lesson 10.
 */
@Getter
@Setter
public class Lesson10DiscountResult {
    private boolean valid = true;
    private int discountPercent;
    private String reason;
    private final List<String> validationErrors = new ArrayList<>();
    private List<String> matchedRules = new ArrayList<>();
    private List<String> firedRules = new ArrayList<>();
    private final List<String> auditMessages = new ArrayList<>();

    /**
     * Called from DRL when a validation rule finds a problem.
     *
     * @param message validation error message
     */
    public void addValidationError(String message) {
        validationErrors.add(message);
    }
}
