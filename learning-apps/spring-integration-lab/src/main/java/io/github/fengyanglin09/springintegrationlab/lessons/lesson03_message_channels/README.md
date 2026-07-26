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

## Files

- `support/Lesson03Channels.java`: shared channel bean names.
- `config/Lesson03ChannelConfiguration.java`: explicit `DirectChannel` and `PublishSubscribeChannel` beans.
- `model/Lesson03DeliveryRequest.java`: payload sent through both channel examples.
- `model/Lesson03DirectDeliveryReport.java`: reply from the direct-channel example.
- `model/Lesson03BroadcastReceipt.java`: record of one broadcast subscriber receiving a message.
- `gateway/Lesson03ChannelGateway.java`: typed methods for direct send and broadcast send.
- `flow/Lesson03ChannelFlows.java`: connects each channel to its subscribers.
- `handler/Lesson03DirectOrderHandler.java`: single direct-channel subscriber.
- `handler/Lesson03BroadcastRecorder.java`: two subscriber methods used by the publish-subscribe example.
- `Lesson03MessageChannelsSpec.groovy`: proves direct handoff and fan-out behavior.

## Code Walkthrough

Lessons 01 and 02 used channel names and let Spring Integration create default
channels. Lesson 03 defines the channels explicitly because the channel type is
the lesson.

```text
DirectChannel path:

Lesson03ChannelGateway.sendDirect(...)
    -> lesson03DirectOrders DirectChannel
    -> Lesson03DirectOrderHandler.handle(...)
    -> Lesson03DirectDeliveryReport returned to the gateway caller
```

`DirectChannel` is point-to-point. One message goes to one subscribed handler.
It does not store messages for later. When a message is sent, the subscribed
handler is called immediately in the same thread that sent the message.

```text
PublishSubscribeChannel path:

Lesson03ChannelGateway.broadcast(...)
    -> lesson03BroadcastEvents PublishSubscribeChannel
        -> Lesson03BroadcastRecorder.recordAudit(...)
        -> Lesson03BroadcastRecorder.recordNotification(...)
```

`PublishSubscribeChannel` broadcasts one message to every subscriber. The
gateway method returns `void` because there is no single reply. This is event
style: "tell everyone who cares," not "ask one component for an answer."

## Run The Lesson Test

```bash
./mvnw -pl learning-apps/spring-integration-lab -Dtest=Lesson03MessageChannelsSpec test
```

## What Comes Next

Lesson 04 moves from channels to endpoints: the things that subscribe to
channels and actually handle, transform, route, filter, or adapt messages.

## Official Docs

- [Message Channels](https://docs.spring.io/spring-integration/reference/channel.html)
- [Message Channel Implementations](https://docs.spring.io/spring-integration/reference/channel/implementations.html)
