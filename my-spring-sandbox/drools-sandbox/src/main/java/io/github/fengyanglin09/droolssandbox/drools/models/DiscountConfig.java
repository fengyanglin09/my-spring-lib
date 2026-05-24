package io.github.fengyanglin09.droolssandbox.drools.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DiscountConfig {
    private final int largeOrderPercent;
    private final int vipPercent;
    private final int normalPercent;
}
