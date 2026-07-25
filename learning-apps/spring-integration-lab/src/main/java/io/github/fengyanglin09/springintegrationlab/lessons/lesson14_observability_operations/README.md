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

An operations dashboard shows message rates, failed message counts, active
endpoints, and the integration graph for a file-to-HTTP order flow.

## Official Docs

- [System Management](https://docs.spring.io/spring-integration/reference/system-management.html)
- [Integration Graph](https://docs.spring.io/spring-integration/reference/graph.html)
