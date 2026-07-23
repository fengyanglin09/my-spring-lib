package io.github.fengyanglin09.rulesenginelab.lessons.lesson10_queries.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Input fact for Lesson 10.
 */
@Getter
@Setter
public class Lesson10DiscountRequest {
    private String customerType;
    private BigDecimal orderAmount;
}
