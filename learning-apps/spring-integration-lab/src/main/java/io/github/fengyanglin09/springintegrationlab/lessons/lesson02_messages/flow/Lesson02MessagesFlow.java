package io.github.fengyanglin09.springintegrationlab.lessons.lesson02_messages.flow;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson02_messages.handler.Lesson02MessageInspector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;

// @Configuration tells Spring:
// "This class contains methods that create beans for the application context."
@Configuration
public class Lesson02MessagesFlow {

    // This is the in-memory channel name shared by the gateway and the flow.
    //
    // Because this lesson does not define a channel bean manually, Spring
    // Integration creates a default DirectChannel with this name at startup.
    public static final String INPUT_CHANNEL = "lesson02Messages";

    // @Bean registers this IntegrationFlow with Spring Integration.
    //
    // Lesson 01 mostly hid the message wrapper from us. Lesson 02 intentionally
    // passes the whole Message object to the handler so the handler can inspect
    // both payload and headers.
    @Bean
    IntegrationFlow lesson02MessageFlow(Lesson02MessageInspector inspector) {
        return IntegrationFlow.from(INPUT_CHANNEL)
                // handle(inspector, "inspect") means:
                // "Call inspector.inspect(...) when a message reaches this flow."
                //
                // Because the inspect method accepts Message<Lesson02OrderRequest>,
                // Spring Integration passes the full Message, not only the payload.
                .handle(inspector, "inspect")
                .get();
    }
}
