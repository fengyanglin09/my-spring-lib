package io.github.fengyanglin09.springintegrationlab.lessons.lesson08_routing.handler;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson08_routing.model.Lesson08OrderRequest;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson08_routing.model.Lesson08RoutableOrder;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson08_routing.model.Lesson08RouteKey;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson08_routing.model.Lesson08RoutingResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Small decision methods used by the lesson 08 flow.
 */
// @Component tells Spring:
// "Create one Lesson08RoutingRules object during startup."
//
// The flow calls these methods from typed lambdas instead of string method
// names. That lets Java check the payload types for us.
@Component
public class Lesson08RoutingRules {

    private static final BigDecimal REVIEW_THRESHOLD = new BigDecimal("1000.00");

    public boolean acceptable(Lesson08OrderRequest request) {
        // This method is used by the filter step.
        //
        // A filter method returns boolean:
        //
        // true  = this message may continue
        // false = this message is rejected by the filter
        //
        // The filter is only deciding "continue or reject". It is not choosing
        // between EXPEDITED, REVIEW, and STANDARD. That later choice belongs to
        // the router.
        return request.customerVerified()
                && request.orderAmount() != null
                && request.orderAmount().compareTo(BigDecimal.ZERO) > 0;
    }

    public Lesson08RoutingResult reject(Lesson08OrderRequest request) {
        // This method is used by the filter discard flow.
        //
        // Rejected messages do not continue to the router. Instead, the filter
        // sends them to the discard flow, and this method builds the reply that
        // tells the caller why the normal routing path was skipped.
        return new Lesson08RoutingResult(
                request.orderId(),
                false,
                "REJECTED",
                request.orderAmount(),
                List.of("filter:rejected-order")
        );
    }

    public Lesson08RoutableOrder classify(Lesson08OrderRequest request) {
        // This method prepares the payload for the router.
        //
        // It chooses a route key and puts that key in the payload. The router
        // will read the route key and choose the matching branch.
        Lesson08RouteKey routeKey;
        if (request.expedited()) {
            routeKey = Lesson08RouteKey.EXPEDITED;
        } else if (request.orderAmount().compareTo(REVIEW_THRESHOLD) >= 0) {
            routeKey = Lesson08RouteKey.REVIEW;
        } else {
            routeKey = Lesson08RouteKey.STANDARD;
        }

        return new Lesson08RoutableOrder(
                request.orderId(),
                normalizeCustomerType(request.customerType()),
                request.orderAmount(),
                routeKey,
                List.of("filter:accepted-order", "transform:classify-route-key")
        );
    }

    public Lesson08RoutingResult expedited(Lesson08RoutableOrder order) {
        return acceptedResult(order, "EXPEDITED_PATH", "router:expedited-branch");
    }

    public Lesson08RoutingResult review(Lesson08RoutableOrder order) {
        return acceptedResult(order, "REVIEW_PATH", "router:review-branch");
    }

    public Lesson08RoutingResult standard(Lesson08RoutableOrder order) {
        return acceptedResult(order, "STANDARD_PATH", "router:standard-branch");
    }

    private Lesson08RoutingResult acceptedResult(
            Lesson08RoutableOrder order,
            String path,
            String branchStep
    ) {
        List<String> routingTrail = new ArrayList<>(order.routingTrail());
        routingTrail.add(branchStep);

        return new Lesson08RoutingResult(
                order.orderId(),
                true,
                path,
                order.orderAmount(),
                List.copyOf(routingTrail)
        );
    }

    private String normalizeCustomerType(String customerType) {
        if (customerType == null || customerType.isBlank()) {
            return "UNKNOWN";
        }
        return customerType.trim().toUpperCase(Locale.ROOT);
    }
}
