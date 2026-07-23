package io.github.fengyanglin09.rulesenginelab.lessons.lesson14_decision_table.rules;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson14_decision_table.model.Lesson14DecisionRow;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson14_decision_table.model.Lesson14DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson14_decision_table.model.Lesson14DiscountResult;
import lombok.Getter;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

/**
 * Rule Unit data model for Lesson 14.
 *
 * <p>The new store is {@code decisionRows}. Each CSV row becomes a fact.
 * Drools does not read the CSV directly in this lesson; Java loads the table,
 * then DRL reasons over the row facts.</p>
 */
@Getter
public class Lesson14DiscountRuleUnit implements RuleUnitData {
    private final DataStore<Lesson14DiscountRequest> requests = DataSource.createStore();
    private final DataStore<Lesson14DiscountResult> results = DataSource.createStore();
    private final DataStore<Lesson14DecisionRow> decisionRows = DataSource.createStore();
}
