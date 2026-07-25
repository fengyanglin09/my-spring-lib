# Lesson 13: Testing Integration Flows

## What This Solves

Understand how to test message components, partial flows, full flows, and
external protocol boundaries.

## Mental Model

Testing a flow is like testing a train route. Sometimes you inspect one station,
sometimes a segment of track, and sometimes the whole journey.

## Core Vocabulary

- Unit test
- Integration test
- Test channel
- Mock endpoint
- Flow isolation
- Embedded external system
- Contract boundary

## Concept Map

```text
component test
    -> partial flow test
    -> full flow test
    -> external adapter test
```

## Main Ideas

- Business handlers should often be tested without full flow infrastructure.
- Flow tests verify wiring and message movement.
- Adapter tests may need mocks, embedded servers, or contract tests.
- Tests should name which layer of confidence they provide.

## Decision Rules

- Test pure business behavior outside the integration flow when possible.
- Use flow-level tests for routing, transformation, and channel behavior.
- Use embedded or mock infrastructure for adapter boundaries.
- Avoid making every test a full application test.

## Common Traps

- Testing everything end-to-end and making failures hard to diagnose.
- Mocking so much that the flow wiring is never tested.
- Ignoring async timing in assertions.

## How This Connects

Testing depends on clear message shapes, named channels, isolated endpoints,
error handling, and adapter boundaries.

## Reference Checklist

- [ ] Can I say what layer this test covers?
- [ ] Can I test routing without the real external system?
- [ ] Can I handle async timing deterministically?

## Mini Scenario

A route test sends an order message into an input channel and verifies that VIP
orders appear on the priority channel while invalid orders appear on the rejection channel.

## Official Docs

- [Testing support](https://docs.spring.io/spring-integration/reference/testing.html)
