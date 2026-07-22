package io.github.fengyanglin09.rulesenginelab.lessons.lesson08_rule_tracing.service;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson08_rule_tracing.model.Lesson08DiscountResult;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.api.conf.RuleConfig;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.MatchCreatedEvent;

/**
 * Creates Drools tracing configuration for Lesson 08.
 *
 * <p>{@link RuleConfig} is passed to
 * {@code RuleUnitProvider.get().createRuleUnitInstance(ruleUnit, ruleConfig)}.
 * Its agenda listener receives callbacks when rules match and fire.</p>
 */
public class Lesson08RuleTraceFactory {

    /**
     * Creates a RuleConfig that records matched and fired rules on the result.
     *
     * @param result output object that should receive trace events
     * @return Drools RuleConfig with an agenda event listener
     */
    public RuleConfig create(Lesson08DiscountResult result) {
        RuleConfig ruleConfig = RuleUnitProvider.get().newRuleConfig();

        ruleConfig.getAgendaEventListeners().add(new DefaultAgendaEventListener() {
            /**
             * Called when a rule activation is created.
             *
             * <p>"Matched" means Drools found facts that satisfy the rule's
             * conditions. A matched rule may still be canceled later, for
             * example by an activation group.</p>
             */
            @Override
            public void matchCreated(MatchCreatedEvent event) {
                result.addMatchedRule(event.getMatch().getRule().getName());
            }

            /**
             * Called after a rule actually runs its {@code then} block.
             */
            @Override
            public void afterMatchFired(AfterMatchFiredEvent event) {
                result.addFiredRule(event.getMatch().getRule().getName());
            }
        });

        return ruleConfig;
    }
}
