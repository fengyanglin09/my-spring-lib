package io.github.fengyanglin09.rulesenginelab.lessons.lesson09_phase_execution.service;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson09_phase_execution.model.Lesson09RuleExecutionContext;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.api.conf.RuleConfig;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.MatchCreatedEvent;

/**
 * Creates Drools tracing configuration for Lesson 09.
 */
public class Lesson09RuleTraceFactory {

    /**
     * Creates a RuleConfig that records matched and fired rules on the context.
     *
     * @param context execution context that should receive trace events
     * @return Drools RuleConfig with an agenda event listener
     */
    public RuleConfig create(Lesson09RuleExecutionContext context) {
        RuleConfig ruleConfig = RuleUnitProvider.get().newRuleConfig();

        ruleConfig.getAgendaEventListeners().add(new DefaultAgendaEventListener() {
            @Override
            public void matchCreated(MatchCreatedEvent event) {
                context.getMatchedRules().add(event.getMatch().getRule().getName());
            }

            @Override
            public void afterMatchFired(AfterMatchFiredEvent event) {
                context.getFiredRules().add(event.getMatch().getRule().getName());
            }
        });

        return ruleConfig;
    }
}
