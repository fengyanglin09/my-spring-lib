package io.github.fengyanglin09.rulesenginelab.lessons.lesson06_rule_unit_config.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Input fact for Lesson 06.
 */
@Getter
@Setter
public class Lesson06DiscountRequest {
    /**
     * Customer category inspected by discount rules.
     */
    private String customerType;

    /**
     * Order amount compared with thresholds from {@link Lesson06DiscountConfig}.
     */
    private BigDecimal orderAmount;
}
