package io.github.fengyanglin09.rulesenginelab.lessons.lesson09_phase_execution.model;

/**
 * Execution phase for Lesson 09 rules.
 *
 * <p>The DRL checks this enum so validation rules and discount rules do not run
 * during the same {@code instance.fire()} call.</p>
 */
public enum Lesson09RulePhase {
    /**
     * Run rules that decide whether the request is valid.
     */
    VALIDATION,

    /**
     * Run rules that calculate the discount after validation succeeds.
     */
    DISCOUNT
}
