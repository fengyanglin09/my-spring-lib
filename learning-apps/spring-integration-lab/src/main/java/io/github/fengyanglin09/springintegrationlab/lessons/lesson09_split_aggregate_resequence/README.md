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

## Official Docs

- [Splitter](https://docs.spring.io/spring-integration/reference/splitter.html)
- [Aggregator](https://docs.spring.io/spring-integration/reference/aggregator.html)
- [Resequencer](https://docs.spring.io/spring-integration/reference/resequencer.html)
