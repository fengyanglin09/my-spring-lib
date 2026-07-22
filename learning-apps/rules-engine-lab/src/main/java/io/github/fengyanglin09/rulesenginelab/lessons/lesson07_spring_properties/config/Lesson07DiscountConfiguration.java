package io.github.fengyanglin09.rulesenginelab.lessons.lesson07_spring_properties.config;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson07_spring_properties.model.Lesson07DiscountConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Converts Spring properties into the Drools config fact used by Lesson 07.
 */
@Configuration
public class Lesson07DiscountConfiguration {

    /**
     * Creates the config fact that the service inserts into the Rule Unit.
     *
     * <p>This separates two responsibilities:</p>
     *
     * <p>{@link Lesson07DiscountProperties} reads values from Spring.</p>
     *
     * <p>{@link Lesson07DiscountConfig} is the plain Java object Drools sees as
     * a fact.</p>
     *
     * @param properties values loaded from {@code application.yml}
     * @return config fact for the Lesson 07 Rule Unit
     */
    @Bean
    public Lesson07DiscountConfig lesson07DiscountConfig(Lesson07DiscountProperties properties) {
        return new Lesson07DiscountConfig(
                properties.getLargeOrderPercent(),
                properties.getVipPercent(),
                properties.getNormalPercent(),
                properties.getLargeOrderMinimumAmount(),
                properties.getVipMinimumAmount(),
                properties.getNormalMinimumAmount()
        );
    }
}
