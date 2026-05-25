package io.github.fengyanglin09.droolssandbox.drools.config.component;


import io.github.fengyanglin09.droolssandbox.drools.config.DroolsDebugProperties;
import io.github.fengyanglin09.droolssandbox.drools.models.RuleExecutionContext;
import lombok.RequiredArgsConstructor;
import org.drools.core.event.DefaultAgendaEventListener;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.api.conf.RuleConfig;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DroolsRuleConfigFactory {

    private final DroolsDebugProperties debugProperties;

    public RuleConfig create(RuleExecutionContext context) {
        RuleConfig ruleConfig = RuleUnitProvider.get().newRuleConfig();

        if (!debugProperties.isTraceEnabled()) {
            return ruleConfig;
        }

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
