package io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.handler;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.model.Lesson07CustomerOrder;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.model.Lesson07ShapeReport;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.support.Lesson07Headers;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds the final lesson 07 report.
 */
// @Component tells Spring:
// "Create one Lesson07ShapeReporter object during startup."
@Component
public class Lesson07ShapeReporter {

    public Lesson07ShapeReport buildReport(Lesson07CustomerOrder order, MessageHeaders headers) {
        // This method receives both the final payload and the message headers.
        //
        // Payload contains business data:
        // order id, customer tier, region, amount, and SKU.
        //
        // Headers contain metadata:
        // source system, lesson name, value band, and shape stage.
        List<String> shapeTrail = new ArrayList<>(order.shapeTrail());
        shapeTrail.add("header-enricher:add-message-metadata");
        shapeTrail.add("handle:build-shape-report");

        return new Lesson07ShapeReport(
                order.orderId(),
                headerAsString(headers, Lesson07Headers.SOURCE_SYSTEM),
                order.customerTier(),
                order.region(),
                order.orderAmount(),
                headerAsString(headers, Lesson07Headers.VALUE_BAND),
                headerAsString(headers, Lesson07Headers.LESSON_NAME),
                List.copyOf(shapeTrail)
        );
    }

    private String headerAsString(MessageHeaders headers, String headerName) {
        Object value = headers.get(headerName);
        return value == null ? null : value.toString();
    }
}
