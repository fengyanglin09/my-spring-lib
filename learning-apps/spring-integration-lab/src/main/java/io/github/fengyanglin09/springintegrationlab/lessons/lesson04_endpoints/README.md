# Lesson 04: Endpoints

## What This Solves

Understand the components that are attached to channels and perform work when a
message arrives.

## Mental Model

Endpoints are workers stationed along a conveyor line.

The channel is the conveyor belt. The endpoint is the worker standing beside the
belt. When a message reaches the endpoint, the endpoint does one job, such as
calling a method, changing the payload, filtering a message, routing a message,
or talking to another system.

## Core Vocabulary

- Endpoint
- Message handler
- Event-driven consumer
- Polling consumer
- Service activator
- Transformer
- Input channel
- Output channel

## Concept Map

```text
gateway method
    -> input channel
    -> transformer endpoint
    -> output channel
    -> service-activator endpoint
    -> reply returned to gateway caller
```

## Main Ideas

- Endpoints connect behavior to channels.
- A method is not an endpoint by itself. It becomes endpoint behavior when an
  `IntegrationFlow` connects a channel to that method.
- A transformer endpoint changes the message payload and passes the changed
  message onward.
- A service activator endpoint calls ordinary application code to do work for a
  message.
- Event-driven consumers react when a subscribable channel delivers a message.
- Polling consumers periodically receive from pollable channels.
- Many EIP components are specialized endpoint behaviors.

## Decision Rules

- Use a service activator for business work.
- Use a transformer when changing message shape.
- Use a router when choosing the next path.
- Use an adapter when crossing the application boundary.

## Common Traps

- Calling every component an adapter.
- Putting routing rules inside business services.
- Ignoring whether an endpoint is event-driven or polling.

## How This Connects

Endpoints are where flows become active. Later lessons study specialized
endpoint types such as routers, transformers, splitters, aggregators, and adapters.

## Reference Checklist

- [ ] Can I define an endpoint?
- [ ] Can I distinguish event-driven from polling consumers?
- [ ] Can I pick the right endpoint type for a job?

## Mini Scenario

A raw order enters the flow. One endpoint normalizes the customer type from
`" vip "` to `"VIP"`. A second endpoint creates the final report returned to the
caller.

## Files

- `support/Lesson04Channels.java`: shared channel bean names.
- `config/Lesson04ChannelConfiguration.java`: explicit `DirectChannel` beans for the start and middle of the flow.
- `model/Lesson04OrderRequest.java`: raw payload entering the flow.
- `model/Lesson04NormalizedOrder.java`: payload after the transformer endpoint changes it.
- `model/Lesson04EndpointReport.java`: reply payload returned by the final endpoint.
- `gateway/Lesson04EndpointGateway.java`: typed method that sends a request into the first channel.
- `flow/Lesson04EndpointFlow.java`: connects channels to endpoint behavior.
- `handler/Lesson04OrderNormalizer.java`: transformer endpoint behavior.
- `handler/Lesson04OrderReporter.java`: service-activator endpoint behavior.
- `Lesson04EndpointsSpec.groovy`: proves the transformer runs before the service activator.

## Code Walkthrough

```text
Lesson04EndpointGateway.process(...)
    -> lesson04RawOrders DirectChannel
    -> transformer endpoint calls Lesson04OrderNormalizer.normalize(...)
    -> lesson04NormalizedOrders DirectChannel
    -> service-activator endpoint calls Lesson04OrderReporter.report(...)
    -> Lesson04EndpointReport returned to the gateway caller
```

The first endpoint is created by this flow step:

```java
.transform(normalizer, "normalize")
```

Read that as:

```text
When a message reaches this step, call normalizer.normalize(...)
and replace the current payload with that method's return value.
```

The second endpoint is created by this flow step:

```java
.handle(reporter, "report")
```

Read that as:

```text
When a message reaches this step, call reporter.report(...)
and use that method's return value as the reply.
```

The middle channel is included on purpose:

```java
.channel(Lesson04Channels.NORMALIZED_ORDERS)
```

It makes the handoff visible. The transformer sends a normalized order message
to `lesson04NormalizedOrders`, and the service activator receives that normalized
order message from the same channel.

## Run The Lesson Test

```bash
./mvnw -pl learning-apps/spring-integration-lab -Dtest=Lesson04EndpointsSpec test
```

## What Comes Next

Lesson 05 zooms out from individual endpoints to complete `IntegrationFlow`
definitions as readable wiring diagrams.

## Official Docs

- [Message Endpoints](https://docs.spring.io/spring-integration/reference/endpoint.html)
- [Service Activator](https://docs.spring.io/spring-integration/reference/service-activator.html)
- [Transformer](https://docs.spring.io/spring-integration/reference/transformer.html)
