# Lesson 11: Error Handling

## What This Solves

Understand how exceptions behave differently in synchronous and asynchronous
message flows.

## Mental Model

In a direct call, an exception can travel back up the call stack. In an async
flow, the sender has already walked away, so the error must be delivered as a
message.

## Core Vocabulary

- `ErrorMessage`
- `errorChannel`
- Error channel header
- Global error channel
- Retry
- Recovery
- Same-thread flow
- Async flow

## Concept Map

```text
sync flow: sender -> handler -> exception returns to sender

async flow: sender -> channel -> handler fails -> ErrorMessage -> errorChannel
```

## Main Ideas

- Synchronous failures can propagate like normal Java exceptions.
- Asynchronous failures are represented as error messages.
- A message can specify its own error channel.
- A global `errorChannel` acts as the fallback destination.

## Decision Rules

- First identify whether the failing handler runs in the sender thread.
- Use local error routing for flow-specific recovery.
- Use global error handling for shared logging or fallback behavior.
- Be explicit about retry and dead-letter style decisions.

## Common Traps

- Expecting async exceptions to return to the original caller.
- Handling all errors globally when the flow needs context-specific recovery.
- Adding async channels without revisiting error behavior.

## How This Connects

Error handling depends on message headers, channel type, pollers, gateways,
transactions, and operational visibility.

## Reference Checklist

- [ ] Can I tell whether an exception returns directly or becomes an error message?
- [ ] Can I describe how the error channel is chosen?
- [ ] Can I explain the recovery path for a failed message?

## Mini Scenario

A payment submission fails in an async outbound step, so the flow sends an
`ErrorMessage` to a payment error channel that records the failure and schedules retry.

## Official Docs

- [Error Handling](https://docs.spring.io/spring-integration/reference/error-handling.html)
