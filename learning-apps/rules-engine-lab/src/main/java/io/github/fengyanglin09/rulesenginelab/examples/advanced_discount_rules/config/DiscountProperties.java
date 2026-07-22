package io.github.fengyanglin09.rulesenginelab.examples.advanced_discount_rules.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@Getter
@Setter
@ConfigurationProperties(prefix = "discount")
public class DiscountProperties {
    private int largeOrderPercent;
    private int vipPercent;
    private int normalPercent;

    private BigDecimal largeOrderMinimumAmount;
    private BigDecimal vipMinimumAmount;
    private BigDecimal normalMinimumAmount;


}
