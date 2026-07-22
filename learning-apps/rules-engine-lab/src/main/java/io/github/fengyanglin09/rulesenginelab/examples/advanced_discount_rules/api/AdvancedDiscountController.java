package io.github.fengyanglin09.rulesenginelab.examples.advanced_discount_rules.api;

import io.github.fengyanglin09.rulesenginelab.examples.advanced_discount_rules.model.DiscountRequest;
import io.github.fengyanglin09.rulesenginelab.examples.advanced_discount_rules.model.DiscountResult;
import io.github.fengyanglin09.rulesenginelab.examples.advanced_discount_rules.service.AdvancedDiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rules-engine/examples/advanced-discount")
@RequiredArgsConstructor
public class AdvancedDiscountController {

    private final AdvancedDiscountService discountService;

    @PostMapping("/calculate")
    public DiscountResult calculate(@RequestBody DiscountRequest request) {
        return discountService.calculate(request);
    }
}
