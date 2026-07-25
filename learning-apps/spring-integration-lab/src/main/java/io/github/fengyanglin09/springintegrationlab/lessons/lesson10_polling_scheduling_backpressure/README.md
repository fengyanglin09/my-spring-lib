# Lesson 10: Polling, Scheduling, And Back Pressure

## What This Solves

Understand who controls the pace of message processing and how buffering affects
throughput, latency, and failure behavior.

## Mental Model

Polling is a worker checking a mailbox on a schedule. Back pressure is what
happens when mail arrives faster than the worker can process it.

## Core Vocabulary

- Poller
- Trigger
- Fixed rate
- Fixed delay
- `PollableChannel`
- Queue capacity
- Task scheduler
- Back pressure

## Concept Map

```text
source or queue
    -> poller checks on schedule
    -> endpoint handles limited messages
```

## Main Ideas

- Pollers control when work is pulled.
- Queue capacity determines how much buffering is allowed.
- Fixed rate and fixed delay express different pacing semantics.
- Async processing can improve throughput but complicates ordering and errors.

## Decision Rules

- Use polling when a source must be checked or a queue must be drained.
- Set limits intentionally: interval, max messages, capacity, and timeout.
- Treat unbounded queues as a risk unless there is a strong reason.
- Decide whether latency or load smoothing matters more.

## Common Traps

- Polling too frequently and creating noisy idle work.
- Polling too slowly and hiding latency.
- Adding a queue without deciding what happens when it fills.

## How This Connects

Polling affects endpoint behavior, channel choice, error handling, transactions,
and operational tuning.

## Reference Checklist

- [ ] Can I identify who controls the pace?
- [ ] Can I explain what happens when messages arrive too fast?
- [ ] Can I justify the polling interval and batch size?

## Mini Scenario

A file directory is checked every five seconds, with a maximum number of files
handled per poll so the service does not overload downstream systems.

## Official Docs

- [Poller](https://docs.spring.io/spring-integration/reference/polling-consumer.html)
- [Message Channel Implementations](https://docs.spring.io/spring-integration/reference/channel/implementations.html)
