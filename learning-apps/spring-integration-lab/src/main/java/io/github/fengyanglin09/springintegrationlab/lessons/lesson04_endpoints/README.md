# Lesson 04: Endpoints

## What This Solves

Understand the components that receive messages from channels and perform work.

## Mental Model

Endpoints are workers stationed along a conveyor line. Each worker has a job:
handle, transform, filter, route, split, aggregate, or talk to another system.

## Core Vocabulary

- Endpoint
- Message handler
- Event-driven consumer
- Polling consumer
- Service activator
- Gateway
- Channel adapter

## Concept Map

```text
input channel
    -> endpoint
    -> output channel or reply
```

## Main Ideas

- Endpoints connect behavior to channels.
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

An endpoint validates an incoming order message and sends valid messages to the
billing channel while invalid messages go to review.

## Official Docs

- [Message Endpoints](https://docs.spring.io/spring-integration/reference/endpoint.html)
