# Lesson 15: Adapter Deep Dives

## What This Solves

Create a repeatable way to study only the protocol adapters your application
actually needs.

This lesson is more specific than Lesson 06.

Lesson 06 answered:

- What is an inbound adapter?
- What is an outbound adapter?

Lesson 15 answers:

- What exactly crosses the adapter boundary?
- Which external fields become payload?
- Which external fields become headers?
- How do headers survive while payloads are transformed?
- How does the outbound side rebuild an external protocol envelope?

## Mental Model

Each adapter is a translator at the border. The language outside changes, but
inside the flow everything should still look like messages, channels, and endpoints.

Analogy:

An adapter is like a customs desk at the border.

- The outside system arrives with its own document format.
- The inbound adapter translates it into the internal message format.
- The flow works with normal Spring Integration messages.
- The outbound adapter translates the internal message back into the outside
  system's format.

## Core Vocabulary

- Inbound adapter: brings outside data into a flow.
- Outbound adapter: sends a message from a flow to something outside.
- Gateway: request-reply boundary where the caller expects a response.
- Payload mapping: deciding which external data becomes the message payload.
- Header mapping: deciding which external metadata becomes message headers.
- Protocol envelope: the outside system's full record, frame, request, file, or
  event shape.

## Concept Map

```text
fake courier protocol
    -> inbound adapter source
    -> Spring Integration message
       payload: Lesson15CourierInboundFrame
       headers: frame id, remote system, content type
    -> transform to Lesson15ShipmentCommand
    -> transform to Lesson15PartnerExport
    -> outbound adapter
    -> fake courier outbound frame
```

The lesson uses a fake courier protocol so we can study adapter behavior without
adding external infrastructure such as Kafka, JMS, SFTP, or HTTP.

## Main Ideas

- Adapter study should be demand-driven.
- Each adapter has inbound, outbound, and sometimes gateway forms.
- Header mapping is often the key adapter detail.
- Testing strategy depends on the external protocol.
- Payloads can change many times inside a flow.
- Headers are useful when metadata must survive those payload changes.

## Decision Rules

- Pick the adapter based on the real boundary, not because it is interesting.
- Learn the inbound path, outbound path, header mapping, error behavior, and testing approach.
- Keep adapter concerns at the edge of the flow.
- Stop once you understand the adapter behavior your use case needs.
- Use named header constants when a header is part of the lesson's contract.
- Test adapter boundaries by checking both business data and metadata.

## Common Traps

- Trying to memorize the adapter catalog.
- Treating each adapter as a new framework instead of a boundary implementation.
- Forgetting that protocol behavior affects retries, idempotency, and transactions.
- Transforming the payload and assuming protocol metadata is gone.
- Hiding important protocol metadata inside string parsing instead of mapping it
  into headers.

## Code Shape

```text
lesson15_adapter_deep_dives/
|-- README.md
|-- package-info.java
|-- flow/
|   `-- Lesson15AdapterDeepDiveFlow.java
|-- handler/
|   `-- Lesson15CourierFrameMapper.java
|-- model/
|   |-- Lesson15CourierInboundFrame.java
|   |-- Lesson15CourierOutboundFrame.java
|   |-- Lesson15PartnerExport.java
|   `-- Lesson15ShipmentCommand.java
`-- support/
    |-- Lesson15AdapterIds.java
    |-- Lesson15Channels.java
    |-- Lesson15CourierFrameSource.java
    |-- Lesson15CourierOutboundAdapter.java
    |-- Lesson15CourierProtocolSandbox.java
    `-- Lesson15HeaderNames.java
```

## Test Shape

```text
src/test/groovy/io/github/fengyanglin09/springintegrationlab/lessons/lesson15_adapter_deep_dives/
`-- Lesson15AdapterDeepDivesSpec.groovy
```

The test starts the inbound adapter manually. That makes the example
deterministic:

1. Put a fake external frame into the sandbox.
2. Start the inbound adapter.
3. Wait until one outbound frame appears.
4. Assert that body data and header metadata both reached the outbound adapter.

## How This Connects

Adapter deep dives reuse every earlier lesson: messages, channels, endpoints,
flows, transformations, errors, transactions, tests, and operations.

This lesson especially reuses:

- Lesson 02: payloads and headers.
- Lesson 05: IntegrationFlow as a wiring chain.
- Lesson 06: inbound and outbound adapters.
- Lesson 07: transforming payloads while headers remain available.
- Lesson 10: pollers and manual start/stop in tests.

## Reference Checklist

- [ ] Can I map the external protocol data into message payload and headers?
- [ ] Can I explain inbound, outbound, and gateway options?
- [ ] Can I test the adapter boundary without relying on a shared external system?
- [ ] Can I explain why the outbound adapter still sees headers after the payload changes?
- [ ] Can I tell which code belongs at the adapter boundary and which code belongs in the business flow?

## Mini Scenario

Study a courier protocol by tracing:

1. External inbound frame arrives.
2. Inbound adapter turns that frame into a Spring Integration message.
3. Frame body becomes the payload.
4. Frame metadata becomes headers.
5. Payload is transformed into internal and outbound shapes.
6. Outbound adapter uses the current payload plus preserved headers to create an
   external outbound frame.

## Official Docs

- [Channel Adapter](https://docs.spring.io/spring-integration/reference/channel-adapter.html)
- [Integration Endpoints](https://docs.spring.io/spring-integration/reference/endpoint-summary.html)
- [Message](https://docs.spring.io/spring-integration/reference/message.html)
- [Spring Integration Reference](https://docs.spring.io/spring-integration/reference/)
