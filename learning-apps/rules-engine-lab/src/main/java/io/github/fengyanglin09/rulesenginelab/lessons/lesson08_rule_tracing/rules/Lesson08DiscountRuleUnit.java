package io.github.fengyanglin09.rulesenginelab.lessons.lesson08_rule_tracing.rules;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson08_rule_tracing.model.Lesson08DiscountConfig;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson08_rule_tracing.model.Lesson08DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson08_rule_tracing.model.Lesson08DiscountResult;
import lombok.Getter;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

/**
 * Rule Unit data model for Lesson 08.
 */
@Getter
public class Lesson08DiscountRuleUnit implements RuleUnitData {
    private final DataStore<Lesson08DiscountRequest> requests = DataSource.createStore();
    private final DataStore<Lesson08DiscountResult> results = DataSource.createStore();
    private final DataStore<Lesson08DiscountConfig> configs = DataSource.createStore();
}
