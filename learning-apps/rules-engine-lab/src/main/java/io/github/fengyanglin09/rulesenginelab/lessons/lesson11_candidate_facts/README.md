# Lesson 11: Candidate Facts

Lesson 11 changes the shape of discount rules.

Earlier lessons did this:

```text
discount rule -> modify final result
```

This lesson does this:

```text
discount rule -> create candidate fact
```

A candidate means "this discount is possible." It does not mean the discount
has been selected.

## What Is A Fact?

In Drools, a fact is an object Drools can inspect during rule execution.

In Lesson 11, these are facts:

```text
request   -> user input
result    -> API result object
config    -> business thresholds and percentages
context   -> current phase
candidate -> possible discount created by a rule
```

The important distinction is who creates the fact.

Service-added facts are known before rules run:

```java
ruleUnit.getRequests().add(request);
ruleUnit.getResults().add(result);
ruleUnit.getConfigs().add(discountConfig);
ruleUnit.getContexts().add(context);
```

Rule-created facts are discovered by Drools when a rule matches:

```drl
candidates.add(new Lesson11DiscountCandidate(...));
```

Rule of thumb:

```text
If Java already knows it before firing rules, the service adds it.
If Drools discovers it because a rule matched, the rule adds it.
```

## Endpoint

```text
POST /api/rules-engine/lessons/11/discount
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
  "reason": null,
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

## DRL Pattern

Candidate rules add facts:

```drl
candidates.add(new Lesson11DiscountCandidate(
    "Large order candidate",
    $config.getLargeOrderPercent(),
    "Large order"
));
```

Then Java queries those facts:

```java
QueryResults queryResults = instance.executeQuery("Find all discount candidates");
```

The query is:

```drl
query "Find all discount candidates"
    $candidate : /candidates
end
```

## Why This Helps

Candidate facts separate two decisions:

```text
Which discounts are possible?
Which possible discount should win?
```

Lesson 11 answers only the first question.

Lesson 12 will answer the second question by selecting the best candidate.
