package io.github.fengyanglin09.rulesenginelab.lessons.lesson02_multiple_rules.rules;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson02_multiple_rules.model.Lesson02DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson02_multiple_rules.model.Lesson02DiscountResult;
import lombok.Getter;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

/**
 * Rule Unit data model for Lesson 02.
 *
 * <p>The Rule Unit is still the bridge between Java and DRL. The matching DRL
 * file declares {@code unit Lesson02DiscountRuleUnit;} and can read these
 * stores through {@code /requests} and {@code /results}.</p>
 */
@Getter
public class Lesson02DiscountRuleUnit implements RuleUnitData {
    /**
     * Input facts. Every rule in the Lesson 02 DRL checks this same store.
     */
    private final DataStore<Lesson02DiscountRequest> requests = DataSource.createStore();

    /**
     * Output facts. Every rule in the Lesson 02 DRL writes to this same store.
     */
    private final DataStore<Lesson02DiscountResult> results = DataSource.createStore();
}
