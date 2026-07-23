package io.github.fengyanglin09.rulesenginelab.lessons.lesson12_candidate_selection.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Input fact for Lesson 12.
 */
@Getter
@Setter
public class Lesson12DiscountRequest {
    private String customerType;
    private BigDecimal orderAmount;
}
