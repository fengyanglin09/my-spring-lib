package io.github.fengyanglin09.rulesenginelab.lessons.lesson10_queries.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Mutable context fact for Lesson 10.
 */
@Getter
@Setter
public class Lesson10RuleExecutionContext {
    private Lesson10RulePhase phase;
    private final List<String> matchedRules = new ArrayList<>();
    private final List<String> firedRules = new ArrayList<>();

    /**
     * Creates a context for the starting phase.
     *
     * @param phase current execution phase
     */
    public Lesson10RuleExecutionContext(Lesson10RulePhase phase) {
        this.phase = phase;
    }
}
