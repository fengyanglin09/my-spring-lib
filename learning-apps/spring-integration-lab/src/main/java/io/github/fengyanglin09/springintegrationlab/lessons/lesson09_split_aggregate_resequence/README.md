# Lesson 09: Split, Aggregate, And Resequence

## What This Solves

Understand how one message becomes many messages and how related messages are
combined or reordered later.

## Mental Model

This is assembly-line batching. A box is unpacked into items, each item is
processed, and the results are packed back together when the set is complete.

## Core Vocabulary

- Splitter
- Aggregator
- Resequencer
- Correlation ID
- Sequence number
- Sequence size
- Release strategy
- Message group

## Concept Map

```text
one message
    -> splitter
    -> many related messages
    -> aggregator or resequencer
    -> one combined or ordered result
```

## Main Ideas

- Splitters create related messages from one input.
- Aggregators combine related messages into a result.
- Resequencers restore ordering without necessarily combining payloads.
- Correlation and release rules decide which messages belong together and when a group is ready.

## Decision Rules

- Use a splitter when independent parts can be processed separately.
- Use an aggregator when later logic needs the grouped result.
- Use a resequencer when order matters after parallel or async work.
- Always define the timeout and incomplete-group story.

## Common Traps

- Forgetting how messages are correlated.
- Waiting forever for a missing message.
- Aggregating too much data in memory.

## How This Connects

Split and aggregate patterns often combine with routing, async channels, message
stores, error handling, and idempotency.

## Reference Checklist

- [ ] Can I say what makes messages part of the same group?
- [ ] Can I explain when a group is released?
- [ ] Can I describe what happens if one part fails or never arrives?

## Mini Scenario

A bulk order is split into line-item messages, each line is priced separately,
and the results are aggregated into one priced order summary.

## Files In This Lesson

```text
lesson09_split_aggregate_resequence/
|-- README.md
|-- package-info.java
|-- config/
|   `-- Lesson09IntegrationConfiguration.java
|-- flow/
|   `-- Lesson09SplitAggregateResequenceFlow.java
|-- gateway/
|   `-- Lesson09BatchOrderGateway.java
|-- handler/
|   `-- Lesson09BatchOrderProcessor.java
|-- model/
|   |-- Lesson09BatchOrderRequest.java
|   |-- Lesson09LineItemRequest.java
|   |-- Lesson09LineItemWork.java
|   |-- Lesson09PricedLineItem.java
|   `-- Lesson09OrderSummary.java
`-- support/
    `-- Lesson09Channels.java
```

Test mirror:

```text
src/test/groovy/io/github/fengyanglin09/springintegrationlab/lessons/lesson09_split_aggregate_resequence/
`-- Lesson09SplitAggregateResequenceSpec.groovy
```

## Code Walkthrough

```text
Lesson09BatchOrderGateway.price(batch)
    -> sends one batch message to lesson09BatchOrders
    -> splitter creates one message per line item
    -> executor channel lets line items be processed on background threads
    -> transformer prices each line item
    -> resequencer releases line items in original sequence-number order
    -> aggregator combines the complete group into Lesson09OrderSummary
```

The gateway is still request-reply from the caller's point of view. The caller
sends one `Lesson09BatchOrderRequest` and receives one `Lesson09OrderSummary`.
The temporary fan-out into many line-item messages is an internal flow detail.

## What To Notice In The Code

- `split(...)` returns a `List`, and Spring Integration turns each list item
  into a new message.
- The splitter adds `CORRELATION_ID`, `SEQUENCE_NUMBER`, and `SEQUENCE_SIZE`
  headers. These are the "which batch?" and "which position?" sticky notes.
- `channel(MessageChannels.executor(...))` allows split messages to continue on
  worker threads, so completion order can differ from original order.
- `resequence()` uses sequence headers to release messages in order. It does not
  combine messages.
- `aggregate(...)` waits for the complete related group and then combines the
  group into one reply payload.
- `expireGroupsUponCompletion(true)` clears the completed group after the
  summary is produced.

## Production Note

This lesson keeps the happy path small. In production, aggregation and
resequencing usually need an explicit timeout and an incomplete-group policy so
the system does not wait forever if one split message fails or never arrives.

## Official Docs

- [Splitter](https://docs.spring.io/spring-integration/reference/splitter.html)
- [Aggregator](https://docs.spring.io/spring-integration/reference/aggregator.html)
- [Resequencer](https://docs.spring.io/spring-integration/reference/resequencer.html)
- [Java DSL Aggregators And Resequencers](https://docs.spring.io/spring-integration/reference/dsl/java-aggregators.html)
