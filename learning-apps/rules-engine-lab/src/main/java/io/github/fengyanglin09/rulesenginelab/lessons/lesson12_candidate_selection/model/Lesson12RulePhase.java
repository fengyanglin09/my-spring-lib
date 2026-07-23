package io.github.fengyanglin09.rulesenginelab.lessons.lesson12_candidate_selection.model;

/**
 * Execution phase for Lesson 12 rules.
 */
public enum Lesson12RulePhase {
    /**
     * Run validation rules.
     */
    VALIDATION,

    /**
     * Run rules that create discount candidates.
     */
    DISCOUNT
}
