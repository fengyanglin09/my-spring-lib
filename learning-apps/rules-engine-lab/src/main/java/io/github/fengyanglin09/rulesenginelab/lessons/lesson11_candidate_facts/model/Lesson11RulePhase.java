package io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.model;

/**
 * Execution phase for Lesson 11 rules.
 */
public enum Lesson11RulePhase {
    /**
     * Run validation rules.
     */
    VALIDATION,

    /**
     * Run rules that create discount candidates.
     */
    DISCOUNT
}
