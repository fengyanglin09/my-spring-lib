# Rules Engine Lab

This module is an educational Spring Boot app for learning Drools step by step.

## Structure

- `common`: shared infrastructure used by multiple lessons or examples.
- `lessons`: small, focused lesson packages. Each lesson should teach one idea.
- `examples`: larger examples that combine several lesson ideas.

## Lesson Path

1. [`lesson01_create_rule`](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson01_create_rule/README.md): One request, one result, one DRL rule.
2. [`lesson02_multiple_rules`](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson02_multiple_rules/README.md): Add VIP, normal, and large-order rules.
3. [`lesson03_salience`](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson03_salience/README.md): Show how firing order works.
4. [`lesson04_activation_group`](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson04_activation_group/README.md): Show how only one winner rule can fire.
5. [`lesson05_validation_rules`](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson05_validation_rules/README.md): Add validation and null guards.
6. [`lesson06_rule_unit_config`](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson06_rule_unit_config/README.md): Add `DiscountConfig` as a fact.
7. [`lesson07_spring_properties`](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson07_spring_properties/README.md): Load config from `application.yml`.
8. [`lesson08_rule_tracing`](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson08_rule_tracing/README.md): Add matched/fired rule tracing.
9. [`lesson09_phase_execution`](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson09_phase_execution/README.md): Add validation/discount phases.
10. [`lesson10_queries`](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson10_queries/README.md): Add DRL queries and `executeQuery`.
11. [`lesson11_candidate_facts`](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson11_candidate_facts/README.md): Let rules create candidate facts.
12. [`lesson12_candidate_selection`](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson12_candidate_selection/README.md): Pick the best candidate.
13. `lesson13_accumulate`: Calculate totals from cart items.
14. `lesson14_decision_table`: Introduce spreadsheet-like rules.

## Current Example

`examples/advanced_discount_rules` contains the existing advanced discount-rule
flow. It combines multiple later lessons and is useful as a reference, but new
learning should start from `lesson01_create_rule`.

## Implemented Lesson Notes

- [Lesson 01: Create Rule](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson01_create_rule/README.md)
- [Lesson 02: Multiple Rules](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson02_multiple_rules/README.md)
- [Lesson 03: Salience](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson03_salience/README.md)
- [Lesson 04: Activation Group](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson04_activation_group/README.md)
- [Lesson 05: Validation Rules](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson05_validation_rules/README.md)
- [Lesson 06: Rule Unit Config](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson06_rule_unit_config/README.md)
- [Lesson 07: Spring Properties](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson07_spring_properties/README.md)
- [Lesson 08: Rule Tracing](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson08_rule_tracing/README.md)
- [Lesson 09: Phase Execution](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson09_phase_execution/README.md)
- [Lesson 10: Queries](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson10_queries/README.md)
- [Lesson 11: Candidate Facts](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson11_candidate_facts/README.md)
- [Lesson 12: Candidate Selection](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson12_candidate_selection/README.md)
