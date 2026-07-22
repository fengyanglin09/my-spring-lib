# Lesson 04: Activation Group

Lesson 03 showed that salience controls order, but all satisfied rules can
still run. Lesson 04 introduces:

```drl
activation-group "discount-winner"
```

Rules in the same activation group behave like a one-winner set. When one rule
in the group fires, Drools cancels the other pending activations in that group.

## Endpoint

```text
POST /api/rules-engine/lessons/04/discount
```

Try this request:

```json
{
  "customerType": "VIP",
  "orderAmount": 600
}
```

Expected response:

```json
{
  "discountPercent": 15,
  "reason": "Large order",
  "firedRules": [
    "Large order discount"
  ]
}
```

Both the VIP rule and the large-order rule match, but only the large-order rule
fires because:

- both rules are in `activation-group "discount-winner"`
- the large-order rule has higher salience
- after it fires, Drools cancels the VIP activation in the same group

## Mental Model

Use salience when you care about order:

```text
many matching rules -> run in priority order
```

Use activation group when you care about one winner:

```text
many matching rules -> first one fires -> rest of group is canceled
```

## Caution

An activation group only affects rules in the same group. Rules outside the
group can still fire normally. That makes it useful for a specific category of
mutually exclusive decisions, such as choosing exactly one discount.
