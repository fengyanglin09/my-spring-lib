package io.github.fengyanglin09.rulesenginelab;

import io.github.fengyanglin09.rulesenginelab.common.config.DroolsDebugProperties;
import io.github.fengyanglin09.rulesenginelab.examples.advanced_discount_rules.config.DiscountProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        DiscountProperties.class,
        DroolsDebugProperties.class
})
public class RulesEngineLabApplication {

    public static void main(String[] args) {
        SpringApplication.run(RulesEngineLabApplication.class, args);
    }

}
