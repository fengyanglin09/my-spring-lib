# Lesson 03: Application Startup And The Context

## What This Solves

Lesson 02 showed how Maven prepares a classpath and Java loads classes from it.
Lesson 03 shows what Spring Boot does next: it uses `SpringApplication` to create
and refresh an `ApplicationContext`.

The core question for this lesson is:

```text
What happens between calling SpringApplication.run(...) and receiving a live context back?
```

The central chain is:

```text
main method or test
    -> create/configure SpringApplication
    -> pass command-line args
    -> SpringApplication creates an ApplicationContext
    -> Boot prepares the Environment and ApplicationArguments
    -> Spring refreshes the context and creates beans
    -> ApplicationRunner beans run near the end of startup
       -> Lesson03StartupRecorder.run(args) returns void back to SpringApplication
    -> SpringApplication.run(...) returns the live context
```

## Lesson Coverage

This lesson covers:

1. `SpringApplication` as the Boot launcher.
2. `ConfigurableApplicationContext` as the live container returned by startup.
3. Command-line options parsed into `ApplicationArguments`.
4. Command-line options exposed through the Spring `Environment`.
5. Component scanning from the root application package.
6. `ApplicationRunner` as code that runs near the end of startup.
7. Closing the context after a programmatic startup test.

This lesson does not teach REST controllers, profiles, configuration properties,
or auto-configuration conditions. Those need their own space later.

## Mental Model

`SpringApplication` is the launch sequence. `ApplicationContext` is the assembled
application.

```text
SpringApplication = the startup coordinator
Environment       = properties and profiles available during startup
ApplicationArgs   = parsed command-line input
ApplicationContext = the container of created beans
ApplicationRunner = startup work after the context is ready
```

The context is like a workbench that has already been assembled. Once
`SpringApplication.run(...)` returns, the beans have been created and the context
can be used or closed.

## Core Vocabulary

- `SpringApplication`: Boot's launcher for starting a Spring app from a main
  method or explicit test.
- `WebApplicationType.NONE`: tells Boot not to start a servlet or reactive web
  application.
- `ConfigurableApplicationContext`: a live Spring context that can be inspected
  and closed.
- `ApplicationArguments`: parsed command-line arguments registered as a bean.
- `Environment`: the property source view used by Spring and Boot.
- `ApplicationRunner`: a bean callback that runs after startup work and before
  `SpringApplication.run(...)` fully returns.
- Component scanning: finding annotated classes under the root application
  package and registering them as beans.

## Option And Non-Option Args

Spring Boot receives startup arguments as raw strings, then exposes them through
`ApplicationArguments`.

Option args start with `--`:

```text
--lesson03.enabled=true
--server.port=8081
--debug
```

Boot treats those as named options. In this lesson:

```text
--lesson03.enabled=true
```

becomes:

```text
option name  = lesson03.enabled
option value = true
```

Because Spring Boot adds command-line option args to the Spring `Environment`,
code can read that value by property name:

```java
environment.getProperty("lesson03.enabled")
```

That call means:

```text
Find the property named lesson03.enabled and return its value.
```

For this lesson's startup args, it returns:

```text
true
```

Non-option args are bare values that do not start with `--`:

```text
startup-input.txt
orders.csv
run-once
```

They are useful for positional startup input, such as a file path, command name,
or one-time job argument. In this lesson:

```text
startup-input.txt
```

is intentionally bare, so it appears in:

```java
applicationArguments.getNonOptionArgs()
```

It does not become an `Environment` property because it has no option name.

## Two Different Run Methods

This lesson has two methods named `run`, and they do different jobs.

The first is Spring Boot's launcher method:

```java
ConfigurableApplicationContext context = application.run(...);
```

That is `SpringApplication.run(...)`. It starts the whole application and returns
the live `ApplicationContext`.

The second is our callback method:

```java
public void run(ApplicationArguments args)
```

That is `Lesson03StartupRecorder.run(...)`. It is required by
`ApplicationRunner`, and it returns `void`.

The precise flow is:

