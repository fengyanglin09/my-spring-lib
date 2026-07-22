package io.github.fengyanglin09.rulesenginelab.examples.advanced_discount_rules.service;

import io.github.fengyanglin09.rulesenginelab.common.config.component.DroolsRuleConfigFactory;
import io.github.fengyanglin09.rulesenginelab.examples.advanced_discount_rules.rules.AdvancedDiscountRuleUnit;
import io.github.fengyanglin09.rulesenginelab.examples.advanced_discount_rules.model.*;
import lombok.RequiredArgsConstructor;
import org.drools.ruleunits.api.DataHandle;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.api.conf.RuleConfig;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdvancedDiscountService {

    private final DroolsRuleConfigFactory ruleConfigFactory;

    private final DiscountConfig discountConfig;

    public DiscountResult calculate(DiscountRequest request) {
        AdvancedDiscountRuleUnit ruleUnit = new AdvancedDiscountRuleUnit();
        DiscountResult result = new DiscountResult();

        ruleUnit.getRequests().add(request);
        ruleUnit.getResults().add(result);
        ruleUnit.getConfigs().add(discountConfig);
        RuleExecutionContext context = new RuleExecutionContext(RulePhase.VALIDATION);
        DataHandle contextHandle = ruleUnit.getContexts().add(context);

        RuleConfig ruleConfig = this.ruleConfigFactory.create(context);

        try (RuleUnitInstance<AdvancedDiscountRuleUnit> instance =
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
}
