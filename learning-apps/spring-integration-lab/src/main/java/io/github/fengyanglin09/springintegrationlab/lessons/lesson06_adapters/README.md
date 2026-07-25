# Lesson 06: Inbound And Outbound Adapters

## What This Solves

Understand how messages enter and leave a Spring Integration flow through files,
HTTP, databases, brokers, sockets, mail, and other systems.

## Mental Model

Adapters are loading docks. They translate between the outside world's protocol
and Spring Integration's internal message model.

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
    -> message flow
    -> outbound adapter
    -> outside system
```

## Main Ideas

- Inbound adapters create messages from external input.
- Outbound adapters send messages to external targets without expecting a reply.
- Gateways model request-reply interactions.
- Protocol adapters should keep transport details away from business logic.

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

An SFTP inbound adapter picks up a file, a flow converts it to order messages,
and an HTTP outbound gateway submits each order to a partner API.

## Official Docs

- [Integration Endpoints](https://docs.spring.io/spring-integration/reference/endpoint-summary.html)
