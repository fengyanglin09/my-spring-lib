package io.github.fengyanglin09.rulesenginelab.lessons.lesson03_salience.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Input fact for Lesson 03.
 *
 * <p>This request intentionally allows overlapping matches. For example, a VIP
 * customer with a large order can match both the VIP rule and the large-order
 * rule.</p>
 */
@Getter
@Setter
public class Lesson03DiscountRequest {
    /**
     * Customer category inspected by customer-specific rules.
     */
    private String customerType;

    /**
     * Order amount inspected by the large-order rule.
     */
    private BigDecimal orderAmount;
}
