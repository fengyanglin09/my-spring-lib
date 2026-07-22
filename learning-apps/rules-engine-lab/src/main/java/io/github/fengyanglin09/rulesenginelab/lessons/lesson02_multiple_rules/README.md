# Lesson 02: Multiple Rules

Lesson 01 had one rule. Lesson 02 keeps the same Java shape and adds three
rules to one DRL file:

- VIP customers get 10%.
- Normal customers get 5%.
- Guest customers with an order over 500 get 15%.

The goal is to see that a Rule Unit can own several rules. Think of the DRL
file like a small decision board: Drools checks each rule condition against the
facts you inserted into the Rule Unit.

## Files To Read

- `model/Lesson02DiscountRequest.java`: input fact inspected by rules.
- `model/Lesson02DiscountResult.java`: output fact modified by rules.
- `rules/Lesson02DiscountRuleUnit.java`: Java bridge that exposes `/requests`
  and `/results` to DRL.
- `Lesson02DiscountRuleUnit.drl`: the three business rules.
- `service/Lesson02DiscountService.java`: creates the Rule Unit and fires rules.
- `api/Lesson02DiscountController.java`: Swagger-friendly HTTP endpoint.
- `Lesson02DiscountServiceSpec.groovy`: executable examples for the lesson.

## Endpoint

Run the app and open Swagger, then use:

```text
POST /api/rules-engine/lessons/02/discount
```

Try a VIP customer:

```json
{
  "customerType": "VIP",
  "orderAmount": 150
}
```

Expected response:

```json
{
  "discountPercent": 10,
  "reason": "VIP customer"
}
```

Try a large guest order:

```json
{
  "customerType": "GUEST",
  "orderAmount": 600
}
```

Expected response:

```json
{
  "discountPercent": 15,
  "reason": "Large guest order"
}
```

## Important Idea

Multiple rules can read the same facts:

```drl
$request : /requests[ customerType == "VIP" ]
$result : /results
```

This means:

- Find a request fact in the `requests` store.
- Check whether it satisfies the rule condition.
- Bind it to `$request` if it matches.
- Find the result fact in the `results` store.
- Modify that result in the `then` block.

## What This Lesson Avoids

This lesson intentionally avoids overlapping matches. For example, the
large-order rule only applies to `GUEST` customers, so it will not compete with
the VIP rule.

That keeps the mental model clean for now:

```text
one matching scenario -> one rule changes the result
```

Lesson 03 will remove that safety rail and show what happens when more than one
rule can match the same request.
