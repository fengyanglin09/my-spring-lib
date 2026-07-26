package io.github.fengyanglin09.springintegrationlab.lessons.lesson05_flows.handler;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson05_flows.model.Lesson05FlowResult;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson05_flows.model.Lesson05NormalizedOrder;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson05_flows.model.Lesson05OrderRequest;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson05_flows.model.Lesson05PrioritizedOrder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Small methods used by the lesson 05 flow.
 */
// @Component tells Spring:
// "Create one Lesson05OrderWorkflowSteps object during startup."
//
// The flow receives this object as a dependency and points individual flow
// steps at individual methods on this object.
@Component
public class Lesson05OrderWorkflowSteps {

    private static final BigDecimal REVIEW_THRESHOLD = new BigDecimal("1000.00");

    public Lesson05NormalizedOrder normalize(Lesson05OrderRequest request) {
        // This method is ordinary Java.
        //
        // In the flow, this method is used by:
        //
        // .transform(workflowSteps, "normalize")
        //
        // That flow step means:
        // "Take the current payload, pass it to normalize(...), and continue
        // with the object returned by normalize(...) as the new payload."
        String customerType = request.customerType() == null
                ? "UNKNOWN"
                : request.customerType().trim().toUpperCase(Locale.ROOT);

        BigDecimal orderAmount = request.orderAmount() == null
                ? BigDecimal.ZERO
                : request.orderAmount();

        return new Lesson05NormalizedOrder(
                request.orderId(),
                customerType,
                orderAmount,
                request.expedited(),
                List.of("1 normalize raw order")
        );
    }

    public Lesson05PrioritizedOrder assignHandlingLane(Lesson05NormalizedOrder order) {
        // This method contains the business rule for choosing the lane.
        //
        // The flow should show that this step happens. The flow should not hide
        // the details of the business rule inside a hard-to-read lambda.
        String handlingLane;
        if (order.expedited()) {
            handlingLane = "EXPEDITED";
        } else if (order.orderAmount().compareTo(REVIEW_THRESHOLD) >= 0) {
            handlingLane = "REVIEW";
        } else {
            handlingLane = "STANDARD";
        }

        List<String> flowSteps = new ArrayList<>(order.flowSteps());
        flowSteps.add("2 assign handling lane");

        return new Lesson05PrioritizedOrder(
                order.orderId(),
                order.customerType(),
                order.orderAmount(),
                handlingLane,
                List.copyOf(flowSteps)
        );
    }

    public Lesson05FlowResult summarize(Lesson05PrioritizedOrder order) {
        // This is the final request-reply step.
        //
        // Because this method returns Lesson05FlowResult, the gateway caller
        // receives a Lesson05FlowResult from Lesson05FlowGateway.prepare(...).
        List<String> flowSteps = new ArrayList<>(order.flowSteps());
        flowSteps.add("3 build final result");

        return new Lesson05FlowResult(
                order.orderId(),
                order.customerType(),
                order.orderAmount(),
                order.handlingLane(),
                List.copyOf(flowSteps)
        );
    }
}
