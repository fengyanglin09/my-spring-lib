package io.github.fengyanglin09.springintegrationlab.lessons.lesson04_endpoints.handler;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson04_endpoints.model.Lesson04NormalizedOrder;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson04_endpoints.model.Lesson04OrderRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

/**
 * Transformer endpoint behavior.
 *
 * <p>A transformer endpoint receives one payload shape and returns a different
 * payload shape. Here it turns a raw order request into a normalized order.</p>
 */
// @Component tells Spring:
// "Create one Lesson04OrderNormalizer object during startup so other Spring
// beans, such as Lesson04EndpointFlow, can receive it as a dependency."
@Component
public class Lesson04OrderNormalizer {

    public Lesson04NormalizedOrder normalize(Lesson04OrderRequest request) {
        // This method is ordinary Java.
        //
        // It becomes transformer endpoint behavior because Lesson04EndpointFlow
        // contains this step:
        //
        // .transform(normalizer, "normalize")
        //
        // That step means:
        // "When a message reaches the transformer, pass the current payload to
        // normalizer.normalize(...), then continue with the returned object as
        // the new payload."
        String normalizedCustomerType = request.customerType() == null
                ? "UNKNOWN"
                : request.customerType().trim().toUpperCase(Locale.ROOT);

        return new Lesson04NormalizedOrder(
                request.orderId(),
                normalizedCustomerType,
                request.orderAmount(),
                List.of("transformer:normalize")
        );
    }
}
