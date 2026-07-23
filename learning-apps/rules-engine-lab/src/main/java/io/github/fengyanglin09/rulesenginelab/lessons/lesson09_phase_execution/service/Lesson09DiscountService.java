package io.github.fengyanglin09.rulesenginelab.lessons.lesson09_phase_execution.service;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson09_phase_execution.model.Lesson09DiscountConfig;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson09_phase_execution.model.Lesson09DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson09_phase_execution.model.Lesson09DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson09_phase_execution.model.Lesson09RuleExecutionContext;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson09_phase_execution.model.Lesson09RulePhase;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson09_phase_execution.rules.Lesson09DiscountRuleUnit;
import org.drools.ruleunits.api.DataHandle;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.api.conf.RuleConfig;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Orchestrates Lesson 09 rule execution in two phases.
 */
@Service
public class Lesson09DiscountService {

    private final Lesson09DiscountConfig discountConfig;
    private final Lesson09RuleTraceFactory ruleTraceFactory;

    /**
     * Creates the Spring service with default config and tracing support.
     */
    public Lesson09DiscountService() {
        this(Lesson09DiscountConfig.defaults(), new Lesson09RuleTraceFactory());
    }

    /**
     * Creates the service with explicit dependencies, which is useful for tests.
     *
     * @param discountConfig config fact inserted into the Rule Unit
     * @param ruleTraceFactory factory for Drools listener config
     */
    public Lesson09DiscountService(
            Lesson09DiscountConfig discountConfig,
            Lesson09RuleTraceFactory ruleTraceFactory
    ) {
        this.discountConfig = discountConfig;
        this.ruleTraceFactory = ruleTraceFactory;
    }

    /**
     * Runs validation rules first, then discount rules only if validation
     * succeeds.
     *
     * <p>The context is inserted as a fact into {@code /contexts}. After the
     * first {@code instance.fire()}, the service changes the context phase from
     * {@code VALIDATION} to {@code DISCOUNT}.</p>
     *
     * <p>The important Drools detail is the {@link DataHandle}. The handle is
     * returned when the context fact is inserted. Later, the service must call
     * {@code ruleUnit.getContexts().update(contextHandle, context)} so Drools
     * knows the existing fact changed phase.</p>
     *
     * @param request input fact for Drools to validate and inspect
     * @return output fact containing validation, discount, and trace results
     */
    public Lesson09DiscountResult calculate(Lesson09DiscountRequest request) {
        Lesson09DiscountRuleUnit ruleUnit = new Lesson09DiscountRuleUnit();
        Lesson09DiscountResult result = new Lesson09DiscountResult();
        Lesson09RuleExecutionContext context =
                new Lesson09RuleExecutionContext(Lesson09RulePhase.VALIDATION);

        ruleUnit.getRequests().add(request);
        ruleUnit.getResults().add(result);
        ruleUnit.getConfigs().add(discountConfig);
        DataHandle contextHandle = ruleUnit.getContexts().add(context);

        RuleConfig ruleConfig = ruleTraceFactory.create(context);

        try (RuleUnitInstance<Lesson09DiscountRuleUnit> instance =
                     RuleUnitProvider.get().createRuleUnitInstance(ruleUnit, ruleConfig)) {
            instance.fire();

            if (result.isValid()) {
                context.setPhase(Lesson09RulePhase.DISCOUNT);
                ruleUnit.getContexts().update(contextHandle, context);
                instance.fire();
            }
        }

        result.setMatchedRules(new ArrayList<>(context.getMatchedRules()));
        result.setFiredRules(new ArrayList<>(context.getFiredRules()));
        return result;
    }
}
