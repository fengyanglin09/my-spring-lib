package io.github.fengyanglin09.droolssandbox.drools.service;

import io.github.fengyanglin09.droolssandbox.drools.config.component.DroolsRuleConfigFactory;
import io.github.fengyanglin09.droolssandbox.drools.droolsConfig.DiscountRuleUnit;
import io.github.fengyanglin09.droolssandbox.drools.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.drools.ruleunits.api.DataHandle;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.api.conf.RuleConfig;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscountService {


    private static final AgendaFilter VALIDATION_RULES =
            match -> match.getRule().getName().startsWith("Missing ");

    private static final AgendaFilter NON_VALIDATION_RULES =
            match -> !match.getRule().getName().startsWith("Missing ");



//    private final Supplier<RuleConfig> ruleConfigSupplier;

    private final DroolsRuleConfigFactory  ruleConfigFactory;

    private final DiscountConfig discountConfig;

    public DiscountResult calculate(DiscountRequest request) {
        DiscountRuleUnit ruleUnit = new DiscountRuleUnit();
        DiscountResult result = new DiscountResult();

//        RuleConfig ruleConfig = ruleConfigSupplier.get();



        ruleUnit.getRequests().add(request);
        ruleUnit.getResults().add(result);
        ruleUnit.getConfigs().add(discountConfig);
        RuleExecutionContext context = new RuleExecutionContext(RulePhase.VALIDATION);
        DataHandle contextHandle = ruleUnit.getContexts().add(context);

        RuleConfig ruleConfig = this.ruleConfigFactory.create(context);

        try (RuleUnitInstance<DiscountRuleUnit> instance =
                     RuleUnitProvider.get().createRuleUnitInstance(ruleUnit, ruleConfig)) {

            instance.fire();
            if (result.isValid()) {
                context.setPhase(RulePhase.DISCOUNT);
                ruleUnit.getContexts().update(contextHandle, context);
                instance.fire();
            }

            QueryResults queryResults = instance.executeQuery("Find all discount audits");

            for (QueryResultsRow row : queryResults) {
                DiscountAudit audit = (DiscountAudit) row.get("$audit");
                result.getAuditMessages().add(audit.getMessage());
            }

        }

        result.setMatchedRules(context.getMatchedRules());
        result.setFiredRules(context.getFiredRules());



        return result;
    }


    private static AgendaFilter categoryFilter(String category) {

        return match -> {
            String ruleName = match.getRule().getName();
            Map<String, Object> metaData = match.getRule().getMetaData();

            log.info("FILTER CHECK rule= {} metadata= {}", ruleName, metaData);

            return category.equals(metaData.get("category"));

        };
    }

}