```text
test
    -> calls SpringApplication.run(...)

SpringApplication.run(...)
    -> creates the ApplicationContext
    -> creates @Component beans
    -> finds Lesson03StartupRecorder because it is a bean
    -> notices the bean implements ApplicationRunner
    -> calls Lesson03StartupRecorder.run(args)
       -> this callback returns void
    -> finishes startup
    -> returns ConfigurableApplicationContext to the test
```

So `Lesson03StartupRecorder.run(args)` does not return the context. It only runs
as one startup callback inside the larger `SpringApplication.run(...)` method.

## Concept Map

```text
SpringApplication(SpringBootLabApplication.class)
    -> set WebApplicationType.NONE
    -> run("--lesson03.enabled=true", "startup-input.txt")
    -> create ApplicationContext
    -> parse ApplicationArguments
       -> option arg: lesson03.enabled=true
       -> non-option arg: startup-input.txt
    -> add command-line property to Environment
    -> scan lesson beans
    -> SpringApplication calls Lesson03StartupRecorder.run(args)
       -> Lesson03StartupRecorder.run(args) returns void
    -> return ConfigurableApplicationContext
    -> test inspects Lesson03StartupSnapshot
    -> try-with-resources closes the context
```

## Main Ideas

- `SpringApplication.run(...)` returns the running context; it is not just a
  void startup trigger.
- The context is created before application beans can be used.
- Command-line args have two useful shapes: parsed `ApplicationArguments` and
  properties in `Environment`.
- `Environment.getProperty("lesson03.enabled")` reads the value for the named
  property `lesson03.enabled`; in this lesson that value is `true`.
- `ApplicationRunner` is a better place for startup tasks than hiding work in
  constructors or lifecycle callbacks.
- In tests that start `SpringApplication` directly, `WebApplicationType.NONE`
  keeps the lesson from accidentally teaching web startup.
- Programmatically started contexts should be closed when the test is done.

## Common Traps

- Thinking `main` is where the application is assembled. `main` usually just
  delegates to `SpringApplication`.
- Putting real work in constructors when an `ApplicationRunner` would make the
  startup phase clearer.
- Forgetting that command-line options can affect the `Environment`.
- Starting web infrastructure in a test that only needs a regular context.
- Inspecting the bean registry in production code. It is useful for this lesson,
  but constructor injection is the normal application style.
- Forgetting to close contexts created manually in tests.

## Files

- `model/Lesson03StartupSnapshot.java`: groups the startup observations.
- `support/Lesson03StartupInspector.java`: inspects the live context, arguments,
  and environment.
- `support/Lesson03StartupRecorder.java`: an `ApplicationRunner` that records
  the arguments it saw during startup.
- `Lesson03ApplicationStartupContextTest.java`: starts the app through
  `SpringApplication` and proves the startup chain.

## Code Walkthrough

The test uses the same launcher style as a `main` method, but keeps it explicit:

```text
new SpringApplication(SpringBootLabApplication.class)
    -> WebApplicationType.NONE
    -> run("--lesson03.enabled=true", "startup-input.txt")
       -> internally calls Lesson03StartupRecorder.run(args)
       -> then returns ConfigurableApplicationContext
    -> context.getBean(Lesson03StartupInspector.class)
    -> inspect startup snapshot
    -> close context
```

The assertions prove five things:

- a real application context started
- component scanning found lesson beans
- option and non-option command-line args were parsed
- the option arg reached the `Environment`
- the `ApplicationRunner` saw the startup arguments before inspection

## Run The Lesson Test

```bash
./mvnw -pl learning-apps/spring-boot-lab -Dtest=Lesson03ApplicationStartupContextTest test
```

## What Comes Next

Lesson 04 zooms in on beans and dependency injection: how application objects
become Spring-managed beans and how constructor injection connects them.

## Official Docs

- [SpringApplication](https://docs.spring.io/spring-boot/reference/features/spring-application.html)
- [Structuring Your Code](https://docs.spring.io/spring-boot/reference/using/structuring-your-code.html)
- [Spring ApplicationContext Container Overview](https://docs.spring.io/spring-framework/reference/core/beans/basics.html)
