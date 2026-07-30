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

- The flow code in this lesson is intentionally familiar.
- The new learning is in how we test the flow, not in a new endpoint type.
- Business handlers should often be tested without full flow infrastructure.
- Flow tests verify wiring and message movement.
- Adapter tests may need mocks, embedded servers, or contract tests.
- Tests should name which layer of confidence they provide.

## What Is New Here

The production code in `src/main/java` does not introduce a new Spring
Integration building block. It reuses patterns from earlier lessons:

```text
gateway -> channel -> transform -> route -> handler
```

That is deliberate. Lesson 13 needs a small flow to test, but the main topic is
the test code in `src/test/groovy`.

The new ideas are:

- testing `Lesson13OrderReviewRules` as plain Java without Spring
- testing `Lesson13TestingFlow` through the real gateway and real channels
- knowing what each test proves and what it does not prove
- avoiding one giant end-to-end test for every small rule

So if the flow looks familiar, that is correct. The lesson is asking a different
question:

```text
Not: "How do I build a new kind of flow?"
But: "How much of the flow do I need to start in order to test this behavior?"
```

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

A component test checks the order review rules without starting Spring. A flow
test sends the same kind of order through the gateway and verifies that the
message reaches the approved, manual-review, or rejected branch.

## Files In This Lesson

```text
lesson13_testing_flows/
|-- README.md
|-- package-info.java
|-- config/
|   `-- Lesson13ChannelConfiguration.java
|-- flow/
|   `-- Lesson13TestingFlow.java
|-- gateway/
|   `-- Lesson13OrderReviewGateway.java
|-- handler/
|   `-- Lesson13OrderReviewRules.java
|-- model/
|   |-- Lesson13OrderDraft.java
|   |-- Lesson13OrderReviewResult.java
|   |-- Lesson13ReviewDecision.java
|   `-- Lesson13ReviewedOrder.java
`-- support/
    `-- Lesson13Channels.java
```

Test mirror:

```text
src/test/groovy/io/github/fengyanglin09/springintegrationlab/lessons/lesson13_testing_flows/
|-- Lesson13OrderReviewRulesSpec.groovy
`-- Lesson13TestingFlowsSpec.groovy
```

## Code Walkthrough

Production flow used as the test target:

```text
Lesson13OrderReviewGateway.review(draft)
    -> lesson13OrderReviewRequests
    -> transform draft into reviewed order
    -> route by Lesson13ReviewDecision
    -> branch handler returns Lesson13OrderReviewResult
```

This is ordinary Spring Integration flow code. It exists so the lesson has
something concrete to test.

Component test path:

```text
new Lesson13OrderReviewRules()
    -> normalize(order draft)
    -> assert the decision directly
```

This test does not start Spring. It is fast and focused, but it does not prove
that channels, gateways, transformers, or routers are wired correctly.

Full flow test path:

```text
Lesson13OrderReviewGateway.review(draft)
    -> lesson13OrderReviewRequests
    -> transform draft into reviewed order
    -> route by Lesson13ReviewDecision
    -> branch handler returns Lesson13OrderReviewResult
```

This test starts Spring and proves the message can move through the real flow.
It is broader than the component test, so use it for wiring behavior rather than
every small business-rule detail.

## What To Notice In The Code

- `Lesson13OrderReviewRulesSpec` has no `@SpringBootTest`; it tests ordinary
  Java behavior directly.
- `Lesson13TestingFlowsSpec` has `@SpringBootTest`; it starts the application
  context and calls the real gateway.
- `@SpringIntegrationTest` enables Spring Integration test support for the test
  context. In this lesson we use the real flow, but the same annotation is where
  future tests can use Spring Integration testing tools for replacing or
  controlling endpoints.
- The full flow tests assert the result and `reviewTrail`, not private Spring
  Integration internals.
- The trail values make message movement visible without requiring a debugger.

## Testing Decision Guide

Use a component test when:

- the logic is ordinary Java
- there is no need to prove channel or endpoint wiring
- a failure should point directly to one rule or method

Use a full flow test when:

- the behavior depends on Spring Integration wiring
- you need to prove a gateway, channel, transformer, router, or handler are
  connected correctly
- the test should behave like application code calling the flow

## Official Docs

- [Testing support](https://docs.spring.io/spring-integration/reference/testing.html)
