package io.github.fengyanglin09.rulesenginelab.lessons.lesson11_candidate_facts.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Input fact for Lesson 11.
 */
@Getter
@Setter
public class Lesson11DiscountRequest {
    private String customerType;
    private BigDecimal orderAmount;
}
