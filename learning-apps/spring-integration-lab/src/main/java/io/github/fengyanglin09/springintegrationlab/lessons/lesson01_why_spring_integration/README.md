# Lesson 01: Why Spring Integration Exists

## What This Solves

Spring Integration helps when an application needs to move data through multiple
steps, systems, formats, or timing boundaries without turning one business
service into the coordinator for everything.

The core question for this lesson is:

```text
When is a message flow clearer than ordinary Java method calls?
```

If the work is simple, local, synchronous, and easy to read as plain Java, keep
it plain. Spring Integration earns its place when the problem is really about
movement: where data enters, how it changes, where it goes next, and what
happens when a step fails.

## Mental Model

Spring Integration is like a postal system inside your application.

```text
Message  = envelope
Payload  = letter inside the envelope
Headers  = labels, stamps, reply address, tracking information
Channel  = route or mailbox
Endpoint = worker that handles the envelope
Flow     = delivery path through the system
Adapter  = loading dock between your app and the outside world
```

The sender does not need to know exactly who handles the message. It puts a
message on a channel, and the flow decides what happens next.

## Core Vocabulary

- Enterprise Integration Patterns: common patterns for connecting applications and services.
- Message-driven architecture: components communicate by sending messages instead of direct calls.
- Loose coupling: senders and receivers know less about each other.
- Message: the envelope that moves through a flow.
- Payload: the business data inside the message.
- Headers: metadata used for routing, replies, errors, correlation, and tracing.
- Channel: the route between producers and consumers.
- Endpoint: a component that consumes, transforms, routes, or handles messages.
- Flow: a chain of message-processing steps.
- Adapter: a boundary component for files, HTTP, databases, brokers, mail, SFTP, and other systems.

## Concept Map

```text
External system or app code
    -> inbound adapter or gateway
    -> message
    -> channel
    -> endpoint
    -> channel
    -> endpoint
    -> outbound adapter or reply
```

## Main Ideas

- Spring Integration embeds integration behavior inside a Spring application.
- It separates business logic from routing, transformation, transport, timing, and failure handling.
- It is based on Enterprise Integration Patterns such as routing, filtering, transforming, splitting, aggregating, retrying, and error handling.
- It is useful when data needs to move through several independent steps or cross system boundaries.
- It gives a consistent message model even when the outside systems use different protocols.
- It is not a replacement for simple method calls when the flow is direct and local.

Without a message-flow model, one service can quietly become responsible for
everything:

```text
OrderService
    -> validate order
    -> load customer
    -> decide billing path
    -> call billing
    -> send notification
    -> handle every failure path
```

With Spring Integration, the movement can be described as a flow:

```text
Order message
    -> validate
    -> enrich with customer data
    -> route by order state
    -> bill
    -> notify
```

That does not automatically make the design better. The flow is worth it only
when the integration structure becomes clearer than direct orchestration.

## Decision Rules

- Use it when integration concerns are becoming tangled with business services.
- Use it when different transports or data formats should be hidden behind one model.
- Use it when data needs routing, filtering, transformation, fan-out, polling, retry, or async handoff.
- Use it when external boundaries should stay at the edge of the application instead of leaking into domain logic.
- Pause before using it for a simple request-reply call with no routing, transformation, or timing concerns.
- Prefer plain Java when a senior engineer could understand the behavior faster as a direct method call.

## Common Traps

- Treating Spring Integration as an external broker. It can use brokers, but it is not one by default.
- Starting with adapter details before learning message, channel, endpoint, and flow.
- Using a flow when a plain Java method would be easier to read and test.
- Hiding business rules inside flow wiring.
- Assuming channels always mean Kafka, RabbitMQ, or another external queue.
- Adding asynchronous handoff without revisiting error handling.

## How This Connects

This lesson frames the rest of the module. Messages, channels, endpoints, and
flows are the core grammar used by every later lesson.

```text
Lesson 02: Messages
Lesson 03: Channels
Lesson 04: Endpoints
Lesson 05: Flows
Lesson 06+: Specialized flow behavior and external boundaries
```

## Reference Checklist

- [ ] Can I explain the problem Spring Integration solves?
- [ ] Can I name the core building blocks: message, channel, endpoint, flow, adapter?
- [ ] Can I explain why loose coupling matters in integration-heavy code?
- [ ] Can I tell when a normal method call is simpler than a message flow?
- [ ] Can I describe the difference between business logic and integration logic?
- [ ] Can I explain why learning adapters first would make the topic feel larger than it is?

## Mini Scenario

An order enters the system.

```text
Receive order
    -> validate it
    -> enrich it with customer data
    -> route VIP orders to priority billing
    -> route invalid orders to review
    -> send successful orders to notification
```

In plain Java, one service might coordinate all of that directly. In Spring
Integration, each step can become a separate message-processing component, and
the flow describes how messages move between those components.

The key design question is not, "Can Spring Integration do this?" It probably
can. The better question is, "Does the flow make the movement easier to reason
about?"

## Files

- `model/Lesson01OrderRequest.java`: the payload entering the flow.
- `model/Lesson01OrderResult.java`: the reply payload returned by the flow.
- `gateway/Lesson01OrderGateway.java`: the typed entry point used by callers.
- `flow/Lesson01WhySpringIntegrationFlow.java`: the Spring Integration wiring.
- `handler/Lesson01OrderHandler.java`: the business behavior called by the flow.
- `Lesson01WhySpringIntegrationSpec.groovy`: proves the gateway-to-flow-to-handler path.

## Code Walkthrough

This lesson uses the smallest useful request-reply shape:

```text
test caller
    -> Lesson01OrderGateway
    -> lesson01OrderRequests channel
    -> Lesson01WhySpringIntegrationFlow
    -> Lesson01OrderHandler
    -> Lesson01OrderResult reply
```

The gateway gives normal application code a typed Java method. The flow turns
that method call into a message exchange. The handler owns the business decision:
VIP orders go to `priority-billing`; other orders go to `standard-billing`.

This lesson intentionally avoids explicit channel configuration, HTTP
controllers, validation, async handoff, retries, and error channels. Those are
later lessons. The point here is only to see the basic movement.

## Run The Lesson Test

```bash
./mvnw -pl learning-apps/spring-integration-lab -Dtest=Lesson01WhySpringIntegrationSpec test
```

## What Comes Next

Lesson 02 keeps the same mental model but zooms in on the message itself:
payload, headers, reply channel, error channel, and metadata.

## Official Docs

- [Spring Integration Reference](https://docs.spring.io/spring-integration/reference/)
- [Overview of Spring Integration Framework](https://docs.spring.io/spring-integration/reference/overview.html)
- [Messaging Gateways](https://docs.spring.io/spring-integration/reference/gateway.html)
- [Java DSL](https://docs.spring.io/spring-integration/reference/dsl.html)
