package io.github.fengyanglin09.rulesenginelab.lessons.lesson13_accumulate.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Cart item fact for Lesson 13.
 */
@Getter
@Setter
public class Lesson13CartItem {
    /**
     * Product identifier shown in examples and Swagger.
     */
    private String sku;

    /**
     * Unit price in cents. Using cents keeps the accumulate lesson focused on
     * rule behavior instead of decimal math.
     */
    private int unitPriceCents;

    /**
     * Number of units ordered.
     */
    private int quantity;

    /**
     * Derived line total used by the DRL accumulate expression.
     *
     * @return unit price times quantity
     */
    public int getLineTotalCents() {
        return unitPriceCents * quantity;
    }
}
