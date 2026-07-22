package io.github.fengyanglin09.rulesenginelab.lessons.lesson01_create_rule.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Input fact for Lesson 01.
 *
 * <p>A "fact" is an object Drools can inspect while evaluating rules. In this
 * lesson, the DRL rule only checks {@code customerType}, while
 * {@code orderAmount} is present to make the request feel like a real discount
 * request.</p>
 */
@Getter
@Setter
public class Lesson01DiscountRequest {
    /**
     * Customer category used by the DRL condition.
     *
     * <p>Lesson 01 keeps this as a plain string so the first rule stays easy to
     * read. Later lessons can introduce enums and validation.</p>
     */
    private String customerType;

    /**
     * Order amount included for realism. The first lesson rule does not use it.
     */
    private BigDecimal orderAmount;
}
