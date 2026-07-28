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

A small work intake accepts messages into a bounded queue. A polling consumer is
started later and drains at most two messages per poll.

## Files In This Lesson

```text
lesson10_polling_scheduling_backpressure/
|-- README.md
|-- package-info.java
|-- config/
|   `-- Lesson10ChannelConfiguration.java
|-- flow/
|   `-- Lesson10PollingBackpressureFlow.java
|-- handler/
|   `-- Lesson10WorkRecorder.java
|-- model/
|   |-- Lesson10EnqueueResult.java
|   |-- Lesson10ProcessedWork.java
|   |-- Lesson10QueueSnapshot.java
|   `-- Lesson10WorkItem.java
`-- support/
    |-- Lesson10Channels.java
    |-- Lesson10EndpointIds.java
    |-- Lesson10Headers.java
    `-- Lesson10WorkIntake.java
```

Test mirror:

```text
src/test/groovy/io/github/fengyanglin09/springintegrationlab/lessons/lesson10_polling_scheduling_backpressure/
`-- Lesson10PollingSchedulingBackpressureSpec.groovy
```

## Code Walkthrough

```text
Lesson10WorkIntake.submit(workItem)
    -> tries to send one message to the bounded QueueChannel
    -> send(message, 0) returns false immediately when the queue is full

Lesson10PollingBackpressureFlow
    -> polls the QueueChannel on a schedule
    -> handles at most 2 messages per poll
    -> records each processed message
```

## What To Notice In The Code

- `QueueChannel(3)` means at most three messages can wait in the channel.
- `send(message, 0)` means "try now, but do not wait if the queue is full."
- `send(...)` returns `true` when the queue accepts the message and `false`
  when it cannot accept the message within the timeout.
- A `QueueChannel` is a Spring Integration channel and is visible in the flow.
  This is different from the Lesson 09 executor's internal task queue.
- The polling consumer is stopped by default in the test so the queue can fill
  predictably before polling starts.
- `maxMessagesPerPoll(2)` means one poll drains at most two messages, even if
  more messages are waiting.
- `fixedDelay(...)` measures the wait time after one poll finishes before the
  next poll starts.

## Production Note

This lesson uses small numbers so the behavior is easy to see. Production
systems should choose queue capacity, send timeout, poll interval, and
`maxMessagesPerPoll` based on downstream capacity and failure behavior.

## Official Docs

- [Poller](https://docs.spring.io/spring-integration/reference/polling-consumer.html)
- [Pollers Java DSL](https://docs.spring.io/spring-integration/api/org/springframework/integration/dsl/Pollers.html)
- [MessageChannel Interface](https://docs.spring.io/spring-integration/reference/channel/interfaces.html)
- [Message Channel Implementations](https://docs.spring.io/spring-integration/reference/channel/implementations.html)
