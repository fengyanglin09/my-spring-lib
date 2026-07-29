# Lesson 12: Transactions And Idempotency

## What This Solves

Understand how to reason about partial failure, duplicate delivery, retries, and
side effects in message flows.

## Mental Model

Transactions are the receipt book for a controlled handoff. Idempotency is the
stamp that says, "I have already handled this message."

## Core Vocabulary

- Transaction boundary
- Rollback
- Retry
- Idempotent receiver
- Metadata store
- Message store
- Duplicate detection
- Side effect

## Concept Map

```text
message
    -> transactional step
    -> possible retry
    -> duplicate check
    -> side effect
```

## Main Ideas

- Transactions protect work inside a defined boundary.
- Message flows often cross boundaries where one transaction cannot cover everything.
- Idempotent receivers protect downstream work from duplicate messages.
- Metadata stores can remember processed message keys.

## Decision Rules

- Define the side effect you must protect before choosing transaction behavior.
- Use idempotency when retries or duplicate delivery are realistic.
- Prefer small transaction boundaries that match real resource ownership.
- Avoid pretending a distributed flow is one simple local transaction.

## Common Traps

- Assuming retry is safe without idempotency.
- Putting too much work inside one transaction.
- Not defining a stable message key for duplicate detection.

## How This Connects

Transactions and idempotency depend on error handling, adapters, message stores,
metadata stores, and external system guarantees.

## Reference Checklist

- [ ] Can I define the transaction boundary?
- [ ] Can I say what happens on retry?
- [ ] Can I identify the idempotency key?

## Mini Scenario

A charge command may be retried after a timeout. The flow uses
`Lesson12ChargeCommand.commandId` as an idempotency key so the account is not
charged twice for the same business command.

## Files In This Lesson

```text
lesson12_transactions_idempotency/
|-- README.md
|-- package-info.java
|-- config/
|   |-- Lesson12ChannelConfiguration.java
|   `-- Lesson12IdempotencyConfiguration.java
|-- flow/
|   `-- Lesson12IdempotentChargeFlow.java
|-- gateway/
|   `-- Lesson12ChargeGateway.java
|-- handler/
|   `-- Lesson12ChargeLedger.java
|-- model/
|   |-- Lesson12ChargeCommand.java
|   |-- Lesson12ChargeResult.java
|   `-- Lesson12LedgerEntry.java
`-- support/
    |-- Lesson12Channels.java
    `-- Lesson12IdempotencyRepository.java
```

Test mirror:

```text
src/test/groovy/io/github/fengyanglin09/springintegrationlab/lessons/lesson12_transactions_idempotency/
`-- Lesson12TransactionsIdempotencySpec.groovy
```

## Code Walkthrough

First message for a command id:

```text
Lesson12ChargeGateway.charge(command)
    -> lesson12ChargeCommands
    -> idempotent receiver checks commandId
    -> commandId is new, so the message continues
    -> Lesson12ChargeLedger.applyCharge(command)
    -> one ledger entry is created
    -> caller receives CHARGED result
```

Duplicate message for the same command id:

```text
Lesson12ChargeGateway.charge(command)
    -> lesson12ChargeCommands
    -> idempotent receiver checks commandId
    -> commandId already exists, so the normal handler is skipped
    -> duplicate message goes to lesson12DuplicateChargeCommands
    -> Lesson12ChargeLedger.skipDuplicate(command)
    -> no new ledger entry is created
    -> caller receives DUPLICATE_SKIPPED result
```

## What To Notice In The Code

- `Lesson12ChargeCommand.commandId` is the idempotency key.
- `MetadataStoreSelector` checks and stores that key in a
  `ConcurrentMetadataStore`.
- `SimpleMetadataStore` is in-memory here so the lesson is easy to reset.
- `IdempotentReceiverInterceptor` is advice around the endpoint that applies
  the side effect.
- The first message reaches `applyCharge(...)`.
- A duplicate message is sent to the discard channel and reaches
  `skipDuplicate(...)`.
- The duplicate path returns a normal reply but does not write another ledger
  entry.

## Transaction Boundary Note

This lesson teaches the idempotency shape, not a real database transaction.
The metadata store and the in-memory ledger are separate in this example.

In production, be careful about when the idempotency key is recorded versus when
the side effect is committed. If possible, store the idempotency key and the
side effect in the same transactional resource, or use a status model such as
`PENDING` and `COMPLETED` so a crash between steps can be recovered safely.

## Official Docs

- [Transaction Support](https://docs.spring.io/spring-integration/reference/transactions.html)
- [Idempotent Receiver Enterprise Integration Pattern](https://docs.spring.io/spring-integration/reference/handler-advice/idempotent-receiver.html)
- [Metadata Store](https://docs.spring.io/spring-integration/reference/meta-data-store.html)
