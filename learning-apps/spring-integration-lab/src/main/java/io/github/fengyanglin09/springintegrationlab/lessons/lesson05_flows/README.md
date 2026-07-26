# Lesson 05: Flows

## What This Solves

Understand how channels and endpoints are composed into one readable integration
pipeline.

## Mental Model

A flow is a wiring diagram. It shows where messages enter, which stations they
visit, and where they leave.

If a channel is a conveyor belt and an endpoint is a worker beside the belt, an
`IntegrationFlow` is the map of the whole conveyor line.

## Core Vocabulary

- Integration flow
- Java DSL
- Input channel
- Output channel
- Flow composition
- Explicit channel
- Implicit handoff
- Flow boundary

## Concept Map

```text
gateway method
    -> input channel
    -> transform
    -> named middle channel
    -> transform
    -> handle
    -> reply returned to gateway caller
```

## Main Ideas

- A flow expresses message movement and processing steps.
- The Java DSL is a fluent Java builder. "Fluent" means each method call returns
  the builder so you can chain the next method call.
- The flow should read top to bottom like a map.
- Flows should reveal intent without burying business rules in wiring.
- Flow boundaries should stay small enough to reason about.
- You can name important channels explicitly.
- You do not need to name every handoff. Spring Integration can wire adjacent
  steps with internal direct handoffs.

## Decision Rules

- Keep a flow focused on one business journey.
- Split a flow when branches become hard to scan.
- Prefer clear endpoint names over clever inline logic.
- Use sub-flows when a branch is meaningful on its own.

## Common Traps

- Letting the DSL become a dense wall of behavior.
- Hiding complex business decisions inside lambdas.
- Creating one giant flow for an entire application.

## How This Connects

Flows are the organizing layer for the rest of the module. Every later lesson can
be read as a specific kind of flow step.

## Reference Checklist

- [ ] Can I draw a flow before writing one?
- [ ] Can I identify each step's responsibility?
- [ ] Can I tell when a flow should be split?

## Mini Scenario

An order enters the flow. The flow normalizes the order, marks the visible
checkpoint where the order is normalized, assigns a handling lane, and returns a
summary.

## Files

- `support/Lesson05Channels.java`: shared channel bean names.
- `config/Lesson05ChannelConfiguration.java`: explicit channels used by the flow.
- `model/Lesson05OrderRequest.java`: raw payload entering the flow.
- `model/Lesson05NormalizedOrder.java`: payload after the first transformer step.
- `model/Lesson05PrioritizedOrder.java`: payload after the handling-lane step.
- `model/Lesson05FlowResult.java`: final reply payload.
- `gateway/Lesson05FlowGateway.java`: typed entry point into the flow.
- `flow/Lesson05OrderWorkflowFlow.java`: the wiring diagram for this lesson.
- `handler/Lesson05OrderWorkflowSteps.java`: named business methods called by the flow.
- `Lesson05FlowsSpec.groovy`: proves the flow runs in the expected order.

## Code Walkthrough

```text
Lesson05FlowGateway.prepare(...)
    -> lesson05OrderRequests DirectChannel
    -> transform: Lesson05OrderWorkflowSteps.normalize(...)
    -> lesson05NormalizedOrders DirectChannel
    -> transform: Lesson05OrderWorkflowSteps.assignHandlingLane(...)
    -> handle: Lesson05OrderWorkflowSteps.summarize(...)
    -> Lesson05FlowResult returned to the gateway caller
```

This lesson has one named input channel:

```java
IntegrationFlow.from(Lesson05Channels.ORDER_REQUESTS)
```

Read that as:

```text
Start this flow when a message is sent to lesson05OrderRequests.
```

It also has one named middle channel:

```java
.channel(Lesson05Channels.NORMALIZED_ORDERS)
```

This middle channel is optional in this lesson. The flow would still work if the
two `transform(...)` calls and the `handle(...)` call were chained directly.

Read that as:

```text
After normalization, send the message to the named checkpoint channel
lesson05NormalizedOrders before continuing.
```

The flow does not name every internal handoff. After
`assignHandlingLane(...)`, the next step is `summarize(...)`. Spring Integration
connects those adjacent steps for us. We only name a channel when the name helps
the lesson or when another component needs to send to or receive from that point.

## Why The Flow Does Not Contain The Business Rule

The flow says:

```text
normalize -> assign handling lane -> summarize
```

The handler says:

```text
if expedited, use EXPEDITED
else if order amount is at least 1000.00, use REVIEW
else use STANDARD
```

That separation is intentional. The flow is easier to read when it shows the
journey. The handler is easier to test when it owns the decision details.

## Transform Versus Handle

`transform(...)` and `handle(...)` can both call Java methods, and both can pass
a return value onward.

The difference is the intent you want the flow to communicate:

```text
transform(...) = convert this payload into another payload shape
handle(...)    = do application work for this message
```

In this lesson:

```text
normalize            = transform, because raw order becomes normalized order
assignHandlingLane   = transform, because normalized order becomes prioritized order
summarize            = handle, because the flow performs the final application action
```

If a middle `handle(...)` returns `void` or `null`, the flow does not keep using
the previous payload. No reply was produced, so the downstream steps do not run.
If the flow should continue, the method should return the payload it wants the
next step to receive.

## Run The Lesson Test

```bash
./mvnw -pl learning-apps/spring-integration-lab -Dtest=Lesson05FlowsSpec test
```

## What Comes Next

Lesson 06 crosses the application boundary with adapters: components that bring
messages in from, or send messages out to, external systems.

## Official Docs

- [DSL Basics](https://docs.spring.io/spring-integration/reference/dsl/java-basics.html)
- [Working With Message Flows](https://docs.spring.io/spring-integration/reference/dsl/java-flows.html)
