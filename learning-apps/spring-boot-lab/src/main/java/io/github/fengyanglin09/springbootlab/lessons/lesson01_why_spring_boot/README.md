# Lesson 01: Why Spring Boot Exists

## What This Solves

Spring Boot helps when a Spring application should start with useful defaults
instead of a pile of manual setup.

The core question for this lesson is:

```text
What does Spring Boot assemble before my own application code does anything interesting?
```

This lesson intentionally avoids HTTP endpoints, databases, configuration
properties, profiles, and actuator. Those arrive later. The first brick is just
startup: a `main` method delegates to `SpringApplication`, Boot creates an
application context, and component scanning finds application beans.

## Mental Model

Spring Boot is like a prepared workshop.

```text
SpringApplication      = opens the workshop
ApplicationContext     = the workbench where parts are assembled
@SpringBootApplication = the sign that marks the root of the project
Component scan         = finds the parts in your package tree
Auto-configuration     = adds sensible default tools based on dependencies
Starter                = brings a useful dependency set
```

You still write the application. Boot removes the repetitive setup so you can
start from a working application shape.

## Core Vocabulary

- `SpringApplication`: the Boot launcher used by `main`.
- Application context: the Spring container that owns beans.
- Bean: an object managed by Spring.
- Component scan: the process that finds annotated application classes.
- `@SpringBootApplication`: a convenience annotation that combines Boot
  configuration, auto-configuration, and component scanning.
- Starter: a dependency that brings a curated set of related libraries.
- Auto-configuration: Boot's default configuration based on the classpath and
  existing beans.

## Concept Map

```text
main(String[] args)
    -> SpringApplication.run(...)
    -> create ApplicationContext
    -> apply auto-configuration
    -> scan project packages
    -> register lesson beans
    -> app is ready for work
```

## Main Ideas

- Spring Boot applications are still Spring applications.
- Boot's first job is startup and assembly, not business logic.
- The main application class should live in a root package above lesson code so
  component scanning naturally sees the whole app.
- `@SpringBootApplication` is a compact way to opt into Boot configuration,
  auto-configuration, and component scanning.
- Starters and auto-configuration work together, but they are not the same
  thing.
- A minimal Boot app does not need to be a web app.

## Decision Rules

- Put the main application class at the package root.
- Keep lesson 01 non-web so startup and context assembly stay visible.
- Prefer one small bean over a controller when the lesson is about the container.
- Reach for Boot defaults first, then override only when the reason is clear.
- Treat Lombok as a helper for ceremony, not as the subject of the lesson.

## Common Traps

- Putting the main application class below the packages you expect to scan.
- Thinking Boot replaces Spring instead of configuring Spring.
- Starting with REST before understanding that the web layer is just one kind of
  auto-configured application.
- Assuming a starter and auto-configuration are the same thing.
- Adding custom configuration before observing what Boot already provides.

## Files

- `SpringBootLabApplication.java`: the root Boot application and `main` method.
- `Lesson01BootInspector.java`: a tiny Spring bean that inspects the running
  application context.
- `Lesson01BootSnapshot.java`: the result object returned by the inspector.
- `Lesson01WhySpringBootTest.java`: proves Boot starts the context and component
  scanning finds the lesson bean.

## Code Walkthrough

The smallest useful lesson flow is:

```text
test
    -> @SpringBootTest starts SpringBootLabApplication
    -> SpringApplication creates the ApplicationContext
    -> component scan finds Lesson01BootInspector
    -> test asks the inspector what Boot assembled
```

`Lesson01BootInspector` uses Lombok `@RequiredArgsConstructor` for constructor
injection. That is a practical helper here, not the main topic. The real point
is that Boot creates the context and supplies the dependencies.

## Run The Lesson Test

```bash
./mvnw -pl learning-apps/spring-boot-lab -Dtest=Lesson01WhySpringBootTest test
```

## What Comes Next

Lesson 02 zooms out from startup into project anatomy: Maven, dependency
management, starters, and why Boot projects usually let Boot choose compatible
library versions.

## Official Docs

- [Spring Boot](https://docs.spring.io/spring-boot/index.html)
- [Developing Your First Spring Boot Application](https://docs.spring.io/spring-boot/tutorial/first-application/index.html)
- [Structuring Your Code](https://docs.spring.io/spring-boot/reference/using/structuring-your-code.html)
