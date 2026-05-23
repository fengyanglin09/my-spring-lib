package io.github.fengyanglin09.droolssandbox.drools.controller;

import io.github.fengyanglin09.droolssandbox.drools.models.DiscountRequest;
import io.github.fengyanglin09.droolssandbox.drools.models.DiscountResult;
import io.github.fengyanglin09.droolssandbox.drools.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/discount")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService  discountService;

    @PostMapping("/calculate")
    public DiscountResult calculate(@RequestBody DiscountRequest request) {
        return discountService.calculate(request);
    }
}
