package io.github.fengyanglin09.springintegrationlab.lessons.lesson04_endpoints.handler;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson04_endpoints.model.Lesson04EndpointReport;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson04_endpoints.model.Lesson04NormalizedOrder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Service-activator endpoint behavior.
 *
 * <p>A service activator calls application code to perform work. In this lesson,
 * the work is simple: produce a report from the normalized order.</p>
 */
// @Component tells Spring:
// "Create one Lesson04OrderReporter object during startup so the flow can call
// it when a normalized order message reaches the service-activator step."
@Component
public class Lesson04OrderReporter {

    public Lesson04EndpointReport report(Lesson04NormalizedOrder order) {
        // This method is ordinary Java.
        //
        // It becomes service-activator endpoint behavior because
        // Lesson04EndpointFlow contains this step:
        //
        // .handle(reporter, "report")
        //
        // By the time the message reaches this step, the payload is already
        // Lesson04NormalizedOrder because the transformer returned that type.
        List<String> endpointTrail = new ArrayList<>(order.endpointTrail());
        endpointTrail.add("service-activator:report");

        return new Lesson04EndpointReport(
                order.orderId(),
                order.customerType(),
                order.orderAmount(),
                List.copyOf(endpointTrail)
        );
    }
}
