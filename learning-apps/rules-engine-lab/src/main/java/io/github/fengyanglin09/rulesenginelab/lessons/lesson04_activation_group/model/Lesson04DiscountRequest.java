package io.github.fengyanglin09.rulesenginelab.lessons.lesson04_activation_group.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Input fact for Lesson 04.
 *
 * <p>The fields intentionally match Lesson 03 so the only new concept is
 * {@code activation-group} in the DRL file.</p>
 */
@Getter
@Setter
public class Lesson04DiscountRequest {
    /**
     * Customer category inspected by customer-specific rules.
     */
    private String customerType;

    /**
     * Order amount inspected by the large-order rule.
     */
    private BigDecimal orderAmount;
}
