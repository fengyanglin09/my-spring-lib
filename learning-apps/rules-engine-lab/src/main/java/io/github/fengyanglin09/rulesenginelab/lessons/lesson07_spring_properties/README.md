# Lesson 07: Spring Properties

Lesson 06 inserted a config fact manually. Lesson 07 lets Spring create that
config from `application.yml`.

The property prefix is:

```text
rules-engine.lessons.lesson-07.discount
```

## Files To Read

- `config/Lesson07DiscountProperties.java`: typed Spring properties.
- `config/Lesson07DiscountConfiguration.java`: converts properties into a
  `Lesson07DiscountConfig` bean.
- `model/Lesson07DiscountConfig.java`: plain config fact inserted into Drools.
- `service/Lesson07DiscountService.java`: inserts the Spring-created config
  into `/configs`.
- `Lesson07DiscountRuleUnit.drl`: reads config values from `/configs`.

## Endpoint

```text
POST /api/rules-engine/lessons/07/discount
```

Try:

```json
{
  "customerType": "VIP",
  "orderAmount": 600
}
```

With the default `application.yml`, the large-order threshold is `500`, so the
large-order rule wins.

## Important Pattern

Spring owns external configuration:

```yaml
rules-engine:
  lessons:
    lesson-07:
      discount:
        large-order-percent: 18
```

Java converts that into a Drools fact:

```java
@Bean
public Lesson07DiscountConfig lesson07DiscountConfig(Lesson07DiscountProperties properties) {
    return new Lesson07DiscountConfig(...);
}
```

Drools reads the fact:

```drl
$config : /configs
setDiscountPercent($config.getLargeOrderPercent())
```

## Mental Model

Spring is the librarian. It knows where the configuration file is.

Drools is the rule evaluator. It should not rummage through Spring properties
directly. Instead, give Drools a clean Java fact containing the values it needs.
