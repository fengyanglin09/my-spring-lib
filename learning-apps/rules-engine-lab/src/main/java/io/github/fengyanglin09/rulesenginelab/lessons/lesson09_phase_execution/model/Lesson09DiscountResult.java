package io.github.fengyanglin09.rulesenginelab.lessons.lesson09_phase_execution.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Output fact for Lesson 09.
 *
 * <p>The result receives the rule trace after both phase executions complete.
 * The trace itself is collected on {@link Lesson09RuleExecutionContext} while
 * rules are firing.</p>
 */
@Getter
@Setter
public class Lesson09DiscountResult {
    private boolean valid = true;
    private int discountPercent;
    private String reason;
    private final List<String> validationErrors = new ArrayList<>();
    private List<String> matchedRules = new ArrayList<>();
    private List<String> firedRules = new ArrayList<>();

    /**
     * Called from DRL when a validation rule finds a problem.
     *
     * @param message validation error message
     */
    public void addValidationError(String message) {
        validationErrors.add(message);
    }
}
