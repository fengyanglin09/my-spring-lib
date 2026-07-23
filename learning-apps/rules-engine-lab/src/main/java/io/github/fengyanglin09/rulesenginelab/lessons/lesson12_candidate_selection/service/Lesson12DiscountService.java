package io.github.fengyanglin09.rulesenginelab.lessons.lesson12_candidate_selection.service;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson12_candidate_selection.model.Lesson12DiscountCandidate;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson12_candidate_selection.model.Lesson12DiscountConfig;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson12_candidate_selection.model.Lesson12DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson12_candidate_selection.model.Lesson12DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson12_candidate_selection.model.Lesson12RuleExecutionContext;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson12_candidate_selection.model.Lesson12RulePhase;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson12_candidate_selection.rules.Lesson12DiscountRuleUnit;
import org.drools.ruleunits.api.DataHandle;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.api.conf.RuleConfig;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

/**
 * Orchestrates Lesson 12 rule execution and candidate selection.
 */
@Service
public class Lesson12DiscountService {

    private final Lesson12DiscountConfig discountConfig;
    private final Lesson12RuleTraceFactory ruleTraceFactory;

    /**
     * Creates the Spring service with default config and tracing support.
     */
    public Lesson12DiscountService() {
        this(Lesson12DiscountConfig.defaults(), new Lesson12RuleTraceFactory());
    }

    /**
     * Creates the service with explicit dependencies, which is useful for tests.
     *
     * @param discountConfig config fact inserted into the Rule Unit
     * @param ruleTraceFactory factory for Drools listener config
     */
    public Lesson12DiscountService(
            Lesson12DiscountConfig discountConfig,
            Lesson12RuleTraceFactory ruleTraceFactory
    ) {
        this.discountConfig = discountConfig;
        this.ruleTraceFactory = ruleTraceFactory;
    }

    /**
     * Runs validation and candidate-generation phases, queries candidates, then
     * selects the best candidate.
     *
     * <p>This lesson keeps candidate generation in DRL and selection in Java.
     * That draws a clean boundary:</p>
     *
     * <p>Rules propose possible discounts.</p>
     *
     * <p>Java chooses the highest-percent candidate and writes the final result.</p>
     *
     * @param request input fact for Drools to validate and inspect
     * @return output fact containing validation, trace, candidates, and winner
     */
    public Lesson12DiscountResult calculate(Lesson12DiscountRequest request) {
        Lesson12DiscountRuleUnit ruleUnit = new Lesson12DiscountRuleUnit();
        Lesson12DiscountResult result = new Lesson12DiscountResult();
        Lesson12RuleExecutionContext context =
                new Lesson12RuleExecutionContext(Lesson12RulePhase.VALIDATION);

        ruleUnit.getRequests().add(request);
        ruleUnit.getResults().add(result);
        ruleUnit.getConfigs().add(discountConfig);
        DataHandle contextHandle = ruleUnit.getContexts().add(context);

        RuleConfig ruleConfig = ruleTraceFactory.create(context);

        try (RuleUnitInstance<Lesson12DiscountRuleUnit> instance =
                     RuleUnitProvider.get().createRuleUnitInstance(ruleUnit, ruleConfig)) {
            instance.fire();

            if (result.isValid()) {
                context.setPhase(Lesson12RulePhase.DISCOUNT);
                ruleUnit.getContexts().update(contextHandle, context);
                instance.fire();
            }

            QueryResults queryResults = instance.executeQuery("Find all discount candidates");
            for (QueryResultsRow row : queryResults) {
                Lesson12DiscountCandidate candidate =
                        (Lesson12DiscountCandidate) row.get("$candidate");
                result.getCandidates().add(candidate);
            }
        }

        if (result.isValid()) {
            selectBestCandidate(result);
        }

        result.setMatchedRules(new ArrayList<>(context.getMatchedRules()));
        result.setFiredRules(new ArrayList<>(context.getFiredRules()));
        return result;
    }

    /**
     * Selects the highest-percent candidate and copies it into final result
     * fields.
     *
     * <p>If no candidate exists, the request is valid but no discount applies.</p>
     *
     * @param result result containing queried candidate facts
     */
    private void selectBestCandidate(Lesson12DiscountResult result) {
        Optional<Lesson12DiscountCandidate> bestCandidate = result.getCandidates().stream()
                .max(Comparator.comparingInt(Lesson12DiscountCandidate::getDiscountPercent));

        if (bestCandidate.isEmpty()) {
            result.setReason("No discount candidate matched");
            return;
        }

        Lesson12DiscountCandidate selectedCandidate = bestCandidate.get();
        result.setSelectedCandidate(selectedCandidate);
        result.setDiscountPercent(selectedCandidate.getDiscountPercent());
        result.setReason(selectedCandidate.getReason());
    }
}
