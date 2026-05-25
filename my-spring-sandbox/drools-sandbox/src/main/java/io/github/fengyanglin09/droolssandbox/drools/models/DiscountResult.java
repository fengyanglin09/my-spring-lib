package io.github.fengyanglin09.droolssandbox.drools.models;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DiscountResult {
    private boolean valid = true;
    private int discountPercent;
    private String reason;
    private List<String> firedRules = new ArrayList<>();
    private List<String> matchedRules = new ArrayList<>();
}
