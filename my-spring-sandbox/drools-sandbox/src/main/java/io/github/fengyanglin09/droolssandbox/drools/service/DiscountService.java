package io.github.fengyanglin09.droolssandbox.drools.service;

import io.github.fengyanglin09.droolssandbox.drools.droolsConfig.DiscountRuleUnit;
import io.github.fengyanglin09.droolssandbox.drools.models.DiscountRequest;
import io.github.fengyanglin09.droolssandbox.drools.models.DiscountResult;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.springframework.stereotype.Service;

@Service
public class DiscountService {
    public DiscountResult calculate(DiscountRequest request) {
        DiscountRuleUnit ruleUnit = new DiscountRuleUnit();
        DiscountResult result = new DiscountResult();

        ruleUnit.getRequests().add(request);
        ruleUnit.getResults().add(result);

        try (RuleUnitInstance<DiscountRuleUnit> instance =
                     RuleUnitProvider.get().createRuleUnitInstance(ruleUnit)) {
            instance.fire();
        }

        return result;
    }
}
