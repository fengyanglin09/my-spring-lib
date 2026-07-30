package io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows.flow;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows.handler.Lesson13OrderReviewRules;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows.model.Lesson13OrderDraft;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows.model.Lesson13ReviewDecision;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows.model.Lesson13ReviewedOrder;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows.support.Lesson13Channels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;

// @Configuration tells Spring:
// "Read this class during startup because it contains bean definitions."
@Configuration
public class Lesson13TestingFlow {

    // @Bean tells Spring:
    // "Create this IntegrationFlow during startup."
    @Bean
    IntegrationFlow lesson13OrderReviewIntegrationFlow(Lesson13OrderReviewRules rules) {
        // This flow intentionally reuses flow concepts from earlier lessons:
        // channel input, transformer, router, and handler branches.
        //
        // Lesson 13 is not trying to teach a brand-new endpoint type. The new
        // lesson is in the test code:
        //
        // - Lesson13OrderReviewRulesSpec tests the handler as plain Java.
        // - Lesson13TestingFlowsSpec tests this whole flow through the gateway.
        // This flow reads as:
        //
        // raw order draft
        // -> normalize and choose a review decision
        // -> route to the branch for that decision
        // -> return a Lesson13OrderReviewResult
        return IntegrationFlow.from(Lesson13Channels.ORDER_REVIEW_REQUESTS)
                // transform(...) creates a transformer endpoint.
                //
                // Transformer means:
                // "Take the current payload and replace it with another
                // payload."
                //
                // The typed lambda form says:
                //
                // - expect the payload to be Lesson13OrderDraft
                // - call rules.normalize(draft)
                // - use the returned Lesson13ReviewedOrder as the next payload
                .transform(Lesson13OrderDraft.class, draft -> rules.normalize(draft))
                // route(...) creates a router endpoint.
                //
                // Router means:
                // "Look at the message and choose one path."
                //
                // This is similar to a Java switch on order.decision(), but the
                // choice is visible as part of the IntegrationFlow.
                //
                // Why use route(...) instead of hiding the choice inside an
                // if/else or switch statement?
                //
                // - Use if/else or switch when the branch is just ordinary Java
                //   logic inside one method.
                // - Use route(...) when the branch is a message path that may
                //   grow into its own flow: extra transforms, handlers,
                //   channels, queues, adapters, or error behavior.
                //
                // In this lesson, each branch is small. We still use route(...)
                // because the lesson is about testing flow wiring:
                //
                // gateway -> channel -> transform -> router -> selected branch
                //
                // The typed lambda form says:
                //
                // - expect the payload to be Lesson13ReviewedOrder
                // - call order.decision()
                // - use the returned Lesson13ReviewDecision enum as the route key
                .route(
                        Lesson13ReviewedOrder.class,
                        order -> order.decision(),
                        routes -> routes
                                // subFlowMapping(...) means:
                                // "When the route key equals this value, send
                                // the message through this inline mini-flow."
                                //
                                // Each branch receives the same
                                // Lesson13ReviewedOrder payload that reached
                                // the router.
                                //
                                // The branch could contain more than one step.
                                // For example, APPROVED could later transform
                                // the order, send an audit event, and then
                                // return a result. That is the main benefit of
                                // keeping the branch as an integration subflow.
                                .subFlowMapping(
                                        Lesson13ReviewDecision.APPROVED,
                                        approved -> approved.handle(
                                                Lesson13ReviewedOrder.class,
                                                (order, headers) -> rules.approve(order)
                                        )
                                )
                                .subFlowMapping(
                                        Lesson13ReviewDecision.MANUAL_REVIEW,
                                        manualReview -> manualReview.handle(
                                                Lesson13ReviewedOrder.class,
                                                (order, headers) -> rules.requestManualReview(order)
                                        )
                                )
                                .subFlowMapping(
                                        Lesson13ReviewDecision.REJECTED,
                                        rejected -> rejected.handle(
                                                Lesson13ReviewedOrder.class,
                                                (order, headers) -> rules.reject(order)
                                        )
                                )
                )
                .get();
    }
}
