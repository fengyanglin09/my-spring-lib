# Lesson 02: Messages

## What This Solves

Understand the standard envelope that moves data through every Spring Integration
flow.

The core question for this lesson is:

```text
What belongs in the payload, and what belongs in headers?
```

Spring Integration can route, transform, split, aggregate, reply, and handle
errors because every step agrees on one basic shape: a message with a payload and
headers.

## Mental Model

A message is an envelope. The payload is the letter inside; headers are the
postal marks, labels, and routing hints on the outside.

```text
Payload = what the business step mainly cares about
Headers = context that helps the system deliver, correlate, trace, or handle the message
```

If the letter changes, the payload probably changes. If the route, reply address,
tracking number, priority, or error path changes, a header probably changes.

## Core Vocabulary

- `Message`: the generic container passed through Spring Integration.
- Payload: the main business data carried by the message.
- Headers: key-value metadata carried with the message.
- `MessageHeaders`: the read-only header map associated with a message.
- Message ID: unique identifier for a message instance.
- Timestamp: creation time for a message instance.
- Reply channel: where a reply should go when no explicit output channel is used.
- Error channel: where an error message should go when async error handling is needed.
- Correlation ID: metadata used to connect related messages.
- Sequence number and sequence size: metadata used when one message becomes many.

## Concept Map

```text
Message
    |
    |-- payload
    |       main domain object or value
    |
    |-- headers
            id
            timestamp
            reply channel
            error channel
            correlation id
            sequence details
            custom metadata
```

## Main Ideas

- The payload carries the main business data.
- Headers carry metadata that helps the framework route, correlate, reply, trace, and handle errors.
- Messages are designed to move without consumers needing to know the producer.
- Header values can shape framework behavior without changing the payload type.
- The message wrapper does not expose a way to replace the payload after creation.
- The header map is effectively read-only after message creation.
- The payload object and header values can still be mutable objects, so immutability is not automatic all the way down.

That last point matters. A message is like a sealed envelope, but the letter
inside might still be written on erasable paper if the payload object is mutable.
Prefer immutable payloads when the same message may travel through multiple
consumers or async boundaries.

### Payload Thinking

The payload should answer:

```text
What is this message mainly about?
```

Good payload examples:

- An order request.
- A customer-created event.
- A file descriptor.
- A payment result.
- A batch of records after aggregation.

Poor payload choices:

- A generic map with unclear meaning.
- A transport-specific object leaking out of an adapter.
- A giant object used only because every later step might need something from it.

### Header Thinking

Headers should answer:

```text
What does the messaging system or flow need to know about this message?
```

Good header examples:

- Tenant ID.
- Correlation ID.
- Source system.
- Reply channel.
- Error channel.
- File name or protocol metadata from an adapter.
- Priority or expiration metadata.

Risky header choices:

- Core business state that handlers must understand to do their job.
- Hidden flags that change behavior far away from where they are set.
- Large data structures that should be payload content or stored elsewhere.

## Decision Rules

- Put domain content in the payload.
- Put delivery, correlation, format, and operational details in headers.
- Use headers for metadata that helps the flow, not for the main business object.
- Keep custom headers few, named clearly, and documented in the lesson README.
- Prefer immutable payload types once messages cross async or fan-out boundaries.
- Do not make downstream components depend on many custom headers unless the flow is explicitly designed around them.
- If a value decides where the message goes, a header can be reasonable.
- If a value decides what the business result means, it probably belongs in the payload.

## Common Traps

- Treating headers as a second payload.
- Mutating shared payload objects and assuming message immutability protects you.
- Forgetting that reply and error routing can be header-driven.
- Letting adapter-specific headers leak deep into business handlers.
- Using a generic payload type so every handler has to inspect and cast data.
- Creating custom headers without a naming convention.
- Removing or overwriting correlation and sequence metadata before split, aggregate, or resequence steps.

## How This Connects

Channels transport messages. Endpoints consume messages. Routers,
transformers, splitters, aggregators, and adapters all operate on messages.

```text
Lesson 03: Channels move messages.
Lesson 04: Endpoints consume messages.
Lesson 07: Transformers change payloads or enrich headers.
Lesson 09: Splitters and aggregators rely on correlation and sequence headers.
Lesson 11: Error handling uses error messages and error channels.
```

## Reference Checklist

- [ ] Can I describe payload versus headers?
- [ ] Can I explain what reply and error channels are for?
- [ ] Can I identify data that should not be hidden in headers?
- [ ] Can I explain why mutable payloads can still be risky?
- [ ] Can I name common framework headers such as ID, timestamp, reply channel, and error channel?
- [ ] Can I explain correlation ID and sequence metadata at a high level?
- [ ] Can I decide whether tenant ID, file name, customer type, and order amount belong in payload or headers?

## Mini Scenario

An order message enters the system.

```text
Message
    payload:
        Order request
            order id
            customer id
            line items
            requested total

    headers:
        tenant id
        source system
        correlation id
        reply channel
        error channel
```

The validation endpoint mainly cares about the payload. The router may inspect
customer type or validation status. The framework can use reply and error
channel headers to send the result or failure to the right place.

The design smell to watch for: if every endpoint must read five custom headers
to understand the business meaning of the order, the message shape is probably
wrong. The payload should tell the business story; headers should help the
message travel.

## Official Docs

- [Message](https://docs.spring.io/spring-integration/reference/message.html)
- [MessageHeaderAccessor API](https://docs.spring.io/spring-integration/api/org/springframework/integration/IntegrationMessageHeaderAccessor.html)
