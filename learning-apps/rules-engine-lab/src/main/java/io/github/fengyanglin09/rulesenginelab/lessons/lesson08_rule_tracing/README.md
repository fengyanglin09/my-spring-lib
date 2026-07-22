# Lesson 08: Rule Tracing

Lesson 08 adds tracing with Drools `AgendaEventListener`.

Earlier lessons recorded fired rules inside DRL:

```drl
addFiredRule("Large order discount")
```

That works for learning, but it mixes tracing with business actions. In this
lesson, tracing moves into Java:

```java
ruleConfig.getAgendaEventListeners().add(new DefaultAgendaEventListener() {
    @Override
    public void matchCreated(MatchCreatedEvent event) {
        result.addMatchedRule(event.getMatch().getRule().getName());
    }

    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        result.addFiredRule(event.getMatch().getRule().getName());
    }
});
```

## Endpoint

```text
POST /api/rules-engine/lessons/08/discount
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

The exact `matchedRules` order is not the main lesson. The key lesson is:

```text
matched -> Drools found facts that satisfy a rule
fired   -> Drools actually ran the rule's then block
```

With activation groups, multiple rules can match, but only the winner fires.

## Important Pattern

The service attaches tracing with a `RuleConfig`:

```java
RuleConfig ruleConfig = ruleTraceFactory.create(result);

try (RuleUnitInstance<Lesson08DiscountRuleUnit> instance =
             RuleUnitProvider.get().createRuleUnitInstance(ruleUnit, ruleConfig)) {
    instance.fire();
}
```

This keeps tracing outside the DRL. The rules stay focused on business changes,
while Java observes the rule engine lifecycle.
