package io.github.fengyanglin09.rulesenginelab.examples.advanced_discount_rules.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DiscountAudit {
    private String ruleName;
    private String message;
}
