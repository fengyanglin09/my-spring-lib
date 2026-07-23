package io.github.fengyanglin09.rulesenginelab.lessons.lesson09_phase_execution.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Input fact for Lesson 09.
 */
@Getter
@Setter
public class Lesson09DiscountRequest {
    /**
     * Customer category inspected by validation and discount rules.
     */
    private String customerType;

    /**
     * Order amount inspected by validation and discount rules.
     */
    private BigDecimal orderAmount;
}
