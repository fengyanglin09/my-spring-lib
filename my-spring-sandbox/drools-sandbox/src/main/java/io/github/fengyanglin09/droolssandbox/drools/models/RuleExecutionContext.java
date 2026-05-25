package io.github.fengyanglin09.droolssandbox.drools.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RuleExecutionContext {
    private RulePhase phase;
    private List<String> matchedRules;
    private List<String> firedRules;

    public RuleExecutionContext(RulePhase phase) {
        this.phase = phase;
        this.matchedRules = new ArrayList<>();
        this.firedRules = new ArrayList<>();
    }
}
