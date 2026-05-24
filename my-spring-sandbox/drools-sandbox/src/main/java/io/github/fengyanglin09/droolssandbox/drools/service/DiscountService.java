package io.github.fengyanglin09.droolssandbox.drools.service;

import io.github.fengyanglin09.droolssandbox.drools.droolsConfig.DiscountRuleUnit;
import io.github.fengyanglin09.droolssandbox.drools.models.DiscountConfig;
import io.github.fengyanglin09.droolssandbox.drools.models.DiscountRequest;
import io.github.fengyanglin09.droolssandbox.drools.models.DiscountResult;
import lombok.RequiredArgsConstructor;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.api.conf.RuleConfig;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class DiscountService {

    private final Supplier<RuleConfig> ruleConfigSupplier;

    public DiscountResult calculate(DiscountRequest request) {
        DiscountRuleUnit ruleUnit = new DiscountRuleUnit();
        DiscountResult result = new DiscountResult();
        DiscountConfig discountConfig = new DiscountConfig(
                20,
                15,
                8,
                new BigDecimal("500"),
                new BigDecimal("100"),
                new BigDecimal("200")
        );

        RuleConfig ruleConfig = ruleConfigSupplier.get();

        ruleUnit.getRequests().add(request);
        ruleUnit.getResults().add(result);
        ruleUnit.getConfigs().add(discountConfig);

        try (RuleUnitInstance<DiscountRuleUnit> instance =
                     RuleUnitProvider.get().createRuleUnitInstance(ruleUnit, ruleConfig)) {
            instance.fire();
        }

        return result;
    }
}
