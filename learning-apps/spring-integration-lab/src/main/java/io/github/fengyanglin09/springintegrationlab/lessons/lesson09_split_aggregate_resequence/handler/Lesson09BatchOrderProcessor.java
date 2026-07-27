package io.github.fengyanglin09.springintegrationlab.lessons.lesson09_split_aggregate_resequence.handler;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson09_split_aggregate_resequence.model.Lesson09BatchOrderRequest;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson09_split_aggregate_resequence.model.Lesson09LineItemRequest;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson09_split_aggregate_resequence.model.Lesson09LineItemWork;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson09_split_aggregate_resequence.model.Lesson09OrderSummary;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson09_split_aggregate_resequence.model.Lesson09PricedLineItem;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.integration.store.MessageGroup;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Lesson-specific Java methods called by the split, transform, and aggregate steps.
 */
// @Component tells Spring:
// "Create one Lesson09BatchOrderProcessor object during startup."
//
// The flow calls this object from typed lambda steps. That keeps the flow easy
// to read and keeps the business object changes easy to test.
@Component
public class Lesson09BatchOrderProcessor {

    public List<Lesson09LineItemWork> splitBatch(Lesson09BatchOrderRequest batch) {
        // This method is used by the splitter.
        //
        // The incoming payload is one Lesson09BatchOrderRequest:
        //
        // order-9001
        //   line 1
        //   line 2
        //   line 3
        //
        // The returned List becomes several messages:
        //
        // message payload = Lesson09LineItemWork for line 1
        // message payload = Lesson09LineItemWork for line 2
        // message payload = Lesson09LineItemWork for line 3
        //
        // Spring Integration also adds sequence headers to those new messages.
        // You can think of those headers as sticky notes:
        //
        // - CORRELATION_ID: "these line-item messages came from the same batch"
        // - SEQUENCE_NUMBER: "this is item 1, item 2, item 3, ..."
        // - SEQUENCE_SIZE: "there are 3 total items in this batch"
        //
        // The resequencer and aggregator use those headers later.
        if (batch.lineItems().isEmpty()) {
            throw new IllegalArgumentException("Lesson 09 needs at least one line item to split");
        }

        return batch.lineItems()
                .stream()
                .map(lineItem -> toLineItemWork(batch.orderId(), lineItem))
                .toList();
    }

    public Lesson09PricedLineItem priceLineItem(Lesson09LineItemWork lineItem) {
        // This method is used by the transformer after the executor channel.
        //
        // Each line-item message can be priced independently. In a real system,
        // this could call a pricing service or read a database. Here we only
        // multiply quantity by unit price so the lesson stays focused on the
        // messaging pattern.
        //
        // The small pause below is not business logic. It makes lower line
        // numbers take slightly longer, so messages are more likely to finish
        // out of order when the executor channel runs them on multiple threads.
        pauseToMakeParallelOrderingVisible(lineItem);

        BigDecimal lineTotal = lineItem.unitPrice().multiply(BigDecimal.valueOf(lineItem.quantity()));

        List<String> lessonTrail = new ArrayList<>(lineItem.lessonTrail());
        lessonTrail.add("transform:price-one-line-item");

        return new Lesson09PricedLineItem(
                lineItem.orderId(),
                lineItem.lineNumber(),
                lineItem.sku(),
                lineItem.quantity(),
                lineItem.unitPrice(),
                lineTotal,
                List.copyOf(lessonTrail)
        );
    }

