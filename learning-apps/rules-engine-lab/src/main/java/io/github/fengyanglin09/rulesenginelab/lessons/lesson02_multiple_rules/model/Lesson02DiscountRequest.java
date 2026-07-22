package io.github.fengyanglin09.rulesenginelab.lessons.lesson02_multiple_rules.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Input fact for Lesson 02.
 *
 * <p>This looks almost the same as Lesson 01 on purpose. The lesson is about
 * adding more rules, not about changing the Java object model.</p>
 */
@Getter
@Setter
public class Lesson02DiscountRequest {
    /**
     * Customer category inspected by the VIP and normal-customer rules.
     */
    private String customerType;

    /**
     * Order amount inspected by the large-order rule.
     */
    private BigDecimal orderAmount;
}
