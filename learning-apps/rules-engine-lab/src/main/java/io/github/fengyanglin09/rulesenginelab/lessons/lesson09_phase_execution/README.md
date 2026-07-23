# Lesson 09: Phase Execution

Lesson 09 splits rule execution into two phases:

```text
1. VALIDATION
2. DISCOUNT
```

Previous lessons used salience to make validation rules run before discount
rules in one `instance.fire()` call. That works, but phase execution is more
explicit.

## Endpoint

```text
POST /api/rules-engine/lessons/09/discount
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
  "matchedRules": [
    "Large order discount",
    "VIP discount"
  ],
  "firedRules": [
    "Large order discount"
  ]
}
```

## Important Pattern

The service inserts a context fact:

```java
Lesson09RuleExecutionContext context =
        new Lesson09RuleExecutionContext(Lesson09RulePhase.VALIDATION);

DataHandle contextHandle = ruleUnit.getContexts().add(context);
```

The first fire runs validation rules:

```java
instance.fire();
```

If validation succeeds, the service changes the phase:

```java
context.setPhase(Lesson09RulePhase.DISCOUNT);
ruleUnit.getContexts().update(contextHandle, context);
instance.fire();
```

The `update(...)` call matters. The context object changed, and Drools needs to
be notified so it can re-evaluate rules that depend on `/contexts`.

## DRL Pattern

Validation rules check:

```drl
$context : /contexts[ phase == Lesson09RulePhase.VALIDATION ]
```

Discount rules check:

```drl
$context : /contexts[ phase == Lesson09RulePhase.DISCOUNT ]
```

## Mental Model

Think of the context fact as a traffic signal for the rule engine:

```text
VALIDATION light -> only validation rules may move
DISCOUNT light   -> only discount rules may move
```

The service changes the signal between `fire()` calls.
