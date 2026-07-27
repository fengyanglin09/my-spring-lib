package io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.handler;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.model.Lesson07CustomerOrder;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.model.Lesson07CustomerProfile;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.model.Lesson07OrderDraft;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.support.Lesson07CustomerProfileCatalog;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Payload-shaping methods used by lesson 07.
 */
// @Component tells Spring:
// "Create one Lesson07OrderShapeTransformer object during startup."
//
// The flow calls this object from typed lambda steps. We avoid string method
// names such as .transform(transformer, "parseRawLine") so the Java compiler
// can help us when method names or types change.
@Component
public class Lesson07OrderShapeTransformer {

    private final Lesson07CustomerProfileCatalog profileCatalog;

    public Lesson07OrderShapeTransformer(Lesson07CustomerProfileCatalog profileCatalog) {
        this.profileCatalog = profileCatalog;
    }

    public Lesson07OrderDraft parseRawLine(String rawOrderLine) {
        // This method turns an external text shape into a Java payload shape.
        //
        // Expected format:
        // orderId,customerId,orderAmount,sku
        //
        // Example:
        // order-7001,cust-vip,1200.00,sku-1
        String[] parts = rawOrderLine.split(",", -1);
        if (parts.length != 4) {
            throw new IllegalArgumentException("Expected 4 CSV columns: orderId,customerId,orderAmount,sku");
        }

        return new Lesson07OrderDraft(
                clean(parts[0]),
                clean(parts[1]).toLowerCase(Locale.ROOT),
                new BigDecimal(clean(parts[2])),
                clean(parts[3]).toUpperCase(Locale.ROOT),
                List.of("transform:raw-csv-to-order-draft")
        );
    }

    public Lesson07CustomerOrder addCustomerProfile(Lesson07OrderDraft draft) {
        // This is payload enrichment.
        //
        // We start with an order draft that has customerId but not customerTier
        // or region. We look up extra customer profile data and return a new
        // payload that contains both the original order data and the extra
        // customer data.
        Lesson07CustomerProfile profile = profileCatalog.findByCustomerId(draft.customerId());

        List<String> shapeTrail = new ArrayList<>(draft.shapeTrail());
        shapeTrail.add("transform:add-customer-profile-to-payload");

        return new Lesson07CustomerOrder(
                draft.orderId(),
                draft.customerId(),
                profile.customerTier(),
                profile.region(),
                draft.orderAmount(),
                draft.sku(),
                List.copyOf(shapeTrail)
        );
    }

    public String valueBand(Lesson07CustomerOrder order) {
        // This method is used by the header enricher.
        //
        // It does not change the payload. It computes metadata about the
        // payload so the flow can store that metadata in a message header.
        return order.orderAmount().compareTo(new BigDecimal("1000.00")) >= 0
                ? "HIGH_VALUE"
                : "STANDARD_VALUE";
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
