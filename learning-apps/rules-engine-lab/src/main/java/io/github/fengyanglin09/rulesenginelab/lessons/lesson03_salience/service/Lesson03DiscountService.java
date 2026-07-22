package io.github.fengyanglin09.rulesenginelab.lessons.lesson03_salience.service;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson03_salience.model.Lesson03DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson03_salience.model.Lesson03DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson03_salience.rules.Lesson03DiscountRuleUnit;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.springframework.stereotype.Service;

/**
 * Orchestrates the Lesson 03 rule execution.
 */
@Service
public class Lesson03DiscountService {

    /**
     * Fires the salience demo rules.
     *
     * <p>If more than one rule matches, all satisfied rules can run. Drools
     * uses salience to decide which activation fires first, but salience does
     * not stop lower-salience rules from firing later.</p>
     *
     * <p>Example: a VIP request with an order amount of 600 matches both the
     * VIP rule and the large-order rule. The VIP rule fires first because it has
     * higher salience, then the large-order rule fires afterward and overwrites
     * the final discount result.</p>
     *
     * @param request input fact for Drools to inspect
     * @return output fact after all matching rules fire
     */
    public Lesson03DiscountResult calculate(Lesson03DiscountRequest request) {
        Lesson03DiscountRuleUnit ruleUnit = new Lesson03DiscountRuleUnit();
        Lesson03DiscountResult result = new Lesson03DiscountResult();

        ruleUnit.getRequests().add(request);
        ruleUnit.getResults().add(result);

        try (RuleUnitInstance<Lesson03DiscountRuleUnit> instance =
                     RuleUnitProvider.get().createRuleUnitInstance(ruleUnit)) {
            instance.fire();
        }

        return result;
    }
}
