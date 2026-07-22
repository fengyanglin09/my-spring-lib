package io.github.fengyanglin09.rulesenginelab.lessons.lesson06_rule_unit_config.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Configuration fact for Lesson 06.
 *
 * <p>Earlier lessons hard-coded thresholds and percentages directly in DRL.
 * This lesson moves those numbers into a Java object and inserts that object
 * into the Rule Unit as a fact.</p>
 */
@Getter
@AllArgsConstructor
public class Lesson06DiscountConfig {
    /**
     * Discount percentage for large orders.
     */
    private final int largeOrderPercent;

    /**
     * Discount percentage for VIP customers.
     */
    private final int vipPercent;

    /**
     * Discount percentage for normal customers.
     */
    private final int normalPercent;

    /**
     * Minimum order amount required for the large-order rule.
     */
    private final BigDecimal largeOrderMinimumAmount;

    /**
     * Minimum order amount required for the VIP rule.
     */
    private final BigDecimal vipMinimumAmount;

    /**
     * Minimum order amount required for the normal-customer rule.
     */
    private final BigDecimal normalMinimumAmount;

    /**
     * Creates the lesson's default business configuration.
     *
     * @return default config used by the Spring service
     */
    public static Lesson06DiscountConfig defaults() {
        return new Lesson06DiscountConfig(
                15,
                10,
                5,
                new BigDecimal("500"),
                new BigDecimal("100"),
                new BigDecimal("200")
        );
    }
}
