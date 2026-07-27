package io.github.fengyanglin09.springintegrationlab.lessons.lesson08_routing.flow;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson08_routing.handler.Lesson08RoutingRules;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson08_routing.model.Lesson08OrderRequest;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson08_routing.model.Lesson08RoutableOrder;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson08_routing.model.Lesson08RouteKey;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson08_routing.support.Lesson08Channels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;

// @Configuration tells Spring:
// "Read this class during startup because it contains bean definitions."
@Configuration
public class Lesson08RoutingFlow {

    // @Bean tells Spring:
    // "Create this IntegrationFlow during startup."
    @Bean
    IntegrationFlow lesson08RoutingIntegrationFlow(Lesson08RoutingRules routingRules) {
        // This flow reads as:
        //
        // receive raw order request
        // -> filter unacceptable orders
        // -> classify accepted orders with a route key
        // -> route accepted orders to the matching branch
        return IntegrationFlow.from(Lesson08Channels.ORDER_REQUESTS)
                // filter(...) creates a filter endpoint.
                //
                // Filter means:
                // "Decide whether this message should continue."
                //
                // The typed lambda form says:
                //
                // - expect the payload to be Lesson08OrderRequest
                // - call routingRules.acceptable(request)
                // - continue only if the result is true
                .filter(
                        Lesson08OrderRequest.class,
                        request -> routingRules.acceptable(request),
                        filter -> filter
                                // throwExceptionOnRejection(false) means:
                                // "If the filter returns false, do not throw an
                                // exception to the gateway caller."
                                //
                                // Instead, the rejected message will go to the
                                // discardFlow below.
                                .throwExceptionOnRejection(false)
                                // discardFlow(...) means:
                                // "If the filter rejects the message, send the
                                // rejected message through this small alternate
                                // flow."
                                //
                                // This prevents rejected messages from seeming
                                // to vanish. The caller receives a normal
                                // Lesson08RoutingResult with path = REJECTED.
                                .discardFlow(discarded -> discarded
                                        .handle(
                                                Lesson08OrderRequest.class,
                                                (request, headers) -> routingRules.reject(request)
                                        ))
                )
                // transform(...) changes the payload shape.
                //
                // Before this step: payload is Lesson08OrderRequest.
                // After this step: payload is Lesson08RoutableOrder.
                //
                // The new payload contains a routeKey. The router will use that
                // routeKey to pick a branch.
                .transform(Lesson08OrderRequest.class, request -> routingRules.classify(request))
                // route(...) creates a router endpoint.
                //
                // Router means:
                // "Look at the message and choose which path it should take."
                //
                // The typed lambda form says:
                //
                // - expect the payload to be Lesson08RoutableOrder
                // - call order.routeKey()
                // - use the returned Lesson08RouteKey to choose a branch
                .route(
                        Lesson08RoutableOrder.class,
                        order -> order.routeKey(),
                        routes -> routes
                                // subFlowMapping(key, subflow) means:
                                // "When the route key equals this key, send the
                                // message through this inline subflow."
                                //
                                // The subflow receives the same payload that
                                // reached the router: Lesson08RoutableOrder.
                                .subFlowMapping(
                                        Lesson08RouteKey.EXPEDITED,
                                        expedited -> expedited.handle(
                                                Lesson08RoutableOrder.class,
                                                (order, headers) -> routingRules.expedited(order)
                                        )
                                )
                                .subFlowMapping(
                                        Lesson08RouteKey.REVIEW,
                                        review -> review.handle(
                                                Lesson08RoutableOrder.class,
                                                (order, headers) -> routingRules.review(order)
                                        )
                                )
                                .subFlowMapping(
                                        Lesson08RouteKey.STANDARD,
                                        standard -> standard.handle(
                                                Lesson08RoutableOrder.class,
                                                (order, headers) -> routingRules.standard(order)
                                        )
                                )
                )
                .get();
    }
}
