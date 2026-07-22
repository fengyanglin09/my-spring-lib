package io.github.fengyanglin09.rulesenginelab.lessons.lesson07_spring_properties.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Input fact for Lesson 07.
 */
@Getter
@Setter
public class Lesson07DiscountRequest {
    /**
     * Customer category inspected by discount rules.
     */
    private String customerType;

    /**
     * Order amount compared with thresholds loaded from Spring properties.
     */
    private BigDecimal orderAmount;
}
