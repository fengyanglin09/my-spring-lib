package io.github.fengyanglin09.rulesenginelab.lessons.lesson12_candidate_selection.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Output fact for Lesson 12.
 *
 * <p>The result keeps all candidates for transparency and also exposes the
 * selected winning candidate as the final discount decision.</p>
 */
@Getter
@Setter
public class Lesson12DiscountResult {
    private boolean valid = true;
    private int discountPercent;
    private String reason;
    private Lesson12DiscountCandidate selectedCandidate;
    private final List<String> validationErrors = new ArrayList<>();
    private List<String> matchedRules = new ArrayList<>();
    private List<String> firedRules = new ArrayList<>();
    private final List<Lesson12DiscountCandidate> candidates = new ArrayList<>();

    /**
     * Called from DRL when a validation rule finds a problem.
     *
     * @param message validation error message
     */
    public void addValidationError(String message) {
        validationErrors.add(message);
    }
}
