# Lesson 05: Validation Rules

Lesson 05 adds validation before discount calculation.

The new ideas are:

- validation rules can mark a request invalid
- validation rules should run before discount rules
- discount rules should require `valid == true`
- null guards should appear before method calls such as `compareTo(...)`

## Endpoint

```text
POST /api/rules-engine/lessons/05/discount
```

Valid request:

```json
{
  "customerType": "VIP",
  "orderAmount": 600
}
```

Expected response:

```json
{
  "valid": true,
  "discountPercent": 15,
  "reason": "Large order",
  "validationErrors": [],
  "firedRules": [
    "Large order discount"
  ]
}
```

Invalid request:

```json
{
  "customerType": "VIP",
  "orderAmount": null
}
```

Expected response:

```json
{
  "valid": false,
  "discountPercent": 0,
  "reason": "Invalid request",
  "validationErrors": [
    "orderAmount is required"
  ],
  "firedRules": [
    "Missing order amount"
  ]
}
```

## Important Pattern

Validation rules run first because they have high salience:

```drl
salience 100
```

Discount rules require the result to still be valid:

```drl
$result : /results[ valid == true ]
```

So the flow is:

```text
validation rules run first
invalid request -> valid becomes false
discount rules require valid == true
invalid request -> discount rules do not run
```

## Null Guards

Whenever a rule calls a method on a nullable field, guard it first:

```drl
orderAmount != null,
orderAmount.compareTo(new java.math.BigDecimal("500")) > 0
```

The comma means AND. Drools only calls `compareTo(...)` when the field is not
null.

This is the same defensive idea you use in Java:

```java
if (orderAmount != null && orderAmount.compareTo(limit) > 0) {
    // safe
}
```
