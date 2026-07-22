package io.github.fengyanglin09.rulesenginelab.lessons.lesson05_validation_rules.rules;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson05_validation_rules.model.Lesson05DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson05_validation_rules.model.Lesson05DiscountResult;
import lombok.Getter;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

/**
 * Rule Unit data model for Lesson 05.
 *
 * <p>The same Rule Unit contains both validation rules and discount rules. The
 * result fact acts as the shared state between those rules.</p>
 */
@Getter
public class Lesson05DiscountRuleUnit implements RuleUnitData {
    /**
     * Input facts inspected by validation and discount rules.
     */
    private final DataStore<Lesson05DiscountRequest> requests = DataSource.createStore();

    /**
     * Output facts modified by validation and discount rules.
     */
    private final DataStore<Lesson05DiscountResult> results = DataSource.createStore();
}
