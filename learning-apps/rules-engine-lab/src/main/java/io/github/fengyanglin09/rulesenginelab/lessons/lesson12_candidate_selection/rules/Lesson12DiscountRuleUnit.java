package io.github.fengyanglin09.rulesenginelab.lessons.lesson12_candidate_selection.rules;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson12_candidate_selection.model.Lesson12DiscountCandidate;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson12_candidate_selection.model.Lesson12DiscountConfig;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson12_candidate_selection.model.Lesson12DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson12_candidate_selection.model.Lesson12DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson12_candidate_selection.model.Lesson12RuleExecutionContext;
import lombok.Getter;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

/**
 * Rule Unit data model for Lesson 12.
 *
 * <p>Like Lesson 11, the candidates store starts empty and is filled by DRL
 * rules during execution.</p>
 */
@Getter
public class Lesson12DiscountRuleUnit implements RuleUnitData {
    private final DataStore<Lesson12DiscountRequest> requests = DataSource.createStore();
    private final DataStore<Lesson12DiscountResult> results = DataSource.createStore();
    private final DataStore<Lesson12DiscountConfig> configs = DataSource.createStore();
    private final DataStore<Lesson12RuleExecutionContext> contexts = DataSource.createStore();
    private final DataStore<Lesson12DiscountCandidate> candidates = DataSource.createStore();
}
