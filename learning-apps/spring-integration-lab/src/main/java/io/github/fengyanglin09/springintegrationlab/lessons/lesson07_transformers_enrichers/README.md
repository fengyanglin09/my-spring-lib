# Lesson 07: Transformers And Enrichers

## What This Solves

Understand how a flow changes message data or adds context without mixing those
concerns into routing or transport code.

## Mental Model

A transformer rewrites the letter. An enricher adds labels, attachments, or
extra context to the envelope.

## Core Vocabulary

- Transformer
- Header enricher
- Content enricher
- Claim check
- Codec
- Payload conversion
- Message shape

## Concept Map

```text
original message
    -> transformer or enricher
    -> reshaped message
```

## Main Ideas

- Transformers change payload shape.
- Header enrichers add or change metadata.
- Content enrichers add data from another source.
- Claim check can replace large payloads with a reference.

## Decision Rules

- Use a transformer when the payload type or structure changes.
- Use a header enricher when delivery metadata changes.
- Use a content enricher when the payload needs extra data from another source.
- Avoid burying routing decisions inside transformers.

## Common Traps

- Mixing transformation and routing in one step.
- Adding business state to headers because it is convenient.
- Forgetting that downstream endpoints depend on the new message shape.

## How This Connects

Transformation prepares messages for routers, handlers, aggregators, and
adapters. Good shaping keeps later steps simple.

## Reference Checklist

- [ ] Can I tell payload transformation from header enrichment?
- [ ] Can I explain content enrichment?
- [ ] Can I identify the message shape before and after the step?

## Mini Scenario

A CSV line becomes an order object, then a customer-tier header is added before
the message reaches a router.

## Official Docs

- [Message Transformation](https://docs.spring.io/spring-integration/reference/transformer.html)
