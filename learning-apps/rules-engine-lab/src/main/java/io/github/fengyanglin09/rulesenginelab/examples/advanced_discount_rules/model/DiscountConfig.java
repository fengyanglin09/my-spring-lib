package io.github.fengyanglin09.rulesenginelab.examples.advanced_discount_rules.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class DiscountConfig {
    private final int largeOrderPercent;
    private final int vipPercent;
    private final int normalPercent;

    private final BigDecimal largeOrderMinimumAmount;
    private final BigDecimal vipMinimumAmount;
    private final BigDecimal normalMinimumAmount;
}
