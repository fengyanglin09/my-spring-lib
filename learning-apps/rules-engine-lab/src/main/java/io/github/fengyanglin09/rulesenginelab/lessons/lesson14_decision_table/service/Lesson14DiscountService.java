package io.github.fengyanglin09.rulesenginelab.lessons.lesson14_decision_table.service;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson14_decision_table.model.Lesson14DecisionRow;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson14_decision_table.model.Lesson14DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson14_decision_table.model.Lesson14DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson14_decision_table.rules.Lesson14DiscountRuleUnit;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Orchestrates Lesson 14 rule execution.
 *
 * <p>The service loads decision-table rows, inserts them as facts, and lets
 * DRL select the best matching row. Java owns loading the table; Drools owns
 * deciding which row applies.</p>
 */
@Service
public class Lesson14DiscountService {

    private final Lesson14DecisionTableLoader decisionTableLoader;

    /**
     * Creates the service with its decision-table loader.
     *
     * @param decisionTableLoader loads CSV rows from the classpath
     */
    public Lesson14DiscountService(Lesson14DecisionTableLoader decisionTableLoader) {
        this.decisionTableLoader = decisionTableLoader;
    }

    /**
     * Inserts the request, result, and decision rows, then fires the rules.
     *
     * @param request input discount request
     * @return output fact with the selected row and discount
     */
    public Lesson14DiscountResult calculate(Lesson14DiscountRequest request) {
        Lesson14DiscountRuleUnit ruleUnit = new Lesson14DiscountRuleUnit();
        Lesson14DiscountResult result = new Lesson14DiscountResult();
        List<Lesson14DecisionRow> rows = decisionTableLoader.loadRows();

        ruleUnit.getRequests().add(request);
        ruleUnit.getResults().add(result);
        for (Lesson14DecisionRow row : rows) {
            ruleUnit.getDecisionRows().add(row);
        }

        try (RuleUnitInstance<Lesson14DiscountRuleUnit> instance =
                     RuleUnitProvider.get().createRuleUnitInstance(ruleUnit)) {
            instance.fire();
        }

        return result;
    }
}