    public Lesson09OrderSummary summarize(MessageGroup group) {
        // This method is used by the aggregator.
        //
        // Aggregator means:
        // "Wait until all related messages are present, then combine them into
        // one result."
        //
        // The group object contains the messages that share the same
        // CORRELATION_ID. For this lesson, that group is the set of priced
        // line-item messages that came from one original batch order.
        //
        // We sort by Spring Integration's SEQUENCE_NUMBER header when building
        // the summary. That makes the final Java List show the original line
        // order clearly, even if the MessageGroup's Collection implementation
        // does not promise iteration order.
        List<Message<?>> orderedMessages = group.getMessages()
                .stream()
                .sorted(Comparator.comparingInt(this::sequenceNumber))
                .toList();

        List<Lesson09PricedLineItem> pricedItems = orderedMessages
                .stream()
                .map(message -> (Lesson09PricedLineItem) message.getPayload())
                .toList();

        BigDecimal orderTotal = pricedItems
                .stream()
                .map(Lesson09PricedLineItem::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new Lesson09OrderSummary(
                pricedItems.get(0).orderId(),
                group.getGroupId().toString(),
                pricedItems.size(),
                orderTotal,
                pricedItems.stream().map(Lesson09PricedLineItem::lineNumber).toList(),
                pricedItems.stream().map(Lesson09PricedLineItem::sku).toList(),
                orderedMessages.stream().map(this::sequenceNumber).toList(),
                List.of(
                        "split:batch-message-to-line-item-messages",
                        "channel:executor-can-process-line-items-in-parallel",
                        "transform:price-each-line-item",
                        "resequence:release-line-items-in-original-sequence",
                        "aggregate:line-items-to-order-summary"
                )
        );
    }

    private Lesson09LineItemWork toLineItemWork(String orderId, Lesson09LineItemRequest lineItem) {
        return new Lesson09LineItemWork(
                orderId,
                lineItem.lineNumber(),
                lineItem.sku(),
                lineItem.quantity(),
                lineItem.unitPrice(),
                List.of("split:batch-message-to-line-item-message")
        );
    }

    private int sequenceNumber(Message<?> message) {
        // This reads the SEQUENCE_NUMBER header that the splitter added.
        //
        // Header values are stored as Objects, so we check that the value is a
        // Number before converting it to an int.
        Object value = message.getHeaders().get(IntegrationMessageHeaderAccessor.SEQUENCE_NUMBER);
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.MAX_VALUE;
    }

    private void pauseToMakeParallelOrderingVisible(Lesson09LineItemWork lineItem) {
        // This method is only here to make the lesson easier to see.
        //
        // The executor channel lets line items run on different worker threads,
        // but very small calculations can finish so quickly that they still
        // appear to finish in order. This pause makes earlier line numbers wait
        // a little longer, so later line numbers are more likely to reach the
        // resequencer first.
        //
        // Example:
        //
        // - line 1 waits about 80 ms
        // - line 2 waits about 40 ms
        // - line 3 waits 0 ms
        //
        // That creates an intentionally out-of-order arrival pattern:
        // line 3 may finish before line 1.
        //
        // Math.max(0, ...) prevents negative delays. If lineNumber is greater
        // than 3, the computed value would be negative, and Thread.sleep(...)
        // cannot sleep for a negative amount of time.
        long delayMillis = Math.max(0, 3 - lineItem.lineNumber()) * 40L;
        if (delayMillis == 0) {
            // No delay is needed for this line item, so continue immediately.
            return;
        }

        try {
            // Thread.sleep(...) pauses only the current worker thread.
            //
            // It does not pause the whole application. Other executor threads
            // can still process their own line-item messages while this one is
            // waiting.
            Thread.sleep(delayMillis);
        } catch (InterruptedException exception) {
            // InterruptedException means:
            // "Something asked this sleeping thread to stop waiting."
            //
            // Calling interrupt() again restores the interrupted flag on the
            // current thread. That is the normal Java pattern because catching
            // InterruptedException clears that flag.
            Thread.currentThread().interrupt();

            // This lesson does not know how to continue safely if the pricing
            // pause is interrupted, so it turns the interruption into an
            // unchecked exception that Spring Integration can handle.
            throw new IllegalStateException("Interrupted while pricing lesson 09 line item", exception);
        }
    }
}
