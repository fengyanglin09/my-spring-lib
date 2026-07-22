# Lesson 03: Salience

Lesson 03 shows what happens when more than one rule can match the same facts.

The new keyword is:

```drl
salience 20
```

Higher salience fires earlier. Lower salience fires later. The key thing to
remember is that salience controls order, not exclusivity.

## Endpoint

```text
POST /api/rules-engine/lessons/03/discount
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
    "VIP discount",
    "Large order discount"
  ]
}
```

The VIP rule fires first because it has higher salience:

```drl
salience 20
```

The large-order rule fires second:

```drl
salience 10
```

Since both rules modify the same result, the second rule overwrites the final
discount and reason.

## Mental Model

Think of salience like priority in a queue:

```text
higher salience -> earlier turn
lower salience  -> later turn
```

But getting an earlier turn does not remove the other rules from the queue.
That is why this lesson returns `firedRules`: it lets you see the path, not just
the final destination.

## Important Caution

Do not use salience as your main way to choose one winning discount. It can
make rules fragile because later rules may still overwrite earlier rules.

Lesson 04 introduces `activation-group`, which is better when you want only one
winner rule to fire.
