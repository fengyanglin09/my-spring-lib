package io.github.fengyanglin09.rulesenginelab.lessons.lesson09_phase_execution.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Mutable context fact for Lesson 09.
 *
 * <p>This object tells DRL which phase is currently running. The service starts
 * it in {@link Lesson09RulePhase#VALIDATION}, fires rules, then changes it to
 * {@link Lesson09RulePhase#DISCOUNT} if validation succeeds.</p>
 */
@Getter
@Setter
public class Lesson09RuleExecutionContext {
    /**
     * Current execution phase checked by DRL.
     */
    private Lesson09RulePhase phase;

    /**
     * Rule names recorded by the agenda listener when matches are created.
     */
    private final List<String> matchedRules = new ArrayList<>();

    /**
     * Rule names recorded by the agenda listener after rules fire.
     */
    private final List<String> firedRules = new ArrayList<>();

    /**
     * Creates a context for the starting phase.
     *
     * @param phase current rule execution phase
     */
    public Lesson09RuleExecutionContext(Lesson09RulePhase phase) {
        this.phase = phase;
    }
}
