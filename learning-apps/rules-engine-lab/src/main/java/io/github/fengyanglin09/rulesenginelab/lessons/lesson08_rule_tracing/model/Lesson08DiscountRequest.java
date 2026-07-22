package io.github.fengyanglin09.rulesenginelab.lessons.lesson08_rule_tracing.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Input fact for Lesson 08.
 */
@Getter
@Setter
public class Lesson08DiscountRequest {
    /**
     * Customer category inspected by discount rules.
     */
    private String customerType;

    /**
     * Order amount compared with configured thresholds.
     */
    private BigDecimal orderAmount;
}
