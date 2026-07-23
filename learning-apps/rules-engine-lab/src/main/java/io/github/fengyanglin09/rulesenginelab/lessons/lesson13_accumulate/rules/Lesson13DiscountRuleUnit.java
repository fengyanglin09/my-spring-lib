package io.github.fengyanglin09.rulesenginelab.lessons.lesson13_accumulate.rules;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson13_accumulate.model.Lesson13CartItem;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson13_accumulate.model.Lesson13DiscountConfig;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson13_accumulate.model.Lesson13DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson13_accumulate.model.Lesson13DiscountResult;
import lombok.Getter;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

/**
 * Rule Unit data model for Lesson 13.
 *
 * <p>The new store is {@code items}. Each cart item is inserted as its own fact
 * so DRL can aggregate over all item facts with {@code accumulate}.</p>
 */
@Getter
public class Lesson13DiscountRuleUnit implements RuleUnitData {
    private final DataStore<Lesson13DiscountRequest> requests = DataSource.createStore();
    private final DataStore<Lesson13DiscountResult> results = DataSource.createStore();
    private final DataStore<Lesson13DiscountConfig> configs = DataSource.createStore();
    private final DataStore<Lesson13CartItem> items = DataSource.createStore();
}
