package io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Output fact for Lesson 11.
 *
 * <p>This result intentionally contains candidate discounts instead of one
 * final selected discount. That is the main teaching point of the lesson.</p>
 */
@Getter
@Setter
public class Lesson11DiscountResult {
    private boolean valid = true;
    private String reason;
    private final List<String> validationErrors = new ArrayList<>();
    private List<String> matchedRules = new ArrayList<>();
    private List<String> firedRules = new ArrayList<>();
    private final List<Lesson11DiscountCandidate> candidates = new ArrayList<>();

    /**
     * Called from DRL when a validation rule finds a problem.
     *
     * @param message validation error message
     */
    public void addValidationError(String message) {
        validationErrors.add(message);
    }
}
