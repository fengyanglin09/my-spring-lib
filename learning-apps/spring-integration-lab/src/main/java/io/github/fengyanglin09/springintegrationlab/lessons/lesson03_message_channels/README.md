# Lesson 03: Message Channels

## What This Solves

Understand how producers and consumers are decoupled so they do not need direct
references to each other.

## Mental Model

A channel is a conveyor belt. A producer places a message on it; one or more
consumers receive from it depending on the channel type.

## Core Vocabulary

- `MessageChannel`
- `DirectChannel`
- `QueueChannel`
- `PublishSubscribeChannel`
- `ExecutorChannel`
- `PollableChannel`
- `SubscribableChannel`

## Concept Map

```text
Producer
    -> channel
    -> consumer endpoint
```

## Main Ideas

- Channels connect message producers to message consumers.
- Some channels hand off in the same thread.
- Some channels buffer messages.
- Some channels fan out to multiple subscribers.
- Channel choice changes throughput, ordering, and error behavior.

## Decision Rules

- Use direct handoff when the work is simple and synchronous.
- Use queue-style thinking when buffering or pacing matters.
- Use publish-subscribe when multiple independent consumers need the same message.
- Be cautious with async channels because error handling becomes message-based.

## Common Traps

- Assuming every channel implies an external queue or broker.
- Adding a queue to hide slow processing without understanding back pressure.
- Forgetting that async handoff changes the call stack and exception behavior.

## How This Connects

Messages travel over channels. Endpoints attach to channels. Error handling and
polling depend heavily on the channel type.

## Reference Checklist

- [ ] Can I explain why channels decouple components?
- [ ] Can I compare direct, queue, and publish-subscribe behavior?
- [ ] Can I explain what async changes?

## Mini Scenario

A payment event is sent to a publish-subscribe channel so audit logging,
receipt creation, and notification can each react independently.

## Official Docs

- [Message Channels](https://docs.spring.io/spring-integration/reference/channel.html)
