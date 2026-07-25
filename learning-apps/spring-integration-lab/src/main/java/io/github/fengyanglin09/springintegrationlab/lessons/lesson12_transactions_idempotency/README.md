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

An invoice message may be retried after a timeout. The flow uses an invoice ID
as an idempotency key so the downstream system is not charged twice.

## Official Docs

- [Transaction Support](https://docs.spring.io/spring-integration/reference/transactions.html)
- [Idempotent Receiver Enterprise Integration Pattern](https://docs.spring.io/spring-integration/reference/handler-advice/idempotent-receiver.html)
