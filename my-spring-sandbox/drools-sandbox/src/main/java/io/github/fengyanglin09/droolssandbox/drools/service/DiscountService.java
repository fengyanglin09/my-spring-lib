package io.github.fengyanglin09.droolssandbox.drools.service;

import io.github.fengyanglin09.droolssandbox.drools.droolsConfig.DiscountRuleUnit;
import io.github.fengyanglin09.droolssandbox.drools.models.DiscountConfig;
import io.github.fengyanglin09.droolssandbox.drools.models.DiscountRequest;
import io.github.fengyanglin09.droolssandbox.drools.models.DiscountResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.api.conf.RuleConfig;
import org.kie.api.runtime.rule.AgendaFilter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscountService {


    private static final AgendaFilter VALIDATION_RULES =
            match -> match.getRule().getName().startsWith("Missing ");

    private static final AgendaFilter NON_VALIDATION_RULES =
            match -> !match.getRule().getName().startsWith("Missing ");



    private final Supplier<RuleConfig> ruleConfigSupplier;

    private final DiscountConfig discountConfig;

    public DiscountResult calculate(DiscountRequest request) {
        DiscountRuleUnit ruleUnit = new DiscountRuleUnit();
        DiscountResult result = new DiscountResult();

        RuleConfig ruleConfig = ruleConfigSupplier.get();

        ruleUnit.getRequests().add(request);
        ruleUnit.getResults().add(result);
        ruleUnit.getConfigs().add(discountConfig);

        try (RuleUnitInstance<DiscountRuleUnit> instance =
                     RuleUnitProvider.get().createRuleUnitInstance(ruleUnit, ruleConfig)) {
//            instance.fire(match -> match.getRule().getName().startsWith("Missing"));

//            instance.fire(NON_VALIDATION_RULES);

            instance.fire(categoryFilter("discount"));
//            instance.fire();
        }

        return result;
    }


    private static AgendaFilter categoryFilter(String category) {

        return match -> {
            String ruleName = match.getRule().getName();
            Map<String, Object> metaData = match.getRule().getMetaData();

            log.info("FILTER CHECK rule= {} metadata= {}", ruleName, metaData);

            return category.equals(metaData.get("category"));

        };
    }

}
