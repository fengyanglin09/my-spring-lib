package io.github.fengyanglin09.rulesenginelab.lessons.lesson10_queries.service;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson10_queries.model.Lesson10DiscountAudit;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson10_queries.model.Lesson10DiscountConfig;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson10_queries.model.Lesson10DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson10_queries.model.Lesson10DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson10_queries.model.Lesson10RuleExecutionContext;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson10_queries.model.Lesson10RulePhase;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson10_queries.rules.Lesson10DiscountRuleUnit;
import org.drools.ruleunits.api.DataHandle;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.api.conf.RuleConfig;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Orchestrates Lesson 10 rule execution and query usage.
 */
@Service
public class Lesson10DiscountService {

    private final Lesson10DiscountConfig discountConfig;
    private final Lesson10RuleTraceFactory ruleTraceFactory;

    /**
     * Creates the Spring service with default config and tracing support.
     */
    public Lesson10DiscountService() {
        this(Lesson10DiscountConfig.defaults(), new Lesson10RuleTraceFactory());
    }

    /**
     * Creates the service with explicit dependencies, which is useful for tests.
     *
     * @param discountConfig config fact inserted into the Rule Unit
     * @param ruleTraceFactory factory for Drools listener config
     */
    public Lesson10DiscountService(
            Lesson10DiscountConfig discountConfig,
            Lesson10RuleTraceFactory ruleTraceFactory
    ) {
        this.discountConfig = discountConfig;
        this.ruleTraceFactory = ruleTraceFactory;
    }

    /**
     * Runs validation and discount phases, then queries audit facts created by
     * the rules.
     *
     * <p>{@code instance.executeQuery("Find all discount audits")} runs the DRL
     * query block. Each row contains the variable named in the query, in this
     * lesson {@code "$audit"}.</p>
     *
     * @param request input fact for Drools to validate and inspect
     * @return output fact containing validation, discount, trace, and audit data
     */
    public Lesson10DiscountResult calculate(Lesson10DiscountRequest request) {
        Lesson10DiscountRuleUnit ruleUnit = new Lesson10DiscountRuleUnit();
        Lesson10DiscountResult result = new Lesson10DiscountResult();
        Lesson10RuleExecutionContext context =
                new Lesson10RuleExecutionContext(Lesson10RulePhase.VALIDATION);

        ruleUnit.getRequests().add(request);
        ruleUnit.getResults().add(result);
        ruleUnit.getConfigs().add(discountConfig);
        DataHandle contextHandle = ruleUnit.getContexts().add(context);

        RuleConfig ruleConfig = ruleTraceFactory.create(context);

        try (RuleUnitInstance<Lesson10DiscountRuleUnit> instance =
                     RuleUnitProvider.get().createRuleUnitInstance(ruleUnit, ruleConfig)) {
            instance.fire();

            if (result.isValid()) {
                context.setPhase(Lesson10RulePhase.DISCOUNT);
                ruleUnit.getContexts().update(contextHandle, context);
                instance.fire();
            }

            /*
             * Run the DRL query named "Find all discount audits".
             *
             * The query is defined in Lesson10DiscountRuleUnit.drl:
             *   query "Find all discount audits"
             *       $audit : /audits
             *   end
             *
             * Each QueryResultsRow is one matching row from that query. Since
             * the query binds audit facts to the variable name $audit, Java
             * reads that value with row.get("$audit").
             *
             * The audit fact is internal rule-engine data. We copy only the
             * message into the API result so the response stays simple.
             */
            QueryResults queryResults = instance.executeQuery("Find all discount audits");
            for (QueryResultsRow row : queryResults) {
                Lesson10DiscountAudit audit = (Lesson10DiscountAudit) row.get("$audit");
                result.getAuditMessages().add(audit.getMessage());
            }
        }

        result.setMatchedRules(new ArrayList<>(context.getMatchedRules()));
        result.setFiredRules(new ArrayList<>(context.getFiredRules()));
        return result;
    }
}
