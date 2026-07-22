package io.github.fengyanglin09.rulesenginelab.lessons.lesson07_spring_properties.rules;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson07_spring_properties.model.Lesson07DiscountConfig;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson07_spring_properties.model.Lesson07DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson07_spring_properties.model.Lesson07DiscountResult;
import lombok.Getter;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

/**
 * Rule Unit data model for Lesson 07.
 *
 * <p>Just like Lesson 06, this exposes {@code /configs} to DRL. The difference
 * is that Spring now creates the config object from {@code application.yml}.</p>
 */
@Getter
public class Lesson07DiscountRuleUnit implements RuleUnitData {
    private final DataStore<Lesson07DiscountRequest> requests = DataSource.createStore();
    private final DataStore<Lesson07DiscountResult> results = DataSource.createStore();
    private final DataStore<Lesson07DiscountConfig> configs = DataSource.createStore();
}
