package io.github.fengyanglin09.rulesenginelab.lessons.lesson05_validation_rules.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Input fact for Lesson 05.
 *
 * <p>This lesson deliberately allows nullable fields so Drools validation rules
 * can detect missing data before discount rules run.</p>
 */
@Getter
@Setter
public class Lesson05DiscountRequest {
    /**
     * Customer category. Validation rules check that it is present.
     */
    private String customerType;

    /**
     * Order amount. Validation rules check that it is present and not negative.
     */
    private BigDecimal orderAmount;
}
