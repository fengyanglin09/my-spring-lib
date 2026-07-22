package io.github.fengyanglin09.rulesenginelab.lessons.lesson02_multiple_rules.service;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson02_multiple_rules.model.Lesson02DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson02_multiple_rules.model.Lesson02DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson02_multiple_rules.rules.Lesson02DiscountRuleUnit;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.springframework.stereotype.Service;

/**
 * Orchestrates the Lesson 02 rule execution.
 *
 * <p>The service is almost identical to Lesson 01. The difference is the DRL
 * connected to {@link Lesson02DiscountRuleUnit}: it contains three rules
 * instead of one.</p>
 */
@Service
public class Lesson02DiscountService {

    /**
     * Inserts one request and one result, fires all matching Lesson 02 rules,
     * and returns the modified result.
     *
     * @param request input fact for Drools to inspect
     * @return output fact after rule execution
     */
    public Lesson02DiscountResult calculate(Lesson02DiscountRequest request) {
        Lesson02DiscountRuleUnit ruleUnit = new Lesson02DiscountRuleUnit();
        Lesson02DiscountResult result = new Lesson02DiscountResult();

        ruleUnit.getRequests().add(request);
        ruleUnit.getResults().add(result);

        try (RuleUnitInstance<Lesson02DiscountRuleUnit> instance =
                     RuleUnitProvider.get().createRuleUnitInstance(ruleUnit)) {
            instance.fire();
        }

        return result;
    }
}
