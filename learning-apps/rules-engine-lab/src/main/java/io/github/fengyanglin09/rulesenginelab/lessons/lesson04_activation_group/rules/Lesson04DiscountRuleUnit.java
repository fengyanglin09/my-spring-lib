package io.github.fengyanglin09.rulesenginelab.lessons.lesson04_activation_group.rules;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson04_activation_group.model.Lesson04DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson04_activation_group.model.Lesson04DiscountResult;
import lombok.Getter;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

/**
 * Rule Unit data model for Lesson 04.
 *
 * <p>The Java bridge is still simple. The one-winner behavior lives in DRL
 * through {@code activation-group}.</p>
 */
@Getter
public class Lesson04DiscountRuleUnit implements RuleUnitData {
    /**
     * Input facts inspected by discount rules.
     */
    private final DataStore<Lesson04DiscountRequest> requests = DataSource.createStore();

    /**
     * Output facts modified by the winning discount rule.
     */
    private final DataStore<Lesson04DiscountResult> results = DataSource.createStore();
}
