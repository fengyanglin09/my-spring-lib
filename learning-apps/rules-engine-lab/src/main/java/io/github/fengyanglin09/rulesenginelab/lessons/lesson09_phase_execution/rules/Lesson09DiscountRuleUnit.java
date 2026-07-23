package io.github.fengyanglin09.rulesenginelab.lessons.lesson09_phase_execution.rules;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson09_phase_execution.model.Lesson09DiscountConfig;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson09_phase_execution.model.Lesson09DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson09_phase_execution.model.Lesson09DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson09_phase_execution.model.Lesson09RuleExecutionContext;
import lombok.Getter;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

/**
 * Rule Unit data model for Lesson 09.
 *
 * <p>The new store is {@code contexts}. In DRL it appears as {@code /contexts}
 * and controls which phase's rules are eligible to match.</p>
 */
@Getter
public class Lesson09DiscountRuleUnit implements RuleUnitData {
    private final DataStore<Lesson09DiscountRequest> requests = DataSource.createStore();
    private final DataStore<Lesson09DiscountResult> results = DataSource.createStore();
    private final DataStore<Lesson09DiscountConfig> configs = DataSource.createStore();
    private final DataStore<Lesson09RuleExecutionContext> contexts = DataSource.createStore();
}
