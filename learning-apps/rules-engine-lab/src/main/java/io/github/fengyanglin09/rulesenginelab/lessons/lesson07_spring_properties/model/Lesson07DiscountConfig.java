package io.github.fengyanglin09.rulesenginelab.lessons.lesson07_spring_properties.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Configuration fact for Lesson 07.
 *
 * <p>This is the object Drools reads from {@code /configs}. Spring creates it
 * from {@code application.yml}, but the DRL does not need to know where the
 * values came from.</p>
 */
@Getter
@AllArgsConstructor
public class Lesson07DiscountConfig {
    private final int largeOrderPercent;
    private final int vipPercent;
    private final int normalPercent;
    private final BigDecimal largeOrderMinimumAmount;
    private final BigDecimal vipMinimumAmount;
    private final BigDecimal normalMinimumAmount;
}
