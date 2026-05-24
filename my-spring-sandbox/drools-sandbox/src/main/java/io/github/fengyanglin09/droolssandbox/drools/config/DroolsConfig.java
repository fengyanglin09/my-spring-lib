package io.github.fengyanglin09.droolssandbox.drools.config;

import lombok.extern.slf4j.Slf4j;
import org.drools.core.event.DefaultAgendaEventListener;
import org.drools.ruleunits.api.RuleUnitProvider;

import org.drools.ruleunits.api.conf.RuleConfig;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
@Slf4j
public class DroolsConfig {

    @Bean(name = "ruleConfigSupplier")
    public Supplier<RuleConfig> ruleConfigSupplier() {
        return () -> {
            RuleConfig ruleConfig = RuleUnitProvider.get().newRuleConfig();

            ruleConfig.getAgendaEventListeners().add(new DefaultAgendaEventListener() {
                @Override
                public void matchCreated(MatchCreatedEvent event) {
                    log.info("MATCHED: {}", event.getMatch().getRule().getName());
                }

                @Override
                public void afterMatchFired(AfterMatchFiredEvent event) {
                    log.info("FIRED: {}", event.getMatch().getRule().getName());
                }
            });

            return ruleConfig;
        };
    }

}
