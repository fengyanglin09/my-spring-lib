# Lesson 10: Queries

Lesson 10 introduces DRL queries.

Rules can create facts that are not part of the original request or result. In
this lesson, rules create `Lesson10DiscountAudit` facts:

```drl
audits.add(new Lesson10DiscountAudit("Large order discount", "Applied large order discount"));
```

After rules finish, Java asks Drools for those audit facts:

```java
QueryResults queryResults = instance.executeQuery("Find all discount audits");
```

## Endpoint

```text
POST /api/rules-engine/lessons/10/discount
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
  "auditMessages": [
    "Applied large order discount"
  ]
}
```

## DRL Query

The query is defined at the bottom of the DRL file:

```drl
query "Find all discount audits"
    $audit : /audits
end
```

This means:

- look in the `audits` store
- return each matching audit fact
- expose it to Java as `$audit`

## Java Query Code

The service runs the query:

```java
QueryResults queryResults = instance.executeQuery("Find all discount audits");

for (QueryResultsRow row : queryResults) {
    Lesson10DiscountAudit audit = (Lesson10DiscountAudit) row.get("$audit");
    result.getAuditMessages().add(audit.getMessage());
}
```

## Mental Model

The result object is what the API returns.

Audit facts are extra notes created during rule execution.

The query is how Java says:

```text
Drools, show me all the audit notes created during this run.
```
