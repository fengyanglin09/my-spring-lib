# Lesson 15: Adapter Deep Dives

## What This Solves

Create a repeatable way to study only the protocol adapters your application
actually needs.

## Mental Model

Each adapter is a translator at the border. The language outside changes, but
inside the flow everything should still look like messages, channels, and endpoints.

## Core Vocabulary

- HTTP adapter
- File adapter
- JDBC adapter
- JMS adapter
- Kafka adapter
- SFTP adapter
- Mail adapter
- TCP adapter

## Concept Map

```text
protocol-specific world
    -> adapter mapping
    -> Spring Integration message model
    -> flow
```

## Main Ideas

- Adapter study should be demand-driven.
- Each adapter has inbound, outbound, and sometimes gateway forms.
- Header mapping is often the key adapter detail.
- Testing strategy depends on the external protocol.

## Decision Rules

- Pick the adapter based on the real boundary, not because it is interesting.
- Learn the inbound path, outbound path, header mapping, error behavior, and testing approach.
- Keep adapter concerns at the edge of the flow.
- Stop once you understand the adapter behavior your use case needs.

## Common Traps

- Trying to memorize the adapter catalog.
- Treating each adapter as a new framework instead of a boundary implementation.
- Forgetting that protocol behavior affects retries, idempotency, and transactions.

## How This Connects

Adapter deep dives reuse every earlier lesson: messages, channels, endpoints,
flows, transformations, errors, transactions, tests, and operations.

## Reference Checklist

- [ ] Can I map the external protocol data into message payload and headers?
- [ ] Can I explain inbound, outbound, and gateway options?
- [ ] Can I test the adapter boundary without relying on a shared external system?

## Mini Scenario

Study the SFTP adapter by tracing file discovery, message creation, file metadata
headers, successful transfer behavior, failed transfer behavior, and test setup.

## Official Docs

- [Integration Endpoints](https://docs.spring.io/spring-integration/reference/endpoint-summary.html)
- [Spring Integration Reference](https://docs.spring.io/spring-integration/reference/)
