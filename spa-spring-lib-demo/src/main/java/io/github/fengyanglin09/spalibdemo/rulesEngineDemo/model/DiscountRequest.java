package io.github.fengyanglin09.spalibdemo.rulesEngineDemo.model;

import io.github.fengyanglin09.spalibdemo.rulesEngineDemo.config.CustomerType;
import io.github.fengyanglin09.spalibdemo.rulesEngineDemo.config.DiscountType;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class DiscountRequest {
    private CustomerType customerType;
    private DiscountType discountType;
    private BigDecimal orderAmount;


}
