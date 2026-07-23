package io.github.fengyanglin09.rulesenginelab.lessons.lesson09_phase_execution.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Configuration fact for Lesson 09.
 */
@Getter
@AllArgsConstructor
public class Lesson09DiscountConfig {
    private final int largeOrderPercent;
    private final int vipPercent;
    private final int normalPercent;
    private final BigDecimal largeOrderMinimumAmount;
    private final BigDecimal vipMinimumAmount;
    private final BigDecimal normalMinimumAmount;

    /**
     * Creates the lesson's default business configuration.
     *
     * @return default config inserted into the Rule Unit
     */
    public static Lesson09DiscountConfig defaults() {
        return new Lesson09DiscountConfig(
                15,
                10,
                5,
                new BigDecimal("500"),
                new BigDecimal("100"),
                new BigDecimal("200")
        );
    }
}
