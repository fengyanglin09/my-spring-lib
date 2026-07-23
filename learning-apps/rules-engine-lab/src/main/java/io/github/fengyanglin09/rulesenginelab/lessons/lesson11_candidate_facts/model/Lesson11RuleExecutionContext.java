package io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Mutable context fact for Lesson 11.
 */
@Getter
@Setter
public class Lesson11RuleExecutionContext {
    private Lesson11RulePhase phase;
    private final List<String> matchedRules = new ArrayList<>();
    private final List<String> firedRules = new ArrayList<>();

    /**
     * Creates a context for the starting phase.
     *
     * @param phase current execution phase
     */
    public Lesson11RuleExecutionContext(Lesson11RulePhase phase) {
        this.phase = phase;
    }
}
