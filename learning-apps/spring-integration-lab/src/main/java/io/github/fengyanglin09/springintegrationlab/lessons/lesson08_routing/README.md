# Lesson 08: Routing

## What This Solves

Understand how a flow chooses where each message should go next.

## Mental Model

A router is a sorting desk. It reads the message and places it on the right
outgoing path.

A filter is a gate before the sorting desk. It decides whether the message is
allowed to reach the router at all.

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
    -> filter
        -> rejected discard path
        -> accepted path
            -> router
                -> expedited branch
                -> review branch
                -> standard branch
```

## Main Ideas

- Routers choose a destination channel.
- Filters decide whether a message continues.
- Recipient lists can send a message to multiple selected recipients.
- Routing rules should be understandable without reading every downstream handler.
- A discard flow makes rejected messages explicit instead of letting them appear
  to vanish.
- Typed lambda routing avoids string method names and lets Java see the payload
  type.

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

## Files

- `support/Lesson08Channels.java`: shared channel bean names.
- `model/Lesson08OrderRequest.java`: raw payload entering the flow.
- `model/Lesson08RoutableOrder.java`: payload after route classification.
- `model/Lesson08RouteKey.java`: enum values used by the router.
- `model/Lesson08RoutingResult.java`: final reply from the gateway.
- `config/Lesson08ChannelConfiguration.java`: explicit input channel.
- `gateway/Lesson08RoutingGateway.java`: typed entry point into the flow.
- `handler/Lesson08RoutingRules.java`: filter, route classification, and branch behavior.
- `flow/Lesson08RoutingFlow.java`: filter plus router flow.
- `Lesson08RoutingSpec.groovy`: proves reject, expedited, review, and standard paths.

## Code Walkthrough

```text
Lesson08RoutingGateway.route(...)
    -> lesson08OrderRequests channel
    -> filter acceptable orders
        -> rejected orders go to discard flow
    -> transform accepted request into Lesson08RoutableOrder
    -> route by Lesson08RouteKey
        -> EXPEDITED branch
        -> REVIEW branch
        -> STANDARD branch
    -> Lesson08RoutingResult returned to the gateway caller
```

The filter answers one question:

```text
Should this message continue?
```

In code:

```java
.filter(Lesson08OrderRequest.class, request -> routingRules.acceptable(request), ...)
```

If the answer is `true`, the message continues to classification and routing.

If the answer is `false`, the message goes to the discard flow:

```java
.discardFlow(discarded -> discarded.handle(...))
```

The discard flow is intentionally explicit. Without an explicit discard path, a
rejected message can feel like it disappeared. In this lesson, rejected messages
return a `Lesson08RoutingResult` with `path = "REJECTED"`.

The router answers a different question:

```text
Which accepted path should this message take?
```

In code:

```java
.route(Lesson08RoutableOrder.class, order -> order.routeKey(), routes -> ...)
```

The route key is an enum:

```text
EXPEDITED
REVIEW
STANDARD
```

Each key maps to a subflow:

```java
.subFlowMapping(Lesson08RouteKey.EXPEDITED, expedited -> ...)
```

Read that as:

```text
When routeKey is EXPEDITED, send the message through this inline branch.
```

## Filter Versus Router

Use a filter when the decision is yes/no:

```text
verified customer and positive amount?
```

Use a router when the message is valid but has several possible next paths:

```text
expedited, review, or standard?
```

Putting both in one flow is common:

```text
filter out invalid messages first, then route the valid messages
```

## Run The Lesson Test

```bash
./mvnw -pl learning-apps/spring-integration-lab -Dtest=Lesson08RoutingSpec test
```

## What Comes Next

Lesson 09 goes from choosing one path to splitting one message into many pieces
and gathering pieces back together.

## Official Docs

- [Message Routing](https://docs.spring.io/spring-integration/reference/router.html)
- [Filter](https://docs.spring.io/spring-integration/reference/filter.html)
- [Java DSL Routers](https://docs.spring.io/spring-integration/reference/dsl/java-routers.html)
- [Java DSL Subflows](https://docs.spring.io/spring-integration/reference/dsl/java-subflows.html)
