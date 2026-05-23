package io.github.fengyanglin09.droolssandbox.drools.droolsConfig;

import io.github.fengyanglin09.droolssandbox.drools.models.DiscountRequest;
import io.github.fengyanglin09.droolssandbox.drools.models.DiscountResult;
import lombok.Getter;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;


@Getter
public class DiscountRuleUnit implements RuleUnitData {

    private final DataStore<DiscountRequest> requests = DataSource.createStore();
    private final DataStore<DiscountResult> results = DataSource.createStore();


}
