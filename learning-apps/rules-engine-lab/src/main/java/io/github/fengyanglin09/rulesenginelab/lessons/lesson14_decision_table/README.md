# Lesson 14: Decision Table Pattern

## Goal

Introduce spreadsheet-like business rules by moving discount thresholds into a
small CSV table.

This lesson is intentionally not using Drools' native XLS/XLSX decision-table
compiler yet. The first learning goal is the pattern:

1. Business rows live outside Java code.
2. Java loads those rows.
3. Java inserts each row as a Drools fact.
4. DRL decides which row applies.

That keeps the mental model clear before adding spreadsheet compiler setup.

## Files

- `table/discount-decision-table.csv`: the table of business rows.
- `model/Lesson14DecisionRow.java`: one CSV row represented as a fact.
- `rules/Lesson14DiscountRuleUnit.java`: contains `requests`, `results`, and
  `decisionRows`.
- `rules/Lesson14DiscountRuleUnit.drl`: matches the request against decision
  row facts.
- `service/Lesson14DecisionTableLoader.java`: reads CSV rows from the
  classpath.
- `service/Lesson14DiscountService.java`: inserts request, result, and row
  facts before firing rules.

## Key Idea

The CSV is like a simple spreadsheet:

```csv
rowName,customerType,minimumOrderAmountCents,discountPercent,reason
Large order,ANY,50000,15,Large order over 500
VIP customer,VIP,10000,10,VIP customer over 100
Normal customer,NORMAL,20000,5,Normal customer over 200
```

The service converts each row into `Lesson14DecisionRow` and inserts it into
`/decisionRows`.

The DRL rule then says:

```drl
$row : /decisionRows[
    matchesCustomerType($request.customerType),
    $request.orderAmountCents >= minimumOrderAmountCents
]
```

Those two conditions are `AND` conditions. The row must match the customer type
and the request amount must be high enough.

## Why The Rule Checks The Current Discount

Several rows can match the same request. A VIP order for 600 dollars matches:

- `Large order`
- `VIP customer`

The rule uses:

```drl
discountPercent < $row.getDiscountPercent()
```

That means a matching row only replaces the result when it gives a better
discount than the current winner.

## Try It

POST `/lessons/14/discount`

```json
{
  "customerType": "VIP",
  "orderAmountCents": 60000
}
```

Expected result:

```json
{
  "discountPercent": 15,
  "reason": "Large order over 500",
  "matchedDecisionRow": "Large order"
}
```

## Takeaway

A decision table is useful when the logic has many similar rows. The DRL rule
stays stable, while business values move into a table.

Native Drools spreadsheet decision tables are still useful, but they add extra
formatting and build-time details. This lesson teaches the simpler row-fact
model first.
