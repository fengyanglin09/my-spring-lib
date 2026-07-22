# Lesson 06: Rule Unit Config

Lesson 06 moves discount numbers out of DRL and into a Java config fact.

Earlier lessons used hard-coded values:

```drl
orderAmount.compareTo(new java.math.BigDecimal("500")) > 0
setDiscountPercent(15)
```

This lesson inserts a `Lesson06DiscountConfig` object into the Rule Unit and
lets DRL read from it:

```drl
$config : /configs
orderAmount.compareTo($config.getLargeOrderMinimumAmount()) > 0
setDiscountPercent($config.getLargeOrderPercent())
```

## Endpoint

```text
POST /api/rules-engine/lessons/06/discount
```

Try:

```json
{
  "customerType": "VIP",
  "orderAmount": 600
}
```

Expected response with the default config:

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

## What Changed

The Rule Unit has a new store:

```java
private final DataStore<Lesson06DiscountConfig> configs = DataSource.createStore();
```

The service inserts the config before firing rules:

```java
ruleUnit.getConfigs().add(discountConfig);
```

The DRL binds that config:

```drl
$config : /configs
```

## Mental Model

Facts are not only request data. A fact can also be reference data, config, or
context.

In this lesson:

```text
request fact -> what happened?
result fact  -> what should we return?
config fact  -> what business thresholds should rules use?
```

Lesson 07 will take the next step and load this config from Spring
`application.yml`.
