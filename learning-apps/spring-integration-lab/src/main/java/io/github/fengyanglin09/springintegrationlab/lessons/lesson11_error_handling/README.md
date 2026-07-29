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

A payment authorization handler may throw a business exception. One gateway lets
the exception propagate to the caller. Another gateway sends the exception to a
lesson-specific error channel, where an error flow maps it into a declined
payment result.

## Files In This Lesson

```text
lesson11_error_handling/
|-- README.md
|-- package-info.java
|-- config/
|   `-- Lesson11ChannelConfiguration.java
|-- flow/
|   `-- Lesson11PaymentFlows.java
|-- gateway/
|   |-- Lesson11RecoveringPaymentGateway.java
|   `-- Lesson11ThrowingPaymentGateway.java
|-- handler/
|   |-- Lesson11PaymentAuthorizer.java
|   `-- Lesson11PaymentErrorMapper.java
|-- model/
|   |-- Lesson11PaymentAuthorizationException.java
|   |-- Lesson11PaymentRequest.java
|   `-- Lesson11PaymentResult.java
`-- support/
    `-- Lesson11Channels.java
```

Test mirror:

```text
src/test/groovy/io/github/fengyanglin09/springintegrationlab/lessons/lesson11_error_handling/
`-- Lesson11ErrorHandlingSpec.groovy
```

## Code Walkthrough

Normal successful path:

```text
Lesson11RecoveringPaymentGateway.authorize(request)
    -> lesson11PaymentRequests
    -> Lesson11PaymentAuthorizer.authorize(request)
    -> Lesson11PaymentResult approved reply
```

Default failure path:

```text
Lesson11ThrowingPaymentGateway.authorize(request)
    -> lesson11PaymentRequests
    -> Lesson11PaymentAuthorizer.authorize(request) throws
    -> exception is thrown back to the caller
```

Recovered failure path:

```text
Lesson11RecoveringPaymentGateway.authorize(request)
    -> lesson11PaymentRequests
    -> Lesson11PaymentAuthorizer.authorize(request) throws
    -> gateway sends ErrorMessage to lesson11PaymentErrors
    -> Lesson11PaymentErrorMapper maps Throwable to Lesson11PaymentResult
    -> caller receives declined result instead of catching an exception
```

## What To Notice In The Code

- `Lesson11ThrowingPaymentGateway` has no `errorChannel`, so it uses the
  default gateway behavior: downstream exceptions are thrown to the caller.
- `Lesson11RecoveringPaymentGateway` has
  `@MessagingGateway(errorChannel = Lesson11Channels.PAYMENT_ERRORS)`, so
  downstream exceptions are sent to the lesson-specific error channel.
- The normal flow does not catch exceptions directly. It lets the gateway
  choose the error behavior.
- The error flow receives an `ErrorMessage`. Its payload is a `Throwable`.
- The error mapper turns that `Throwable` into the lesson's normal reply type:
  `Lesson11PaymentResult`.
- If an error flow returns a normal payload, the gateway caller receives that
  payload as the method return value.

## Production Note

This lesson focuses on exception-to-failure-reply mapping. Production systems
often combine this with retries, alerting, dead-letter channels, metrics, and
careful rules for which errors should be recovered versus thrown.

## Official Docs

- [Error Handling](https://docs.spring.io/spring-integration/reference/error-handling.html)
- [Messaging Gateway Error Handling](https://docs.spring.io/spring-integration/reference/7.0/gateway.html#error-handling)
