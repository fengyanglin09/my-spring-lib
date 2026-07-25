# Lesson 05: Flows

## What This Solves

Understand how channels and endpoints are composed into one readable integration
pipeline.

## Mental Model

A flow is a wiring diagram. It shows where messages enter, which stations they
visit, and where they leave.

## Core Vocabulary

- Integration flow
- Java DSL
- Input channel
- Output channel
- Sub-flow
- Gateway flow
- Flow composition

## Concept Map

```text
source
    -> channel
    -> transform
    -> route
    -> handle
    -> target
```

## Main Ideas

- A flow expresses message movement and processing steps.
- The Java DSL is a fluent way to define flows in Spring configuration.
- Flows should reveal intent without burying business rules in wiring.
- Flow boundaries should stay small enough to reason about.

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

An inbound file is read, transformed into orders, routed by customer type, and
sent to the correct downstream channel.

## Official Docs

- [Java DSL](https://docs.spring.io/spring-integration/reference/dsl.html)
- [Working With Message Flows](https://docs.spring.io/spring-integration/reference/dsl/java-flows.html)
