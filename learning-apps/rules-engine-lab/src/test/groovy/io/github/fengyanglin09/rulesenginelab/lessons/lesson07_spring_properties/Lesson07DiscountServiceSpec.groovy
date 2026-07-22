package io.github.fengyanglin09.rulesenginelab.lessons.lesson07_spring_properties

import io.github.fengyanglin09.rulesenginelab.lessons.lesson07_spring_properties.config.Lesson07DiscountConfiguration
import io.github.fengyanglin09.rulesenginelab.lessons.lesson07_spring_properties.config.Lesson07DiscountProperties
import io.github.fengyanglin09.rulesenginelab.lessons.lesson07_spring_properties.model.Lesson07DiscountConfig
import io.github.fengyanglin09.rulesenginelab.lessons.lesson07_spring_properties.model.Lesson07DiscountRequest
import io.github.fengyanglin09.rulesenginelab.lessons.lesson07_spring_properties.service.Lesson07DiscountService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import spock.lang.Specification

class Lesson07DiscountServiceSpec extends Specification {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig)
            .withPropertyValues(
                    "rules-engine.lessons.lesson-07.discount.large-order-percent=22",
                    "rules-engine.lessons.lesson-07.discount.vip-percent=13",
                    "rules-engine.lessons.lesson-07.discount.normal-percent=6",
                    "rules-engine.lessons.lesson-07.discount.large-order-minimum-amount=700",
                    "rules-engine.lessons.lesson-07.discount.vip-minimum-amount=100",
                    "rules-engine.lessons.lesson-07.discount.normal-minimum-amount=200"
            )

    def "Spring test properties drive which rule wins and what percent is returned"() {
        expect:
        contextRunner.run { context ->
            Lesson07DiscountService discountService = serviceFromContext(context)
            Lesson07DiscountRequest request = request("VIP", "600")

            def result = discountService.calculate(request)

            assert result.valid
            assert result.firedRules == ["VIP discount"]
            assert result.discountPercent == 13
            assert result.reason == "VIP customer"
        }
    }

    def "Spring-loaded large order config applies above its configured threshold"() {
        expect:
        contextRunner.run { context ->
            Lesson07DiscountService discountService = serviceFromContext(context)
            Lesson07DiscountRequest request = request("VIP", "800")

            def result = discountService.calculate(request)

            assert result.valid
            assert result.firedRules == ["Large order discount"]
            assert result.discountPercent == 22
            assert result.reason == "Large order"
        }
    }

    private static Lesson07DiscountService serviceFromContext(context) {
        new Lesson07DiscountService(context.getBean(Lesson07DiscountConfig))
    }

    private static Lesson07DiscountRequest request(String customerType, String orderAmount) {
        Lesson07DiscountRequest request = new Lesson07DiscountRequest()
        request.customerType = customerType
        request.orderAmount = orderAmount == null ? null : new BigDecimal(orderAmount)
        request
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(Lesson07DiscountProperties)
    @Import(Lesson07DiscountConfiguration)
    static class TestConfig {
    }
}
