package io.github.fengyanglin09.rulesenginelab.lessons.lesson03_salience.rules;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson03_salience.model.Lesson03DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson03_salience.model.Lesson03DiscountResult;
import lombok.Getter;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

/**
 * Rule Unit data model for Lesson 03.
 *
 * <p>The stores are the same idea as Lessons 01 and 02. The new behavior comes
 * from the DRL rules using {@code salience} to influence firing order.</p>
 */
@Getter
public class Lesson03DiscountRuleUnit implements RuleUnitData {
    /**
     * Input facts inspected by the salience demo rules.
     */
    private final DataStore<Lesson03DiscountRequest> requests = DataSource.createStore();

    /**
     * Output facts modified by the salience demo rules.
     */
    private final DataStore<Lesson03DiscountResult> results = DataSource.createStore();
}
