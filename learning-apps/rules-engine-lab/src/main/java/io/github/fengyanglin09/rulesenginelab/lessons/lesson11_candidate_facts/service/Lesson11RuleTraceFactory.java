package io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.service;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.model.Lesson11RuleExecutionContext;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.api.conf.RuleConfig;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.MatchCreatedEvent;

/**
 * Creates Drools tracing configuration for Lesson 11.
 */
public class Lesson11RuleTraceFactory {

    /**
     * Creates a RuleConfig that records matched and fired rules on the context.
     *
     * @param context execution context that should receive trace events
     * @return Drools RuleConfig with an agenda event listener
     */
    public RuleConfig create(Lesson11RuleExecutionContext context) {
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
