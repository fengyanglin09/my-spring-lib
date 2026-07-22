package io.github.fengyanglin09.rulesenginelab.lessons.lesson07_spring_properties.service;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson07_spring_properties.model.Lesson07DiscountConfig;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson07_spring_properties.model.Lesson07DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson07_spring_properties.model.Lesson07DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson07_spring_properties.rules.Lesson07DiscountRuleUnit;
import lombok.RequiredArgsConstructor;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.springframework.stereotype.Service;

/**
 * Orchestrates the Lesson 07 rule execution.
 */
@Service
@RequiredArgsConstructor
public class Lesson07DiscountService {

    /**
     * Spring-created config fact loaded from {@code application.yml}.
     */
    private final Lesson07DiscountConfig discountConfig;

    /**
     * Fires validation and discount rules with a Spring-loaded config fact.
     *
     * <p>The service does not read properties directly. It only receives a
     * {@link Lesson07DiscountConfig} bean. That keeps Spring configuration code
     * outside the Drools execution code.</p>
     *
     * @param request input fact for Drools to validate and inspect
     * @return output fact containing validation and discount results
     */
    public Lesson07DiscountResult calculate(Lesson07DiscountRequest request) {
        Lesson07DiscountRuleUnit ruleUnit = new Lesson07DiscountRuleUnit();
        Lesson07DiscountResult result = new Lesson07DiscountResult();

        ruleUnit.getRequests().add(request);
        ruleUnit.getResults().add(result);
        ruleUnit.getConfigs().add(discountConfig);

        try (RuleUnitInstance<Lesson07DiscountRuleUnit> instance =
                     RuleUnitProvider.get().createRuleUnitInstance(ruleUnit)) {
            instance.fire();
        }

        return result;
    }
}
