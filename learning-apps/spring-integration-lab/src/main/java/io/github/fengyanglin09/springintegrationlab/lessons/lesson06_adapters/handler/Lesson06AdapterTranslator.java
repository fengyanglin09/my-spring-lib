package io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters.handler;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters.model.Lesson06ExternalOrderRecord;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters.model.Lesson06InternalOrder;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson06_adapters.model.Lesson06PartnerOrderRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Mapping code used around the adapter boundaries.
 */
// @Component tells Spring:
// "Create one Lesson06AdapterTranslator object during startup so the flow can
// call its methods."
@Component
public class Lesson06AdapterTranslator {

    public Lesson06InternalOrder toInternalOrder(Lesson06ExternalOrderRecord record) {
        // This transform is close to the inbound adapter boundary.
        //
        // The external system gives us strings and short codes. The rest of our
        // application should not need to understand those external details, so
        // we convert them into our internal order shape here.
        return new Lesson06InternalOrder(
                record.externalOrderId(),
                normalizeCustomerCode(record.customerCode()),
                parseAmount(record.amountText()),
                normalizeShippingCode(record.shippingCode()),
                List.of("inbound-adapter:poll-external-inbox", "transform:external-to-internal")
        );
    }

    public Lesson06PartnerOrderRequest toPartnerOrderRequest(Lesson06InternalOrder order) {
        // This transform is close to the outbound adapter boundary.
        //
        // The partner system has its own words for our internal data. We convert
        // to that partner-facing shape before the outbound adapter sends it.
        List<String> adapterTrail = new ArrayList<>(order.adapterTrail());
        adapterTrail.add("transform:internal-to-partner-request");

        return new Lesson06PartnerOrderRequest(
                order.orderId(),
                order.customerType(),
                order.orderAmount(),
                order.shippingPriority(),
                List.copyOf(adapterTrail)
        );
    }

    private String normalizeCustomerCode(String customerCode) {
        if (customerCode == null || customerCode.isBlank()) {
            return "UNKNOWN";
        }
        return customerCode.trim().toUpperCase(Locale.ROOT);
    }

    private BigDecimal parseAmount(String amountText) {
        if (amountText == null || amountText.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(amountText.trim());
    }

    private String normalizeShippingCode(String shippingCode) {
        if ("EXP".equalsIgnoreCase(shippingCode)) {
            return "EXPEDITED";
        }
        return "STANDARD";
    }
}
