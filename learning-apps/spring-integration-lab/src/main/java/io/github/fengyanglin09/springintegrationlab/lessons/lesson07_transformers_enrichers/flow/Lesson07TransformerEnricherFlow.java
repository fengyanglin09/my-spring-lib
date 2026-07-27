package io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.flow;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.handler.Lesson07OrderShapeTransformer;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.handler.Lesson07ShapeReporter;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.model.Lesson07CustomerOrder;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.model.Lesson07OrderDraft;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.support.Lesson07Channels;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson07_transformers_enrichers.support.Lesson07Headers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.Message;

// @Configuration tells Spring:
// "Read this class during startup because it contains bean definitions."
@Configuration
public class Lesson07TransformerEnricherFlow {

    // @Bean tells Spring:
    // "Create this IntegrationFlow during startup."
    @Bean
    IntegrationFlow lesson07TransformerEnricherIntegrationFlow(
            Lesson07OrderShapeTransformer transformer,
            Lesson07ShapeReporter reporter
    ) {
        // This flow reads as:
        //
        // raw CSV string
        // -> transform payload into an order draft
        // -> transform/enrich payload with customer profile data
        // -> enrich headers with metadata
        // -> handle final payload and headers to build the reply
        return IntegrationFlow.from(Lesson07Channels.RAW_ORDER_LINES)
                // Typed lambda transform:
                //
                // String.class tells Spring Integration:
                // "At this step, expect the payload to be a String."
                //
                // rawLine -> transformer.parseRawLine(rawLine) is normal Java.
                // There is no string method name such as "parseRawLine" for
                // Spring to find by reflection.
                //
                // Before this step: payload is String.
                // After this step: payload is Lesson07OrderDraft.
                .transform(String.class, rawLine -> transformer.parseRawLine(rawLine))
                // This is another typed lambda transform.
                //
                // It enriches the payload by returning a new object that contains
                // the original order data plus customer profile data.
                //
                // Important vocabulary:
                // This is payload enrichment because the payload gains more
                // business data. We still use transform(...) because the payload
                // shape changes from Lesson07OrderDraft to Lesson07CustomerOrder.
                //
                // Before this step: payload is Lesson07OrderDraft.
                // After this step: payload is Lesson07CustomerOrder.
                .transform(Lesson07OrderDraft.class, draft -> transformer.addCustomerProfile(draft))
                // enrichHeaders(...) creates a header enricher endpoint.
                //
                // Header enricher means:
                // "Add or change message headers without changing the payload."
                //
                // Headers are metadata about the message. In this lesson, we add
                // the lesson name, a value band, and the current shape stage.
                //
                // Each header has:
                //
                // - a header name, such as Lesson07Headers.LESSON_NAME
                // - a header value, such as "lesson07-transformers-enrichers"
                //
                // .header(name, value) adds a fixed value.
                //
                // .headerFunction(name, function) computes the value from the
                // current message. Use this when the header value depends on
                // the payload or existing headers.
                //
                // The payload stays Lesson07CustomerOrder after this step.
                .enrichHeaders(headers -> headers
                        // Add this header:
                        //
                        // name  = "lesson07LessonName"
                        // value = "lesson07-transformers-enrichers"
                        .header(Lesson07Headers.LESSON_NAME, "lesson07-transformers-enrichers")
                        // Add this header:
                        //
                        // name  = "lesson07ValueBand"
                        // value = result of transformer.valueBand(...)
                        //
                        // Spring passes the current Message into this function.
                        // We call message.getPayload() to read the current
                        // Lesson07CustomerOrder payload and compute metadata
                        // from its order amount.
                        .headerFunction(
                                Lesson07Headers.VALUE_BAND,
                                (Message<Lesson07CustomerOrder> message) ->
                                        transformer.valueBand(message.getPayload())
                        )
                        // Add this header:
                        //
                        // name  = "lesson07ShapeStage"
                        // value = "headers-enriched"
                        .header(Lesson07Headers.SHAPE_STAGE, "headers-enriched"))
                // handle(...) creates a service-activator endpoint.
                //
                // We use the typed lambda form here too.
                //
                // order is the current payload.
                // headers are the current message headers after enrichment.
                //
                // reporter.buildReport(...) returns Lesson07ShapeReport, so the
                // gateway caller receives Lesson07ShapeReport.
                .handle(Lesson07CustomerOrder.class, (order, headers) ->
                        reporter.buildReport(order, headers))
                .get();
    }
}
