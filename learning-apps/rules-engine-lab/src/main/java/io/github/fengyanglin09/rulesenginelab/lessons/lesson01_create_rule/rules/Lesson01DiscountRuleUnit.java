package io.github.fengyanglin09.rulesenginelab.lessons.lesson01_create_rule.rules;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson01_create_rule.model.Lesson01DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson01_create_rule.model.Lesson01DiscountResult;
import lombok.Getter;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

/**
 * Rule Unit data model for Lesson 01.
 *
 * <p>This class is the bridge between Java and the DRL file. Drools finds rules
 * whose DRL declares {@code unit Lesson01DiscountRuleUnit;} in the same package
 * as this class.</p>
 *
 * <p>The data stores act like named buckets of facts. In DRL, they are accessed
 * as {@code /requests} and {@code /results}.</p>
 */
@Getter
public class Lesson01DiscountRuleUnit implements RuleUnitData {
    /**
     * Input facts inspected by the rule.
     */
    private final DataStore<Lesson01DiscountRequest> requests = DataSource.createStore();

    /**
     * Output facts modified by the rule.
     */
    private final DataStore<Lesson01DiscountResult> results = DataSource.createStore();
}
