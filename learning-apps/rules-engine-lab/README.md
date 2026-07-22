# Rules Engine Lab

This module is an educational Spring Boot app for learning Drools step by step.

## Structure

- `common`: shared infrastructure used by multiple lessons or examples.
- `lessons`: small, focused lesson packages. Each lesson should teach one idea.
- `examples`: larger examples that combine several lesson ideas.

## Lesson Path

1. [`lesson01_create_rule`](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson01_create_rule/README.md): One request, one result, one DRL rule.
2. [`lesson02_multiple_rules`](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson02_multiple_rules/README.md): Add VIP, normal, and large-order rules.
3. `lesson03_salience`: Show how firing order works.
4. `lesson04_activation_group`: Show how only one winner rule can fire.
5. `lesson05_validation_rules`: Add validation and null guards.
6. `lesson06_rule_unit_config`: Add `DiscountConfig` as a fact.
7. `lesson07_spring_properties`: Load config from `application.yml`.
8. `lesson08_rule_tracing`: Add matched/fired rule tracing.
9. `lesson09_phase_execution`: Add validation/discount phases.
10. `lesson10_queries`: Add DRL queries and `executeQuery`.
11. `lesson11_candidate_facts`: Let rules create candidate facts.
12. `lesson12_candidate_selection`: Pick the best candidate.
13. `lesson13_accumulate`: Calculate totals from cart items.
14. `lesson14_decision_table`: Introduce spreadsheet-like rules.

## Current Example

`examples/advanced_discount_rules` contains the existing advanced discount-rule
flow. It combines multiple later lessons and is useful as a reference, but new
learning should start from `lesson01_create_rule`.

## Implemented Lesson Notes

- [Lesson 01: Create Rule](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson01_create_rule/README.md)
- [Lesson 02: Multiple Rules](src/main/java/io/github/fengyanglin09/rulesenginelab/lessons/lesson02_multiple_rules/README.md)
