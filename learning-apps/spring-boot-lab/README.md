# Spring Boot Lab

This module is a focused learning path for Spring Boot fundamentals,
application structure, production readiness, and custom starter patterns.
Lombok and Vavr are helper libraries in this lab, not separate learning
categories.

## Structure

- `lessons`: small, focused lesson packages. Each lesson starts with a
  `package-info.java` that records the learning objective and the planned study
  path.
- Runnable code, tests, resources, and HTTP examples should be added only when a
  lesson is ready for hands-on study.

## Lesson File Structure

Start each lesson with only the baseline package metadata:

```text
lessonXX_topic_name/
└── package-info.java
```

Add folders only when the lesson needs them:

```text
lessonXX_topic_name/
├── api/
├── config/
├── model/
├── service/
├── support/
└── package-info.java
```

## Lesson Path

1. `lesson01_why_spring_boot`: Why Spring Boot exists.
2. `lesson02_project_anatomy_maven_starters`: Project shape, Maven, and starters.
3. `lesson03_application_startup_context`: Startup and the application context.
4. `lesson04_beans_dependency_injection`: Beans and constructor injection.
5. `lesson05_auto_configuration`: Auto-configuration and backing off.
6. `lesson06_externalized_configuration`: Properties, profiles, and binding.
7. `lesson07_rest_apis_spring_mvc`: REST APIs with Spring MVC.
8. `lesson08_error_handling`: Validation and API error responses.
9. `lesson09_service_layer_domain_boundaries`: Services and domain boundaries.
10. `lesson10_persistence_basics`: Persistence basics.
11. `lesson11_transactions_consistency`: Transactions and consistency.
12. `lesson12_testing_spring_boot_apps`: Boot testing strategy.
13. `lesson13_calling_other_services`: Outbound HTTP clients.
14. `lesson14_actuator_health`: Actuator and health endpoints.
15. `lesson15_observability`: Logs, metrics, traces, and observations.
16. `lesson16_security_basics`: Spring Security defaults and simple configuration.
17. `lesson17_packaging_runtime_config`: Packaging and runtime configuration.
18. `lesson18_custom_auto_configuration_starters`: Custom auto-configuration and starters.
19. `lesson19_pragmatic_lombok`: Lombok usage that helps Spring Boot code.
20. `lesson20_pragmatic_vavr`: Vavr usage at service and boundary failure points.
