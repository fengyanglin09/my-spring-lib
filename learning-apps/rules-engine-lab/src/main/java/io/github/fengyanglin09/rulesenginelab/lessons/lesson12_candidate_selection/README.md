# Lesson 12: Candidate Selection

Lesson 11 created candidate facts and returned all of them.

Lesson 12 adds the next step:

```text
rules create candidates -> Java selects the best candidate
```

In this lesson, "best" means the candidate with the highest
`discountPercent`.

## Endpoint

```text
POST /api/rules-engine/lessons/12/discount
```

Try:

```json
{
  "customerType": "VIP",
  "orderAmount": 600
}
```

Expected idea:

```json
{
  "valid": true,
  "discountPercent": 15,
  "reason": "Large order",
  "selectedCandidate": {
    "ruleName": "Large order candidate",
    "discountPercent": 15,
    "reason": "Large order"
  },
  "candidates": [
    {
      "ruleName": "Large order candidate",
      "discountPercent": 15,
      "reason": "Large order"
    },
    {
      "ruleName": "VIP candidate",
      "discountPercent": 10,
      "reason": "VIP customer"
    }
  ]
}
```

## Important Pattern

The DRL still creates candidates:

```drl
candidates.add(new Lesson12DiscountCandidate(
    "Large order candidate",
    $config.getLargeOrderPercent(),
    "Large order"
));
```

The service queries candidates:

```java
QueryResults queryResults = instance.executeQuery("Find all discount candidates");
```

Then Java selects the highest-percent candidate:

```java
Optional<Lesson12DiscountCandidate> bestCandidate = result.getCandidates().stream()
        .max(Comparator.comparingInt(Lesson12DiscountCandidate::getDiscountPercent));
```

## Why Select In Java?

For this lesson, Java selection keeps the idea simple:

```text
Drools proposes possible business decisions.
Java applies a clear selection policy.
```

This is often easier to test and explain than hiding selection inside firing
order or activation groups.

Later, you can experiment with making the selection itself rule-driven.
