# Lesson 08: Routing

## What This Solves

Understand how a flow chooses where each message should go next.

## Mental Model

A router is a sorting desk. It reads the message and places it on the right
outgoing path.

## Core Vocabulary

- Router
- Filter
- Recipient list
- Routing slip
- Dynamic router
- Discard channel
- Default channel

## Concept Map

```text
message
    -> routing rule
        -> channel A
        -> channel B
        -> discard or default channel
```

## Main Ideas

- Routers choose a destination channel.
- Filters decide whether a message continues.
- Recipient lists can send a message to multiple selected recipients.
- Routing rules should be understandable without reading every downstream handler.

## Decision Rules

- Use a router when there are multiple valid next paths.
- Use a filter when the answer is continue or stop.
- Use a recipient list when several paths may apply.
- Avoid dynamic routing until static routing is too rigid.

## Common Traps

- Encoding business policy in scattered route expressions.
- Dropping filtered messages without a discard strategy.
- Making route names vague enough that flow diagrams stop helping.

## How This Connects

Routing often follows transformation or enrichment and often precedes specialized
handlers, adapters, or split/aggregate patterns.

## Reference Checklist

- [ ] Can I explain the routing decision in one sentence?
- [ ] Can I identify the default or discard path?
- [ ] Can I tell whether this should be a router or a filter?

## Mini Scenario

Orders under review go to a manual review channel, normal orders go to billing,
and invalid orders go to a rejection channel.

## Official Docs

- [Message Routing](https://docs.spring.io/spring-integration/reference/router.html)
- [Filter](https://docs.spring.io/spring-integration/reference/filter.html)
