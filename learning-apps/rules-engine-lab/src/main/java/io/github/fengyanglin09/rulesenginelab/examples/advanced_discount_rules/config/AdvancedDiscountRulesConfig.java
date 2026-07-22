package io.github.fengyanglin09.rulesenginelab.examples.advanced_discount_rules.config;

import io.github.fengyanglin09.rulesenginelab.examples.advanced_discount_rules.model.DiscountConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdvancedDiscountRulesConfig {

    @Bean
    public DiscountConfig discountConfig(DiscountProperties properties) {
        return new DiscountConfig(
                properties.getLargeOrderPercent(),
                properties.getVipPercent(),
                properties.getNormalPercent(),
                properties.getLargeOrderMinimumAmount(),
                properties.getVipMinimumAmount(),
                properties.getNormalMinimumAmount()
        );
    }

}
