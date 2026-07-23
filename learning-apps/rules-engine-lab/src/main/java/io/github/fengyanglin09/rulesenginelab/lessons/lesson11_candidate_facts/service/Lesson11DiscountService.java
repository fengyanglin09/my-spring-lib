package io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.service;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.model.Lesson11DiscountCandidate;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.model.Lesson11DiscountConfig;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.model.Lesson11DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.model.Lesson11DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.model.Lesson11RuleExecutionContext;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.model.Lesson11RulePhase;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.rules.Lesson11DiscountRuleUnit;
import org.drools.ruleunits.api.DataHandle;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.api.conf.RuleConfig;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Orchestrates Lesson 11 rule execution and candidate collection.
 */
@Service
public class Lesson11DiscountService {

    private final Lesson11DiscountConfig discountConfig;
    private final Lesson11RuleTraceFactory ruleTraceFactory;

    /**
     * Creates the Spring service with default config and tracing support.
     */
    public Lesson11DiscountService() {
        this(Lesson11DiscountConfig.defaults(), new Lesson11RuleTraceFactory());
    }

    /**
     * Creates the service with explicit dependencies, which is useful for tests.
     *
     * @param discountConfig config fact inserted into the Rule Unit
     * @param ruleTraceFactory factory for Drools listener config
     */
    public Lesson11DiscountService(
            Lesson11DiscountConfig discountConfig,
            Lesson11RuleTraceFactory ruleTraceFactory
    ) {
        this.discountConfig = discountConfig;
        this.ruleTraceFactory = ruleTraceFactory;
    }

    /**
     * Runs validation and candidate-generation phases, then queries the
     * candidate facts.
     *
     * <p>Unlike earlier lessons, discount rules do not set a final discount on
     * the result. They add {@link Lesson11DiscountCandidate} facts. The service
     * then queries those facts and copies them into the API result.</p>
     *
     * <p>Rule of thumb for facts: the service adds facts Java already knows
     * before rules run, such as request, result, config, and context. Rules add
     * facts that are discovered because rule conditions matched, such as
     * discount candidates.</p>
     *
     * @param request input fact for Drools to validate and inspect
     * @return output fact containing validation, trace, and candidate data
     */
    public Lesson11DiscountResult calculate(Lesson11DiscountRequest request) {
        Lesson11DiscountRuleUnit ruleUnit = new Lesson11DiscountRuleUnit();
        Lesson11DiscountResult result = new Lesson11DiscountResult();
        Lesson11RuleExecutionContext context =
                new Lesson11RuleExecutionContext(Lesson11RulePhase.VALIDATION);

        /*
         * These are service-added facts.
         *
         * Java already knows these objects before Drools starts:
         *   request        -> the user input
         *   result         -> the object rules can update
         *   discountConfig -> business thresholds and percentages
         *   context        -> the current execution phase
         *
         * The service does not add candidate facts here because candidates are
         * not known yet. Candidate facts are created later by DRL rules when
         * their conditions match.
         */
        ruleUnit.getRequests().add(request);
        ruleUnit.getResults().add(result);
        ruleUnit.getConfigs().add(discountConfig);
        DataHandle contextHandle = ruleUnit.getContexts().add(context);

        RuleConfig ruleConfig = ruleTraceFactory.create(context);

        try (RuleUnitInstance<Lesson11DiscountRuleUnit> instance =
                     RuleUnitProvider.get().createRuleUnitInstance(ruleUnit, ruleConfig)) {
            instance.fire();

            if (result.isValid()) {
                context.setPhase(Lesson11RulePhase.DISCOUNT);
                ruleUnit.getContexts().update(contextHandle, context);
                instance.fire();
            }

            /*
             * Now the rules have had a chance to create candidate facts.
             *
             * Query those rule-created facts and copy them into the API result
             * so the caller can see every possible discount.
             */
            QueryResults queryResults = instance.executeQuery("Find all discount candidates");
            for (QueryResultsRow row : queryResults) {
                Lesson11DiscountCandidate candidate =
                        (Lesson11DiscountCandidate) row.get("$candidate");
                result.getCandidates().add(candidate);
            }
        }

        result.setMatchedRules(new ArrayList<>(context.getMatchedRules()));
        result.setFiredRules(new ArrayList<>(context.getFiredRules()));
        return result;
    }
}
