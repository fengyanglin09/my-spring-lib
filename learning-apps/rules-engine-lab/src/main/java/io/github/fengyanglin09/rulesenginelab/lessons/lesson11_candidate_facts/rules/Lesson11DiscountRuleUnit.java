package io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.rules;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.model.Lesson11DiscountCandidate;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.model.Lesson11DiscountConfig;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.model.Lesson11DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.model.Lesson11DiscountResult;
import io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.model.Lesson11RuleExecutionContext;
import lombok.Getter;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

/**
 * Rule Unit data model for Lesson 11.
 *
 * <p>The new store is {@code candidates}. Discount rules add candidate facts to
 * that store instead of modifying the final API result directly.</p>
 *
 * <p>Stores do not mean the service must fill every store. Some stores are
 * filled by Java before rules run. Other stores are empty at first and are
 * filled by rules during execution.</p>
 */
@Getter
public class Lesson11DiscountRuleUnit implements RuleUnitData {
    /**
     * Service-filled store: the user input is known before rules run.
     */
    private final DataStore<Lesson11DiscountRequest> requests = DataSource.createStore();

    /**
     * Service-filled store: rules need a result object they can update.
     */
    private final DataStore<Lesson11DiscountResult> results = DataSource.createStore();

    /**
     * Service-filled store: rules need thresholds and percentages.
     */
    private final DataStore<Lesson11DiscountConfig> configs = DataSource.createStore();

    /**
     * Service-filled store: rules need to know the current phase.
     */
    private final DataStore<Lesson11RuleExecutionContext> contexts = DataSource.createStore();

    /**
     * Rule-filled store: candidate facts are created by DRL after rules match.
     */
    private final DataStore<Lesson11DiscountCandidate> candidates = DataSource.createStore();
}
