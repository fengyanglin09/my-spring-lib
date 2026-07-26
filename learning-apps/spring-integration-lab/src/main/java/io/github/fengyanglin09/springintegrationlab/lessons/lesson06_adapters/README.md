# Lesson 06: Inbound And Outbound Adapters

## What This Solves

Understand how messages enter and leave a Spring Integration flow through files,
HTTP, databases, brokers, sockets, mail, and other systems.

## Mental Model

Adapters are loading docks. They translate between the outside world's protocol
and Spring Integration's internal message model.

In this lesson, the outside world is simulated with simple Java classes:

- `Lesson06ExternalOrderInbox`: a stand-in for an external source.
- `Lesson06PartnerOrderOutbox`: a stand-in for an external target.

That keeps the lesson deterministic. The concept is the same whether the real
outside system is a folder, database, message broker, REST API, or SFTP server.

## Core Vocabulary

- Channel adapter
- Gateway
- Inbound adapter
- Outbound adapter
- Inbound gateway
- Outbound gateway
- Protocol module

## Concept Map

```text
outside system
    -> inbound adapter
    -> Spring Integration message flow
    -> outbound adapter
    -> outside system
```

## Main Ideas

- Inbound adapters create messages from external input.
- Outbound adapters send messages to external targets without expecting a reply.
- Gateways model request-reply interactions.
- Protocol adapters should keep transport details away from business logic.
- A poller is a scheduler that repeatedly asks an inbound source whether data is
  available.
- If a polled source returns `null`, no message is created for that poll.

## Decision Rules

- Use an inbound adapter when the external system starts the flow.
- Use an outbound adapter when the flow emits data and does not need a reply.
- Use a gateway when request-reply behavior matters.
- Learn adapters only as needed; the catalog is large.

## Common Traps

- Studying every adapter before understanding the core model.
- Confusing adapter and gateway behavior.
- Letting protocol details leak into domain services.

## How This Connects

Adapters sit at flow boundaries. The same message, channel, endpoint, and error
handling ideas still apply once data is inside the flow.

## Reference Checklist

- [ ] Can I distinguish inbound from outbound?
- [ ] Can I distinguish adapter from gateway?
- [ ] Can I describe what transport details the adapter hides?

## Mini Scenario

An external order record appears in a source system. An inbound adapter polls for
that record and turns it into a Spring Integration message. The flow translates
the data into a partner request. An outbound adapter sends that request to a
partner system.

## Files

- `support/Lesson06Channels.java`: shared channel bean names.
- `support/Lesson06AdapterIds.java`: stable endpoint id used by the test.
- `support/Lesson06ExternalOrderInbox.java`: simulated external source.
- `support/Lesson06PartnerOrderOutbox.java`: simulated external target.
- `config/Lesson06ChannelConfiguration.java`: explicit boundary channels.
- `model/Lesson06ExternalOrderRecord.java`: data shape from the external source.
- `model/Lesson06InternalOrder.java`: data shape used inside the app.
- `model/Lesson06PartnerOrderRequest.java`: data shape sent to the partner.
- `model/Lesson06PartnerOrderReceipt.java`: recorded result in the simulated partner.
- `handler/Lesson06AdapterTranslator.java`: maps external/internal/partner shapes.
- `flow/Lesson06AdapterFlows.java`: inbound adapter flow and outbound adapter flow.
- `Lesson06AdaptersSpec.groovy`: proves the boundary flow end to end.

## Code Walkthrough

```text
Lesson06ExternalOrderInbox
    -> inbound adapter polls externalInbox.poll()
    -> lesson06ExternalOrderRecords channel
    -> transform external record to internal order
    -> transform internal order to partner request
    -> lesson06PartnerOrderRequests channel
    -> outbound adapter calls partnerOutbox.send(...)
    -> Lesson06PartnerOrderReceipt stored by the simulated partner
```

The inbound adapter starts the flow:

```java
IntegrationFlow.fromSupplier(externalInbox::poll, adapter -> ...)
```

Read that as:

```text
Ask externalInbox.poll() for external data.
If it returns a non-null value, wrap that value as a message payload.
```

The outbound adapter ends the flow:

```java
.handle(Lesson06PartnerOrderRequest.class, (request, headers) -> {
    partnerOutbox.send(request);
    return null;
})
```

Read that as:

```text
Take the current payload as a Lesson06PartnerOrderRequest.
Call partnerOutbox.send(request).
Return null because the outbound adapter is one-way and the flow ends here.
```

The `headers` parameter contains message metadata. This lesson does not use it,
but real flows often use headers for correlation ids, source names, trace ids, or
routing details.

## Why No Real File Or HTTP Adapter Yet

Spring Integration has many protocol-specific adapters. We will use them later
when the protocol itself is the lesson.

For this lesson, using local Java classes keeps the focus on the adapter shape:

```text
outside -> inbound adapter -> message flow -> outbound adapter -> outside
```

The important boundary idea is already visible without introducing file paths,
ports, credentials, broker setup, or retry behavior.

## Adapter Versus Gateway

Use an adapter when the communication is one-way:

```text
inbound adapter  = outside system provides data to us
outbound adapter = we send data out and do not wait for a reply
```

Use a gateway when the communication is request-reply:

```text
outbound gateway = we send a request out and expect a response back
inbound gateway  = outside system calls in and expects a response from us
```

## Run The Lesson Test

```bash
./mvnw -pl learning-apps/spring-integration-lab -Dtest=Lesson06AdaptersSpec test
```

## What Comes Next

Lesson 07 goes deeper on transformers and enrichers: components that reshape
payloads and add useful message data.

## Official Docs

- [Channel Adapter](https://docs.spring.io/spring-integration/reference/channel-adapter.html)
- [Inbound Channel Adapters In The Java DSL](https://docs.spring.io/spring-integration/reference/dsl/java-inbound-adapters.html)
- [Integration Endpoint Summary](https://docs.spring.io/spring-integration/reference/endpoint-summary.html)
