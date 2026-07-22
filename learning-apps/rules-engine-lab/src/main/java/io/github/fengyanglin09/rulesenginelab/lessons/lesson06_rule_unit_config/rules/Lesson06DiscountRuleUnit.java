package io.github.fengyanglin09.rulesenginelab.lessons.lesson06_rule_unit_config.rules;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson06_rule_unit_config.model.Lesson06DiscountConfig;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson06_rule_unit_config.model.Lesson06DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson06_rule_unit_config.model.Lesson06DiscountResult;
import lombok.Getter;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

/**
 * Rule Unit data model for Lesson 06.
 *
 * <p>The new store is {@code configs}. In DRL, it is read as {@code /configs},
 * just like {@code /requests} and {@code /results}.</p>
 */
@Getter
public class Lesson06DiscountRuleUnit implements RuleUnitData {
    /**
     * Input facts inspected by validation and discount rules.
     */
    private final DataStore<Lesson06DiscountRequest> requests = DataSource.createStore();

    /**
     * Output facts modified by validation and discount rules.
     */
    private final DataStore<Lesson06DiscountResult> results = DataSource.createStore();

    /**
     * Configuration facts that provide thresholds and percentages to DRL.
     */
    private final DataStore<Lesson06DiscountConfig> configs = DataSource.createStore();
}
