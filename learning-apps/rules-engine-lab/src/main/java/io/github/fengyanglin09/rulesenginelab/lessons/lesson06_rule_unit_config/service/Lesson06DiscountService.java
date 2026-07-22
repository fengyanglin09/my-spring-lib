package io.github.fengyanglin09.rulesenginelab.lessons.lesson06_rule_unit_config.service;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson06_rule_unit_config.model.Lesson06DiscountConfig;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson06_rule_unit_config.model.Lesson06DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson06_rule_unit_config.model.Lesson06DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson06_rule_unit_config.rules.Lesson06DiscountRuleUnit;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.springframework.stereotype.Service;

/**
 * Orchestrates the Lesson 06 rule execution.
 */
@Service
public class Lesson06DiscountService {

    private final Lesson06DiscountConfig discountConfig;

    /**
     * Creates the Spring service with the lesson default configuration.
     */
    public Lesson06DiscountService() {
        this(Lesson06DiscountConfig.defaults());
    }

    /**
     * Creates the service with an explicit config, which is useful for tests.
     *
     * @param discountConfig configuration fact inserted into the Rule Unit
     */
    public Lesson06DiscountService(Lesson06DiscountConfig discountConfig) {
        this.discountConfig = discountConfig;
    }

    /**
     * Fires validation and discount rules with a config fact.
     *
     * <p>The important new line is {@code ruleUnit.getConfigs().add(...)}. That
     * makes the config available to DRL as {@code /configs}. A rule can bind it
     * with {@code $config : /configs} and then call getters such as
     * {@code $config.getLargeOrderPercent()}.</p>
     *
     * @param request input fact for Drools to validate and inspect
     * @return output fact containing validation and discount results
     */
    public Lesson06DiscountResult calculate(Lesson06DiscountRequest request) {
        Lesson06DiscountRuleUnit ruleUnit = new Lesson06DiscountRuleUnit();
        Lesson06DiscountResult result = new Lesson06DiscountResult();

        ruleUnit.getRequests().add(request);
        ruleUnit.getResults().add(result);
        ruleUnit.getConfigs().add(discountConfig);

        try (RuleUnitInstance<Lesson06DiscountRuleUnit> instance =
                     RuleUnitProvider.get().createRuleUnitInstance(ruleUnit)) {
            instance.fire();
        }

        return result;
    }
}
