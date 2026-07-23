# Lesson 13: Accumulate

Lesson 13 uses Drools `accumulate` to calculate a cart total from many item
facts.

Earlier lessons usually had one request fact with one `orderAmount`. This
lesson has many item facts:

```text
item 1 -> line total
item 2 -> line total
item 3 -> line total
```

Drools sums those line totals.

## Endpoint

```text
POST /api/rules-engine/lessons/13/discount
```

Try:

```json
{
  "customerType": "GUEST",
  "items": [
    {
      "sku": "DESK",
      "unitPriceCents": 30000,
      "quantity": 1
    },
    {
      "sku": "CHAIR",
      "unitPriceCents": 25000,
      "quantity": 1
    }
  ]
}
```

Expected idea:

```json
{
  "valid": true,
  "totalCalculated": true,
  "totalCents": 55000,
  "discountPercent": 15,
  "reason": "Large cart total"
}
```

## Service Pattern

The service inserts each cart item as its own fact:

```java
for (Lesson13CartItem item : request.getItems()) {
    ruleUnit.getItems().add(item);
}
```

The service does not calculate the total.

## DRL Accumulate Pattern

The DRL calculates the total:

```drl
$total : Number() from accumulate(
    $item : /items,
    sum($item.lineTotalCents)
)
```

This means:

- look at every fact in `/items`
- for each item, read `lineTotalCents`
- sum those values
- bind the result to `$total`

## Mental Model

Normal rule matching asks:

```text
Does this one fact match?
```

`accumulate` asks:

```text
Across all matching facts, what value can we calculate?
```

In this lesson, that calculated value is the cart total.
