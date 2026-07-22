package io.github.fengyanglin09.rulesenginelab.lessons.lesson08_rule_tracing.service;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson08_rule_tracing.model.Lesson08DiscountConfig;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson08_rule_tracing.model.Lesson08DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson08_rule_tracing.model.Lesson08DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson08_rule_tracing.rules.Lesson08DiscountRuleUnit;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.api.conf.RuleConfig;
import org.springframework.stereotype.Service;

/**
 * Orchestrates Lesson 08 rule execution with listener-based tracing.
 */
@Service
public class Lesson08DiscountService {

    private final Lesson08DiscountConfig discountConfig;
    private final Lesson08RuleTraceFactory ruleTraceFactory;

    /**
     * Creates the Spring service with default config and tracing support.
     */
    public Lesson08DiscountService() {
        this(Lesson08DiscountConfig.defaults(), new Lesson08RuleTraceFactory());
    }

    /**
     * Creates the service with explicit dependencies, which is useful for tests.
     *
     * @param discountConfig config fact inserted into the Rule Unit
     * @param ruleTraceFactory factory for Drools listener config
     */
    public Lesson08DiscountService(
            Lesson08DiscountConfig discountConfig,
            Lesson08RuleTraceFactory ruleTraceFactory
    ) {
        this.discountConfig = discountConfig;
        this.ruleTraceFactory = ruleTraceFactory;
    }

    /**
     * Fires rules and records matched/fired rule names through an agenda
     * listener.
     *
     * <p>The important difference from previous lessons is that this service
     * creates a {@link RuleConfig} and passes it to
     * {@code createRuleUnitInstance(ruleUnit, ruleConfig)}. That is how the
     * listener is attached to this rule execution.</p>
     *
     * @param request input fact for Drools to validate and inspect
     * @return output fact containing validation, discount, and trace results
     */
    public Lesson08DiscountResult calculate(Lesson08DiscountRequest request) {
        Lesson08DiscountRuleUnit ruleUnit = new Lesson08DiscountRuleUnit();
        Lesson08DiscountResult result = new Lesson08DiscountResult();

        ruleUnit.getRequests().add(request);
        ruleUnit.getResults().add(result);
        ruleUnit.getConfigs().add(discountConfig);

        RuleConfig ruleConfig = ruleTraceFactory.create(result);

        try (RuleUnitInstance<Lesson08DiscountRuleUnit> instance =
                     RuleUnitProvider.get().createRuleUnitInstance(ruleUnit, ruleConfig)) {
            instance.fire();
        }

        return result;
    }
}
