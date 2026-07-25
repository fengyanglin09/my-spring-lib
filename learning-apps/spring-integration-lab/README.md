# Spring Integration Lab

This module is a reference-first learning path for `org.springframework.integration`.
It is intentionally scaffolded without implementation code so each lesson can become
a small, standalone concept note before any examples are added.

## Structure

- `lessons`: focused lesson packages. Each lesson starts with a `README.md` and a
  `package-info.java` only.
- Each lesson README uses the same shape so it is easy to scan later.

```text
spring-integration-lab/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/
    │   └── resources/
    │       └── application.yml
    └── test/
        ├── groovy/
        └── resources/
            └── application-test.yml
```

## Lesson Template

1. What This Solves
2. Mental Model
3. Core Vocabulary
4. Concept Map
5. Main Ideas
6. Decision Rules
7. Common Traps
8. How This Connects
9. Reference Checklist
10. Mini Scenario
11. Official Docs

## Lesson File Structure

Each lesson should stay small and predictable. Start with only the baseline
files, then add folders only when the lesson needs them.

```text
lessonXX_topic_name/
├── README.md
├── package-info.java
├── flow/
├── api/
├── gateway/
├── handler/
├── model/
├── config/
└── support/
```

### Baseline Files

- `README.md`: the standalone lesson reference.
- `package-info.java`: a short package-level summary of the lesson.

### Optional Folders

- `flow`: Spring Integration flow definitions.
- `api`: HTTP/controller entry points for manual testing.
- `gateway`: messaging gateways, especially for request-reply examples.
- `handler`: service activators, transformers, routers, filters, splitters, aggregators, or small lesson-specific handlers.
- `model`: lesson-specific request, response, payload, event, or result types.
- `config`: lesson-specific channels, pollers, schedulers, error channels, retry config, message stores, or related beans.
- `support`: small helpers used only by that lesson.

### Resource And Test Mirrors

Only add resource folders when a lesson needs sample files or externalized
messages:

```text
src/main/resources/io/github/fengyanglin09/springintegrationlab/lessons/lessonXX_topic_name/
├── sample-data/
├── files/
└── messages/
```

This module uses Groovy and Spock for tests, matching the existing
`rules-engine-lab` style. Mirror the lesson package under `src/test/groovy`:

```text
src/test/groovy/io/github/fengyanglin09/springintegrationlab/lessons/lessonXX_topic_name/
└── LessonXXTopicNameSpec.groovy
```

### Folder Rules

- Add `model` only if messages need named payload types.
- Add `flow` once the lesson has an `IntegrationFlow`.
- Add `handler` once the flow calls lesson behavior.
- Add `config` only for explicit channels, pollers, error config, stores, or infrastructure.
- Add `api` only if the lesson should be reachable over HTTP.
- Add `gateway` only for request-reply or gateway lessons.
- Add `support` only when a helper is clearer than keeping the logic in the main lesson files.
- Add a lesson test package only when the lesson has behavior to verify.

## Lesson Path

1. [`lesson01_why_spring_integration`](src/main/java/io/github/fengyanglin09/springintegrationlab/lessons/lesson01_why_spring_integration/README.md): Why Spring Integration exists.
2. [`lesson02_messages`](src/main/java/io/github/fengyanglin09/springintegrationlab/lessons/lesson02_messages/README.md): Messages, payloads, and headers.
3. [`lesson03_message_channels`](src/main/java/io/github/fengyanglin09/springintegrationlab/lessons/lesson03_message_channels/README.md): Channels and producer-consumer decoupling.
4. [`lesson04_endpoints`](src/main/java/io/github/fengyanglin09/springintegrationlab/lessons/lesson04_endpoints/README.md): Message-consuming components.
5. [`lesson05_flows`](src/main/java/io/github/fengyanglin09/springintegrationlab/lessons/lesson05_flows/README.md): Integration flows as wiring diagrams.
6. [`lesson06_adapters`](src/main/java/io/github/fengyanglin09/springintegrationlab/lessons/lesson06_adapters/README.md): Inbound and outbound adapters.
7. [`lesson07_transformers_enrichers`](src/main/java/io/github/fengyanglin09/springintegrationlab/lessons/lesson07_transformers_enrichers/README.md): Payload and header shaping.
8. [`lesson08_routing`](src/main/java/io/github/fengyanglin09/springintegrationlab/lessons/lesson08_routing/README.md): Routing, filtering, and path selection.
9. [`lesson09_split_aggregate_resequence`](src/main/java/io/github/fengyanglin09/springintegrationlab/lessons/lesson09_split_aggregate_resequence/README.md): Fan-out, fan-in, and ordering.
10. [`lesson10_polling_scheduling_backpressure`](src/main/java/io/github/fengyanglin09/springintegrationlab/lessons/lesson10_polling_scheduling_backpressure/README.md): Polling, pacing, and buffering.
11. [`lesson11_error_handling`](src/main/java/io/github/fengyanglin09/springintegrationlab/lessons/lesson11_error_handling/README.md): Exceptions, error messages, and error channels.
12. [`lesson12_transactions_idempotency`](src/main/java/io/github/fengyanglin09/springintegrationlab/lessons/lesson12_transactions_idempotency/README.md): Transactions, duplicates, and recovery.
13. [`lesson13_testing_flows`](src/main/java/io/github/fengyanglin09/springintegrationlab/lessons/lesson13_testing_flows/README.md): Testing components, partial flows, and full flows.
14. [`lesson14_observability_operations`](src/main/java/io/github/fengyanglin09/springintegrationlab/lessons/lesson14_observability_operations/README.md): Message history, metrics, and control.
15. [`lesson15_adapter_deep_dives`](src/main/java/io/github/fengyanglin09/springintegrationlab/lessons/lesson15_adapter_deep_dives/README.md): Protocol-specific adapter study.

## Learning Order

Start with lessons 01-05 to learn the grammar: message, channel, endpoint, and
flow. Then study lessons 10-11 to understand pacing and failure behavior before
going deeper into routing, aggregation, transactions, tests, and adapters.
