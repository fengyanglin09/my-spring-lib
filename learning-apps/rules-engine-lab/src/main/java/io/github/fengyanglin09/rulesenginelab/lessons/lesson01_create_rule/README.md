# Lesson 01: Create Rule

## Goal

Learn the smallest useful Drools Rule Unit flow:

1. Java creates input and output facts.
2. Java inserts those facts into a rule unit.
3. Drools fires one DRL rule.
4. Java returns the modified result.

This lesson intentionally avoids validation, config, tracing, salience, and
multiple rules. It is the first brick.

## Files

- `api/Lesson01DiscountController.java`: exposes the lesson endpoint.
- `model/Lesson01DiscountRequest.java`: input fact.
- `model/Lesson01DiscountResult.java`: output fact.
- `rules/Lesson01DiscountRuleUnit.java`: owns the Rule Unit data stores.
- `service/Lesson01DiscountService.java`: creates the rule unit and fires rules.
- `Lesson01DiscountRuleUnit.drl`: contains the single rule.
- `Lesson01DiscountServiceSpec.groovy`: proves the lesson behavior.

## Endpoint

```text
POST /api/rules-engine/lessons/01/discount
```

Request:

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

## Rule

```drl
rule "VIP customer discount"
when
    $request : /requests[ customerType == "VIP" ]
    $result : /results
then
    modify($result) {
        setDiscountPercent(10),
        setReason("VIP customer")
    }
end
```

## Mental Model

Think of the rule unit as a tiny workspace:

```text
requests  -> facts Drools can inspect
results   -> facts Drools can modify
```

The service puts one request and one result into that workspace, then calls:

```java
instance.fire();
```

Drools checks the `when` condition. If the request has `customerType == "VIP"`,
the rule runs and modifies the result.

## Things To Notice

- The DRL file uses `unit Lesson01DiscountRuleUnit;`.
- The Java class `Lesson01DiscountRuleUnit` implements `RuleUnitData`.
- The DRL package must match the Java rule-unit package.
- The non-VIP case returns the default result because no rule matches.

## Run The Lesson Test

```bash
./mvnw -pl learning-apps/rules-engine-lab -Dtest=Lesson01DiscountServiceSpec test
```

## What Comes Next

Lesson 02 adds more rules. That is where we start seeing multiple possible
matches and why rule organization begins to matter.
