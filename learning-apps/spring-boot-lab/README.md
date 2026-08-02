# Spring Boot Lab

This module is a focused learning path for Spring Boot fundamentals,
application structure, production readiness, and custom starter patterns.
Lombok and Vavr are helper libraries in this lab, not separate learning
categories.

## Structure

- `lessons`: small, focused lesson packages. Each lesson starts with a
  `README.md` and `package-info.java` that record the learning objective and
  the planned study path.
- Runnable code, tests, resources, and HTTP examples should be added only when a
  lesson is ready for hands-on study.

## Lesson File Structure

Start each lesson with only the baseline lesson notes and package metadata:

```text
lessonXX_topic_name/
├── README.md
└── package-info.java
```

Add folders only when the lesson needs them:

```text
lessonXX_topic_name/
├── README.md
├── package-info.java
├── api/
├── config/
├── model/
├── repository/
├── service/
└── support/
```

### Optional Folders

- `api`: controllers, request/response DTOs, API error models, and web boundary
  code.
- `config`: lesson-specific `@Configuration`, properties, bean setup, security
  configuration, and conditional wiring.
- `model`: domain models, commands, results, enums, records, snapshots, and
  other data shapes.
- `repository`: persistence interfaces, database-backed repositories, or
  in-memory repository examples.
- `service`: application services and business workflows.
- `support`: small helpers, inspectors, adapters, fixtures, or lesson-only
  utilities that are not the main business model.

### Folder Rules

- Keep `README.md` and `package-info.java` at the lesson root.
- Add only the folders the lesson earns; empty folders make a small lesson look
  bigger than it is.
- Prefer `model` for data returned by the lesson and `support` for code that
  exists mainly to make a concept observable.
- Add `api` only when the lesson teaches an HTTP boundary.
- Add `repository` only when persistence is part of the lesson.
- Add `config` only when the lesson needs explicit Boot or Spring configuration.

### Current Lesson Structures

Lesson 01 uses this shape because it teaches context startup:

```text
lesson01_why_spring_boot/
├── README.md
├── package-info.java
├── model/
│   └── Lesson01BootSnapshot.java
└── support/
    └── Lesson01BootInspector.java
```

Lesson 02 uses this shape because it teaches Maven and classpath assembly:

```text
lesson02_project_anatomy_maven_starters_classpath/
├── README.md
├── package-info.java
├── model/
│   ├── Lesson02ClasspathEntry.java
│   └── Lesson02DependencySnapshot.java
└── support/
    └── Lesson02ProjectClasspathInspector.java
```

Lesson 03 uses this shape because it teaches startup and context inspection:

```text
lesson03_application_startup_context/
├── README.md
├── package-info.java
├── model/
│   └── Lesson03StartupSnapshot.java
└── support/
    ├── Lesson03StartupInspector.java
    └── Lesson03StartupRecorder.java
```

Lesson 04 uses this shape because it teaches a small bean graph:

```text
lesson04_beans_dependency_injection/
├── README.md
├── package-info.java
├── config/
│   └── Lesson04BeanConfiguration.java
├── model/
│   ├── Lesson04BeanGraphSnapshot.java
│   ├── Lesson04OrderReceipt.java
│   └── Lesson04OrderRequest.java
├── service/
│   ├── Lesson04OrderService.java
│   └── Lesson04ReceiptFormatter.java
└── support/
    ├── Lesson04BeanGraphInspector.java
    └── Lesson04OrderNumberGenerator.java
```

## Lesson Path

1. [`lesson01_why_spring_boot`](src/main/java/io/github/fengyanglin09/springbootlab/lessons/lesson01_why_spring_boot/README.md): Why Spring Boot exists.
2. [`lesson02_project_anatomy_maven_starters_classpath`](src/main/java/io/github/fengyanglin09/springbootlab/lessons/lesson02_project_anatomy_maven_starters_classpath/README.md): Project shape, Maven, starters, and classpath.
3. [`lesson03_application_startup_context`](src/main/java/io/github/fengyanglin09/springbootlab/lessons/lesson03_application_startup_context/README.md): Startup and the application context.
4. [`lesson04_beans_dependency_injection`](src/main/java/io/github/fengyanglin09/springbootlab/lessons/lesson04_beans_dependency_injection/README.md): Beans and constructor injection.
5. [`lesson05_auto_configuration`](src/main/java/io/github/fengyanglin09/springbootlab/lessons/lesson05_auto_configuration/README.md): Auto-configuration and backing off.
6. [`lesson06_externalized_configuration`](src/main/java/io/github/fengyanglin09/springbootlab/lessons/lesson06_externalized_configuration/README.md): Properties, profiles, and binding.
7. [`lesson07_rest_apis_spring_mvc`](src/main/java/io/github/fengyanglin09/springbootlab/lessons/lesson07_rest_apis_spring_mvc/README.md): REST APIs with Spring MVC.
8. [`lesson08_error_handling`](src/main/java/io/github/fengyanglin09/springbootlab/lessons/lesson08_error_handling/README.md): Validation and API error responses.
9. [`lesson09_service_layer_domain_boundaries`](src/main/java/io/github/fengyanglin09/springbootlab/lessons/lesson09_service_layer_domain_boundaries/README.md): Services and domain boundaries.
10. [`lesson10_persistence_basics`](src/main/java/io/github/fengyanglin09/springbootlab/lessons/lesson10_persistence_basics/README.md): Persistence basics.
11. [`lesson11_transactions_consistency`](src/main/java/io/github/fengyanglin09/springbootlab/lessons/lesson11_transactions_consistency/README.md): Transactions and consistency.
12. [`lesson12_testing_spring_boot_apps`](src/main/java/io/github/fengyanglin09/springbootlab/lessons/lesson12_testing_spring_boot_apps/README.md): Boot testing strategy.
13. [`lesson13_calling_other_services`](src/main/java/io/github/fengyanglin09/springbootlab/lessons/lesson13_calling_other_services/README.md): Outbound HTTP clients.
14. [`lesson14_actuator_health`](src/main/java/io/github/fengyanglin09/springbootlab/lessons/lesson14_actuator_health/README.md): Actuator and health endpoints.
15. [`lesson15_observability`](src/main/java/io/github/fengyanglin09/springbootlab/lessons/lesson15_observability/README.md): Logs, metrics, traces, and observations.
16. [`lesson16_security_basics`](src/main/java/io/github/fengyanglin09/springbootlab/lessons/lesson16_security_basics/README.md): Spring Security defaults and simple configuration.
17. [`lesson17_packaging_runtime_config`](src/main/java/io/github/fengyanglin09/springbootlab/lessons/lesson17_packaging_runtime_config/README.md): Packaging and runtime configuration.
18. [`lesson18_custom_auto_configuration_starters`](src/main/java/io/github/fengyanglin09/springbootlab/lessons/lesson18_custom_auto_configuration_starters/README.md): Custom auto-configuration and starters.
19. [`lesson19_pragmatic_lombok`](src/main/java/io/github/fengyanglin09/springbootlab/lessons/lesson19_pragmatic_lombok/README.md): Lombok usage that helps Spring Boot code.
20. [`lesson20_pragmatic_vavr`](src/main/java/io/github/fengyanglin09/springbootlab/lessons/lesson20_pragmatic_vavr/README.md): Vavr usage at service and boundary failure points.
