package io.github.fengyanglin09.rulesenginelab.lessons.lesson08_rule_tracing.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Output fact for Lesson 08.
 *
 * <p>The rule trace lists are filled by an {@code AgendaEventListener} in the
 * service. The DRL rules do not manually call {@code addFiredRule(...)} in this
 * lesson.</p>
 */
@Getter
@Setter
public class Lesson08DiscountResult {
    private boolean valid = true;
    private int discountPercent;
    private String reason;
    private final List<String> validationErrors = new ArrayList<>();
    private final List<String> matchedRules = new ArrayList<>();
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
     * Called by the listener when Drools creates a rule activation.
     *
     * @param ruleName human-readable rule name
     */
    public void addMatchedRule(String ruleName) {
        matchedRules.add(ruleName);
    }

    /**
     * Called by the listener after Drools actually fires a rule.
     *
     * @param ruleName human-readable rule name
     */
    public void addFiredRule(String ruleName) {
        firedRules.add(ruleName);
    }
}
