package io.github.fengyanglin09.rulesenginelab.lessons.lesson14_decision_table.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Input fact for Lesson 14.
 *
 * <p>This lesson keeps the request intentionally small so the focus stays on
 * the decision table rows. Amounts use cents to avoid decimal rounding issues
 * while learning rule mechanics.</p>
 */
@Getter
@Setter
public class Lesson14DiscountRequest {
    private String customerType;
    private int orderAmountCents;
}
