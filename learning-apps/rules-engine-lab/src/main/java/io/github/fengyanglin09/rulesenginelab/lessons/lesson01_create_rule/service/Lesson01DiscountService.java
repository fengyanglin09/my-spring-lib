package io.github.fengyanglin09.rulesenginelab.lessons.lesson01_create_rule.service;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson01_create_rule.model.Lesson01DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson01_create_rule.model.Lesson01DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson01_create_rule.rules.Lesson01DiscountRuleUnit;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.springframework.stereotype.Service;

/**
 * Orchestrates the Lesson 01 rule execution.
 *
 * <p>This service decides which Rule Unit to run by creating
 * {@link Lesson01DiscountRuleUnit}. Drools then uses that Rule Unit type to find
 * DRL files declaring {@code unit Lesson01DiscountRuleUnit;}.</p>
 */
@Service
public class Lesson01DiscountService {

    /**
     * Inserts one request and one result into the rule unit, fires matching
     * rules, and returns the modified result.
     *
     * @param request input fact for Drools to inspect
     * @return output fact after rule execution
     */
    public Lesson01DiscountResult calculate(Lesson01DiscountRequest request) {
        Lesson01DiscountRuleUnit ruleUnit = new Lesson01DiscountRuleUnit();
        Lesson01DiscountResult result = new Lesson01DiscountResult();

        /*
         * These names line up with the DRL paths:
         *   ruleUnit.getRequests() -> /requests
         *   ruleUnit.getResults()  -> /results
         */
        ruleUnit.getRequests().add(request);
        ruleUnit.getResults().add(result);

        try (RuleUnitInstance<Lesson01DiscountRuleUnit> instance =
                     RuleUnitProvider.get().createRuleUnitInstance(ruleUnit)) {
            instance.fire();
        }

        return result;
    }
}
