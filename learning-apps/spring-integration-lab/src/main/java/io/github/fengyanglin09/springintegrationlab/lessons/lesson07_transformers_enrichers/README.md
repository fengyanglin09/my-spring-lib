# Lesson 07: Transformers And Enrichers

## What This Solves

Understand how a flow changes message data or adds context without mixing those
concerns into routing or transport code.

## Mental Model

A transformer rewrites the letter. An enricher adds labels, attachments, or
extra context to the envelope.

More precisely:

- Payload transformation changes the message body.
- Payload enrichment returns a richer message body with more business data.
- Header enrichment adds metadata around the body without changing the body.

## Core Vocabulary

- Transformer
- Header enricher
- Content enricher
- Payload enrichment
- Payload conversion
- Message shape
- Message headers

## Concept Map

```text
raw payload
    -> transformer changes payload shape
    -> transformer enriches payload with more business data
    -> header enricher adds metadata
    -> handler uses payload and headers
```

## Main Ideas

- Transformers change payload shape.
- Header enrichers add or change metadata.
- Content enrichers add data from another source.
- Enrichment is still a kind of transformation, but the intent is "add missing
  context" rather than "convert this into a totally different form."
- Claim check can replace large payloads with a reference.
- A transformer should return a real value. If you want to drop a message, use a
  filter instead of returning `null` from a transformer.

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

## Files

- `support/Lesson07Channels.java`: shared channel bean names.
- `support/Lesson07Headers.java`: lesson-specific message header names.
- `support/Lesson07CustomerProfileCatalog.java`: tiny customer lookup used for enrichment.
- `config/Lesson07ChannelConfiguration.java`: explicit input channel.
- `gateway/Lesson07ShapeGateway.java`: typed entry point into the lesson flow.
- `model/Lesson07OrderDraft.java`: payload after raw CSV parsing.
- `model/Lesson07CustomerProfile.java`: extra customer data used for enrichment.
- `model/Lesson07CustomerOrder.java`: payload after customer profile enrichment.
- `model/Lesson07ShapeReport.java`: final reply payload.
- `handler/Lesson07OrderShapeTransformer.java`: payload transformation and enrichment methods.
- `handler/Lesson07ShapeReporter.java`: builds the final report from payload and headers.
- `flow/Lesson07TransformerEnricherFlow.java`: connects transformer, header enricher, and handler.
- `Lesson07TransformersEnrichersSpec.groovy`: proves payload and header shaping.

## Code Walkthrough

```text
Lesson07ShapeGateway.shape(...)
    -> lesson07RawOrderLines channel
    -> transform String CSV into Lesson07OrderDraft
    -> transform/enrich Lesson07OrderDraft into Lesson07CustomerOrder
    -> enrich headers with lesson name, value band, and shape stage
    -> handle Lesson07CustomerOrder plus headers
    -> Lesson07ShapeReport returned to the gateway caller
```

The first transform changes the payload shape:

```java
.transform(String.class, rawLine -> transformer.parseRawLine(rawLine))
```

Read that as:

```text
The payload is currently a String.
Call parseRawLine(rawLine).
Continue with the returned Lesson07OrderDraft as the new payload.
```

The second transform enriches the payload:

```java
.transform(Lesson07OrderDraft.class, draft -> transformer.addCustomerProfile(draft))
```

Read that as:

```text
The payload is currently Lesson07OrderDraft.
Look up customer profile data.
Return Lesson07CustomerOrder, which contains the original order data plus
customer tier and region.
```

The header enricher adds metadata without changing the payload:

```java
.enrichHeaders(headers -> headers
        .header(Lesson07Headers.LESSON_NAME, "lesson07-transformers-enrichers")
        .headerFunction(Lesson07Headers.VALUE_BAND, message -> ...)
        .header(Lesson07Headers.SHAPE_STAGE, "headers-enriched"))
```

Read that as:

```text
Keep the same Lesson07CustomerOrder payload.
Add or update headers around that payload.
```

## Why Typed Lambdas

Earlier lessons used examples like:

```java
.handle(partnerOutbox, "send")
```

From this lesson forward, we prefer typed lambdas when they are clearer:

```java
.handle(Lesson07CustomerOrder.class, (order, headers) -> ...)
```

This avoids string method names. Java can see the payload type, and renaming a
method is less likely to leave a hidden string behind.

## Payload Versus Header

Payload is the business body:

```text
order id, customer id, customer tier, region, amount, SKU
```

Headers are metadata about the message:

```text
source system, lesson name, value band, shape stage
```

Use headers for metadata that helps the message move through the system. Keep
core business state in the payload.

## Run The Lesson Test

```bash
./mvnw -pl learning-apps/spring-integration-lab -Dtest=Lesson07TransformersEnrichersSpec test
```

## What Comes Next

Lesson 08 uses message data to choose paths with routers and filters.

## Official Docs

- [Message Transformation](https://docs.spring.io/spring-integration/reference/transformer.html)
- [Content Enricher](https://docs.spring.io/spring-integration/reference/content-enrichment.html)
- [Java DSL Transformers](https://docs.spring.io/spring-integration/reference/dsl/java-transformers.html)
