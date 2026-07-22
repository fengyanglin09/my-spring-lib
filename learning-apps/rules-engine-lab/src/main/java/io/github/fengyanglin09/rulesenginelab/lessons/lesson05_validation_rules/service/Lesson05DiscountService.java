package io.github.fengyanglin09.rulesenginelab.lessons.lesson05_validation_rules.service;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson05_validation_rules.model.Lesson05DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson05_validation_rules.model.Lesson05DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson05_validation_rules.rules.Lesson05DiscountRuleUnit;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.springframework.stereotype.Service;

/**
 * Orchestrates the Lesson 05 rule execution.
 */
@Service
public class Lesson05DiscountService {

    /**
     * Fires validation rules and then any eligible discount rule.
     *
     * <p>The DRL gives validation rules higher salience, so they run before
     * discount rules. Discount rules require {@code /results[ valid == true ]};
     * when validation changes {@code valid} to {@code false}, those pending
     * discount matches are no longer eligible.</p>
     *
     * @param request input fact for Drools to validate and inspect
     * @return output fact containing validation and discount results
     */
    public Lesson05DiscountResult calculate(Lesson05DiscountRequest request) {
        Lesson05DiscountRuleUnit ruleUnit = new Lesson05DiscountRuleUnit();
        Lesson05DiscountResult result = new Lesson05DiscountResult();

        ruleUnit.getRequests().add(request);
        ruleUnit.getResults().add(result);

        try (RuleUnitInstance<Lesson05DiscountRuleUnit> instance =
                     RuleUnitProvider.get().createRuleUnitInstance(ruleUnit)) {
            instance.fire();
        }

        return result;
    }
}
