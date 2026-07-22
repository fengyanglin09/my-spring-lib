package io.github.fengyanglin09.rulesenginelab.lessons.lesson07_spring_properties.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

/**
 * Spring-bound properties for Lesson 07.
 *
 * <p>Spring reads values from {@code application.yml} using the prefix
 * {@code rules-engine.lessons.lesson-07.discount}. This class is only about
 * loading configuration; it does not know anything about Drools.</p>
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "rules-engine.lessons.lesson-07.discount")
public class Lesson07DiscountProperties {
    /**
     * Discount percentage for large orders.
     */
    private int largeOrderPercent;

    /**
     * Discount percentage for VIP customers.
     */
    private int vipPercent;

    /**
     * Discount percentage for normal customers.
     */
    private int normalPercent;

    /**
     * Minimum order amount required for the large-order rule.
     */
    private BigDecimal largeOrderMinimumAmount;

    /**
     * Minimum order amount required for the VIP rule.
     */
    private BigDecimal vipMinimumAmount;

    /**
     * Minimum order amount required for the normal-customer rule.
     */
    private BigDecimal normalMinimumAmount;
}
