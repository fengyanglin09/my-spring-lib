package io.github.fengyanglin09.droolssandbox.drools.models;


import lombok.Data;

@Data
public class DiscountResult {
    private boolean valid = true;
    private int discountPercent;
    private String reason;
}
