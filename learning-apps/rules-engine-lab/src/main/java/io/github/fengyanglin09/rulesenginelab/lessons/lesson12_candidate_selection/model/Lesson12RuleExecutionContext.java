package io.github.fengyanglin09.rulesenginelab.lessons.lesson12_candidate_selection.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Mutable context fact for Lesson 12.
 */
@Getter
@Setter
public class Lesson12RuleExecutionContext {
    private Lesson12RulePhase phase;
    private final List<String> matchedRules = new ArrayList<>();
    private final List<String> firedRules = new ArrayList<>();

    /**
     * Creates a context for the starting phase.
     *
     * @param phase current execution phase
     */
    public Lesson12RuleExecutionContext(Lesson12RulePhase phase) {
        this.phase = phase;
    }
}
