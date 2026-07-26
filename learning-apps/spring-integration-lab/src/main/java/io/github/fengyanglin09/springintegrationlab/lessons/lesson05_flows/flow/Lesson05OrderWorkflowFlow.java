package io.github.fengyanglin09.springintegrationlab.lessons.lesson05_flows.flow;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson05_flows.handler.Lesson05OrderWorkflowSteps;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson05_flows.support.Lesson05Channels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;

// @Configuration tells Spring:
// "Read this class during startup because it contains bean definitions."
//
// This class is the lesson's wiring diagram. It says which channel starts the
// flow and which endpoint method each message visits.
@Configuration
public class Lesson05OrderWorkflowFlow {

    // @Bean tells Spring:
    // "Create this IntegrationFlow during startup."
    //
    // IntegrationFlow is the Java builder used by Spring Integration to define
    // a message path. The builder is sometimes called the Java DSL.
    //
    // DSL means "domain-specific language". Here, it just means:
    // "Java methods shaped around Spring Integration words, such as from,
    // transform, channel, and handle."
    @Bean
    IntegrationFlow lesson05OrderWorkflowIntegrationFlow(Lesson05OrderWorkflowSteps workflowSteps) {
        // IntegrationFlow.from(...) means:
        // "Start this flow when code sends a message to the channel named
        // lesson05OrderRequests."
        //
        // That channel is created in Lesson05ChannelConfiguration.
        // The gateway sends to the same channel, so the gateway is the normal
        // entry point into this flow.
        return IntegrationFlow.from(Lesson05Channels.ORDER_REQUESTS)
                // transform(...) means:
                // "Call workflowSteps.normalize(...) with the current payload
                // and replace the payload with the return value."
                //
                // A handle(...) step can also return a value, so transform and
                // handle can look similar in code. The difference is intent:
                //
                // - Use transform(...) when the main idea is "convert this
                //   payload into a different payload shape."
                // - Use handle(...) when the main idea is "do application work
                //   for this message."
                //
                // normalize(...) is a transform because it changes a raw request
                // into a cleaner payload shape for the rest of the flow.
                //
                // Before this step: payload is Lesson05OrderRequest.
                // After this step: payload is Lesson05NormalizedOrder.
                .transform(workflowSteps, "normalize")
                // This channel(...) line is NOT required for this simple flow.
                //
                // We could write:
                //
                // .transform(workflowSteps, "normalize")
                // .transform(workflowSteps, "assignHandlingLane")
                // .handle(workflowSteps, "summarize")
                //
                // and Spring Integration would still connect the steps for us.
                //
                // channel(...) means:
                // "Send the current message to this named channel before the
                // next endpoint receives it."
                //
                // We keep this named middle channel only as a learning
                // checkpoint. It gives a name to the moment where the payload
                // has changed from Lesson05OrderRequest to Lesson05NormalizedOrder.
                //
                // In real code, name a middle channel when another flow needs
                // to connect here, when you want a different channel type, or
                // when the named checkpoint makes the flow easier to understand.
                .channel(Lesson05Channels.NORMALIZED_ORDERS)
                // This is another transformer endpoint.
                //
                // assignHandlingLane(...) returns a new payload type, just like
                // a handle(...) method could. We still use transform(...) here
                // because the main idea is reshaping the message from
                // "normalized order" into "prioritized order".
                //
                // Before this step: payload is Lesson05NormalizedOrder.
                // After this step: payload is Lesson05PrioritizedOrder.
                //
                // Notice that there is no .channel(...) after this step.
                // Spring Integration still wires this step to the next step for
                // us with an internal direct handoff. We only name channels when
                // the name helps us understand or connect the flow.
                .transform(workflowSteps, "assignHandlingLane")
                // handle(...) creates a service-activator endpoint.
                //
                // Service activator means:
                // "Call ordinary application code to do work for this message."
                //
                // handle(...) can also return a value. When it returns a
                // non-null value, that value becomes the reply or the next
                // payload.
                //
                // We use handle(...) here because summarize(...) represents the
                // final application action in this flow: build the response that
                // the caller asked for.
                //
                // summarize(...) returns Lesson05FlowResult, so the gateway
                // returns that same result to whoever called prepare(...).
                .handle(workflowSteps, "summarize")
                // get() finishes the builder chain and gives Spring the
                // IntegrationFlow object to register.
                .get();
    }
}
