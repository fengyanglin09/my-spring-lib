package io.github.fengyanglin09.droolssandbox.drools.models;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class DiscountRequest {
    private CustomerType customerType;
    private DiscountMode discountMode;
    private BigDecimal orderAmount;
}
