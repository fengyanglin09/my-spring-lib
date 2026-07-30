# Lesson 14: Observability And Operations

## What This Solves

Understand how to inspect, monitor, control, and safely operate integration flows
after they are running.

## Mental Model

Operations are the control room. You need a map of the conveyor lines, counters
for throughput, history for tracing, and controls for stopping or adjusting flow.

## Core Vocabulary

- Message history
- Integration graph
- Metrics
- JMX
- Control bus
- Message store
- Metadata store
- Orderly shutdown

## Concept Map

```text
running flow
    -> history and metrics
    -> graph and controls
    -> operational decisions
```

## Main Ideas

- Message history can help trace where a message has been.
- A wire tap can send a copy of a message to an observation flow.
- Integration graph exposes the flow topology.
- Metrics reveal throughput, latency, and failure signals.
- Control bus and management features can adjust runtime behavior.

## Decision Rules

- Add observability before production troubleshooting forces it.
- Track enough message identity to diagnose failures without leaking sensitive data.
- Use controls carefully; runtime changes should be visible and intentional.
- Include shutdown behavior in operational design.

## Common Traps

- Having many channels and no way to trace message movement.
- Logging full payloads that may contain sensitive data.
- Treating operational controls as debugging toys instead of production tools.

## How This Connects

Observability ties together channels, endpoints, errors, pollers, stores, and
adapter boundaries.

## Reference Checklist

- [ ] Can I trace a message through the flow?
- [ ] Can I see which endpoints are active?
- [ ] Can I explain how the flow shuts down?

## Mini Scenario

A shipment flow dispatches events normally. A wire tap records a copy of each
shipment update for operations. A control-bus command can stop or start that
recording without stopping the shipment flow itself.

## What Is New Here

The new Spring Integration ideas in this lesson are:

- `wireTap(...)`: observe a copy of a message while the original continues
- `@EnableMessageHistory`: ask Spring Integration to add a message-history
  header for tracked components
- `controlBus()`: send an operational command through a message channel
- `@ManagedOperation`: mark a method as callable by management infrastructure

This lesson does not build a full observability stack. It does not add
Micrometer dashboards, Actuator endpoints, distributed tracing, or the
Integration Graph API. Those are real production topics, but they would bury the
basic idea.

The focus here is smaller:

```text
Can I observe a message path?
Can I change one operational setting at runtime?
```

## Files In This Lesson

```text
lesson14_observability_operations/
|-- README.md
|-- package-info.java
|-- config/
|   `-- Lesson14ChannelConfiguration.java
|-- flow/
|   `-- Lesson14ObservabilityOperationsFlow.java
|-- gateway/
|   |-- Lesson14OperationsGateway.java
|   `-- Lesson14ShipmentGateway.java
|-- handler/
|   |-- Lesson14ObservationRecorder.java
|   `-- Lesson14ShipmentHandler.java
|-- model/
|   |-- Lesson14ObservationRecord.java
|   |-- Lesson14ShipmentEvent.java
|   |-- Lesson14ShipmentResult.java
|   `-- Lesson14ShipmentUpdate.java
`-- support/
    `-- Lesson14Channels.java
```

Test mirror:

```text
src/test/groovy/io/github/fengyanglin09/springintegrationlab/lessons/lesson14_observability_operations/
`-- Lesson14ObservabilityOperationsSpec.groovy
```

## Code Walkthrough

Normal shipment path:

```text
Lesson14ShipmentGateway.process(event)
    -> lesson14ShipmentEvents
    -> transform event into shipment update
    -> wire tap sends a copy to lesson14ObservationEvents
    -> original message continues
    -> dispatch handler returns Lesson14ShipmentResult
```

Observation path:

```text
wire-tapped message copy
    -> lesson14ObservationEvents
    -> Lesson14ObservationRecorder.record(message)
    -> recorder stores shipment id, lane, lesson trail, and message history
```

Operational control path:

```text
Lesson14OperationsGateway.operate("lesson14ObservationRecorder.stopObservation")
    -> lesson14Operations
    -> controlBus()
    -> call Lesson14ObservationRecorder.stopObservation()
    -> return OBSERVATION_STOPPED
```

## What To Notice In The Code

- The wire tap does not replace the main flow. It sends a copy to the observer.
- Stopping observation does not stop shipment processing.
- `Lesson14ObservationRecorder` uses `@ManagedOperation` so the control bus can
  call selected methods.
- `@EnableMessageHistory("lesson14*")` limits message-history tracking to lesson
  14 components.
- Message history is useful for debugging, but it is not a substitute for
  business audit data.

## Operational Safety Note

The control bus is powerful because it can call managed operations on Spring
beans. In production, do not expose it casually. Treat it like an admin-only
control surface: secure it, log who used it, and prefer narrow operations over
general-purpose command execution.

## Official Docs

- [System Management](https://docs.spring.io/spring-integration/reference/system-management.html)
- [Message History](https://docs.spring.io/spring-integration/reference/message-history.html)
- [Wire Tap With Java DSL](https://docs.spring.io/spring-integration/reference/dsl/java-wiretap.html)
- [Control Bus](https://docs.spring.io/spring-integration/reference/control-bus.html)
- [Integration Graph](https://docs.spring.io/spring-integration/reference/graph.html)
