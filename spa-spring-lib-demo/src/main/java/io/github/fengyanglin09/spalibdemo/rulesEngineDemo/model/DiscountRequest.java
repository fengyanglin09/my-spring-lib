package io.github.fengyanglin09.spalibdemo.rulesEngineDemo.model;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class DiscountRequest {
    private String customerType;
    private String discountType;
    private BigDecimal orderAmount;


}
