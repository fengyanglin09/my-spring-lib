package io.github.fengyanglin09.rulesenginelab.lessons.lesson10_queries.model;

/**
 * Execution phase for Lesson 10 rules.
 */
public enum Lesson10RulePhase {
    /**
     * Run validation rules.
     */
    VALIDATION,

    /**
     * Run discount rules after validation succeeds.
     */
    DISCOUNT
}
