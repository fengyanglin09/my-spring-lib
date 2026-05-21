package io.github.fengyanglin09.spalibdemo.rulesEngineDemo.controller;


import io.github.fengyanglin09.spalibdemo.rulesEngineDemo.DroolsDiscountService;
import io.github.fengyanglin09.spalibdemo.rulesEngineDemo.model.DiscountRequest;
import io.github.fengyanglin09.spalibdemo.rulesEngineDemo.model.DiscountResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/drools")
public class DroolsDemoController {

    private final DroolsDiscountService  droolsDiscountService;

    @PostMapping("/discount")
    public DiscountResult calculateDiscount(@RequestBody DiscountRequest discountRequest) {
        return droolsDiscountService.calculateDiscount(discountRequest);
    }

}
