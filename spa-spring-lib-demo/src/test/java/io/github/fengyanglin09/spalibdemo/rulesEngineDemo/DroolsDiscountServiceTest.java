package io.github.fengyanglin09.spalibdemo.rulesEngineDemo;

import io.github.fengyanglin09.spalibdemo.rulesEngineDemo.config.CustomerType;
import io.github.fengyanglin09.spalibdemo.rulesEngineDemo.config.DiscountType;
import io.github.fengyanglin09.spalibdemo.rulesEngineDemo.model.DiscountRequest;
import io.github.fengyanglin09.spalibdemo.rulesEngineDemo.model.DiscountResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class DroolsDiscountServiceTest {

    private static DroolsDiscountService droolsDiscountService;

    @BeforeAll
    static void setUp() {
        KieContainer kieContainer = KieServices.Factory.get().getKieClasspathContainer();
        droolsDiscountService = new DroolsDiscountService(kieContainer);
    }

    @Test
    void autoVipLargeOrderUsesLargeOrderDiscount() {
        DiscountResult result = calculate(CustomerType.VIP, DiscountType.AUTO, "600");

        assertThat(result.isValid()).isTrue();
        assertThat(result.getDiscountPercent()).isEqualTo(20);
        assertThat(result.getReason()).isEqualTo("Large order over 500");
    }

    @Test
    void vipOnlyUsesVipDiscount() {
        DiscountResult result = calculate(CustomerType.VIP, DiscountType.VIP_ONLY, "600");

        assertThat(result.isValid()).isTrue();
        assertThat(result.getDiscountPercent()).isEqualTo(15);
        assertThat(result.getReason()).isEqualTo("VIP customer with order over 100");
    }

    @Test
    void autoNormalCustomerGetsNormalDiscount() {
        DiscountResult result = calculate(CustomerType.NORMAL, DiscountType.AUTO, "250");

        assertThat(result.isValid()).isTrue();
        assertThat(result.getDiscountPercent()).isEqualTo(8);
        assertThat(result.getReason()).isEqualTo("Normal customer with order over 200");
    }

    @Test
    void autoNormalSmallOrderReturnsFallbackReason() {
        DiscountResult result = calculate(CustomerType.NORMAL, DiscountType.AUTO, "50");

        assertThat(result.isValid()).isTrue();
        assertThat(result.getDiscountPercent()).isZero();
        assertThat(result.getReason()).isEqualTo("No discount rule matched");
    }

    @Test
    void missingCustomerTypeIsInvalid() {
        DiscountResult result = calculate(null, DiscountType.AUTO, "600");

        assertThat(result.isValid()).isFalse();
        assertThat(result.getDiscountPercent()).isZero();
        assertThat(result.getReason()).isEqualTo("Customer type is required");
    }

    @Test
    void missingDiscountTypeIsInvalid() {
        DiscountResult result = calculate(CustomerType.VIP, null, "600");

        assertThat(result.isValid()).isFalse();
        assertThat(result.getDiscountPercent()).isZero();
        assertThat(result.getReason()).isEqualTo("Unsupported discount type");
    }

    private DiscountResult calculate(CustomerType customerType, DiscountType discountType, String orderAmount) {
        DiscountRequest request = new DiscountRequest();
        request.setCustomerType(customerType);
        request.setDiscountType(discountType);
        request.setOrderAmount(new BigDecimal(orderAmount));

        return droolsDiscountService.calculateDiscount(request);
    }
}
