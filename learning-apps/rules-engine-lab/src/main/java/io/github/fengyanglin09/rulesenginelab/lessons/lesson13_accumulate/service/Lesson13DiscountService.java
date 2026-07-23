package io.github.fengyanglin09.rulesenginelab.lessons.lesson13_accumulate.service;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson13_accumulate.model.Lesson13CartItem;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson13_accumulate.model.Lesson13DiscountConfig;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson13_accumulate.model.Lesson13DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson13_accumulate.model.Lesson13DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson13_accumulate.rules.Lesson13DiscountRuleUnit;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.springframework.stereotype.Service;

/**
 * Orchestrates Lesson 13 rule execution.
 */
@Service
public class Lesson13DiscountService {

    private final Lesson13DiscountConfig discountConfig;

    /**
     * Creates the Spring service with default config.
     */
    public Lesson13DiscountService() {
        this(Lesson13DiscountConfig.defaults());
    }

    /**
     * Creates the service with explicit config, which is useful for tests.
     *
     * @param discountConfig config fact inserted into the Rule Unit
     */
    public Lesson13DiscountService(Lesson13DiscountConfig discountConfig) {
        this.discountConfig = discountConfig;
    }

    /**
     * Inserts the request, result, config, and each cart item fact, then fires
     * the accumulate demo rules.
     *
     * <p>The important Lesson 13 detail is that the service does not calculate
     * the cart total. It inserts each item into {@code /items}; the DRL
     * {@code accumulate} expression calculates the total.</p>
     *
     * @param request input containing customer type and cart items
     * @return output fact containing total and discount result
     */
    public Lesson13DiscountResult calculate(Lesson13DiscountRequest request) {
        Lesson13DiscountRuleUnit ruleUnit = new Lesson13DiscountRuleUnit();
        Lesson13DiscountResult result = new Lesson13DiscountResult();

        ruleUnit.getRequests().add(request);
        ruleUnit.getResults().add(result);
        ruleUnit.getConfigs().add(discountConfig);

        if (request.getItems() != null) {
            for (Lesson13CartItem item : request.getItems()) {
                ruleUnit.getItems().add(item);
            }
        }

        try (RuleUnitInstance<Lesson13DiscountRuleUnit> instance =
                     RuleUnitProvider.get().createRuleUnitInstance(ruleUnit)) {
            instance.fire();
        }

        return result;
    }
}
