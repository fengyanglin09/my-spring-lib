package io.github.fengyanglin09.rulesenginelab.lessons.lesson13_accumulate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Configuration fact for Lesson 13.
 */
@Getter
@AllArgsConstructor
public class Lesson13DiscountConfig {
    private final int largeCartPercent;
    private final int vipPercent;
    private final int largeCartMinimumCents;

    /**
     * Creates the lesson's default business configuration.
     *
     * @return default config inserted into the Rule Unit
     */
    public static Lesson13DiscountConfig defaults() {
        return new Lesson13DiscountConfig(15, 10, 50_000);
    }
}
