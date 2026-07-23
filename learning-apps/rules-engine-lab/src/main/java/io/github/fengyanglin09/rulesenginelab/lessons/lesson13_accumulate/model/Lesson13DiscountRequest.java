package io.github.fengyanglin09.rulesenginelab.lessons.lesson13_accumulate.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Input model for Lesson 13.
 *
 * <p>The service splits the {@code items} list into individual Drools facts
 * before rules run.</p>
 */
@Getter
@Setter
public class Lesson13DiscountRequest {
    private String customerType;
    private List<Lesson13CartItem> items = new ArrayList<>();
}
