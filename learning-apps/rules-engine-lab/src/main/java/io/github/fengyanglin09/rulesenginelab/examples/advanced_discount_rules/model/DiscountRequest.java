package io.github.fengyanglin09.rulesenginelab.examples.advanced_discount_rules.model;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class DiscountRequest {
    private CustomerType customerType;
    private DiscountMode discountMode;
    private BigDecimal orderAmount;
}
