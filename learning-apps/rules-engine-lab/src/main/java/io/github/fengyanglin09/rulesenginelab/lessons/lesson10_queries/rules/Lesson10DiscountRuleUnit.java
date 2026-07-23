package io.github.fengyanglin09.rulesenginelab.lessons.lesson10_queries.rules;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson10_queries.model.Lesson10DiscountAudit;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson10_queries.model.Lesson10DiscountConfig;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson10_queries.model.Lesson10DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson10_queries.model.Lesson10DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson10_queries.model.Lesson10RuleExecutionContext;
import lombok.Getter;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

/**
 * Rule Unit data model for Lesson 10.
 *
 * <p>The new store is {@code audits}. DRL rules add audit facts to it, and the
 * service retrieves those facts with a DRL query.</p>
 */
@Getter
public class Lesson10DiscountRuleUnit implements RuleUnitData {
    private final DataStore<Lesson10DiscountRequest> requests = DataSource.createStore();
    private final DataStore<Lesson10DiscountResult> results = DataSource.createStore();
    private final DataStore<Lesson10DiscountConfig> configs = DataSource.createStore();
    private final DataStore<Lesson10RuleExecutionContext> contexts = DataSource.createStore();
    private final DataStore<Lesson10DiscountAudit> audits = DataSource.createStore();
}
