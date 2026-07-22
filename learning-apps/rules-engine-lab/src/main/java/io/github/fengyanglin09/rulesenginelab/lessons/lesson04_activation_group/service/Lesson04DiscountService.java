package io.github.fengyanglin09.rulesenginelab.lessons.lesson04_activation_group.service;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson04_activation_group.model.Lesson04DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson04_activation_group.model.Lesson04DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson04_activation_group.rules.Lesson04DiscountRuleUnit;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.springframework.stereotype.Service;

/**
 * Orchestrates the Lesson 04 rule execution.
 */
@Service
public class Lesson04DiscountService {

    /**
     * Fires the activation-group demo rules.
     *
     * <p>In Lesson 03, multiple satisfied rules could all run in salience order.
     * In this lesson, rules in the same activation group behave like a
     * one-winner set: when one rule in the group fires, Drools cancels the other
     * pending activations in that same group.</p>
     *
     * @param request input fact for Drools to inspect
     * @return output fact after the winning rule fires
     */
    public Lesson04DiscountResult calculate(Lesson04DiscountRequest request) {
        Lesson04DiscountRuleUnit ruleUnit = new Lesson04DiscountRuleUnit();
        Lesson04DiscountResult result = new Lesson04DiscountResult();

        ruleUnit.getRequests().add(request);
        ruleUnit.getResults().add(result);

        try (RuleUnitInstance<Lesson04DiscountRuleUnit> instance =
                     RuleUnitProvider.get().createRuleUnitInstance(ruleUnit)) {
            instance.fire();
        }

        return result;
    }
}
