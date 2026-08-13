# Lesson 05: Auto-Configuration

## What This Solves

Lesson 04 showed how Spring creates beans and injects them into other beans.
Lesson 05 asks the next question:

```text
Where do many of Spring Boot's useful default beans come from?
```

The answer is auto-configuration.

Auto-configuration is Spring Boot's way of saying:

```text
If the right classes are on the classpath,
and the application has not already defined its own bean,
then create a useful default bean.
```

Lesson 05 keeps those two checks separate:

```text
classpath check:
    Is Lesson05GreetingLibraryMarker available?

bean check:
    Is a Lesson05GreetingService bean already defined?

default creation:
    If marker is present and no service bean exists,
    create a default Lesson05GreetingService bean.
```

The simplest version of this lesson is:

```text
Boot can create beans for you even when the class is not annotated with
@Service or @Component.

It does that through configuration classes and @Bean methods.

Auto-configuration is Boot's conditional version of that idea:
"create this default bean only when it makes sense."
```

The central chain is:

```text
ApplicationContext starts
    -> Boot imports auto-configuration classes
    -> each auto-configuration checks its conditions
    -> @ConditionalOnClass checks whether a library/type is on the classpath
    -> @ConditionalOnMissingBean checks whether the app already has a bean
    -> matching @Bean methods create default beans
    -> non-matching @Bean methods back off
    -> ConditionEvaluationReport records why each decision happened
```

## Lesson Coverage

This lesson covers:

1. Auto-configuration as conditional default bean creation.
2. The difference between component scanning and auto-configuration imports.
3. `ApplicationContextRunner` as a small test tool for auto-configuration.
4. `AutoConfigurations.of(...)` as a way to import one auto-configuration in a
   focused test.
5. `@ConditionalOnClass` as a classpath condition.
6. `@ConditionalOnMissingBean` as the common backing-off condition.
7. `ConditionEvaluationReport` as Boot's explanation of condition decisions.

This lesson does not teach configuration properties binding, profiles, REST,
Actuator's `/conditions` endpoint, or writing a real external starter. Those are
coming later.

## The Big Idea

Spring Boot does not magically know what every application needs.

Instead, Boot looks at signals:

```text
Classpath signal:
    Is a certain library or class available?

Application bean signal:
    Has the application already defined a bean of this type?

Property signal:
    Did configuration properties enable or disable something?
```

Lesson 05 focuses on the first two.

If the classpath says "this feature is possible" and the application has not
already supplied its own bean, Boot creates a default.

If the application supplies its own bean, Boot usually backs off.

## What Lesson 05 Is Trying To Teach

Lesson 05 is trying to teach one main idea:

```text
Spring Boot defaults are usually conditional beans.
```

That means Boot is often doing this:

```text
Can I see the library/class this feature needs?
    yes -> this feature is possible
    no  -> do nothing

Did the application already define its own bean?
    yes -> back off and keep the user's bean
    no  -> create Boot's default bean
```

The lesson uses a fake greeting service because it is small. The real-world
version of the same idea is bigger:

```text
If web classes are present and you did not define your own web infrastructure,
Boot creates web defaults.

If database classes are present and you did not define your own database
infrastructure, Boot creates database defaults.

If you define your own bean, Boot often backs off.
```

So the greeting code is not the lesson. The conditional decision is the lesson.

## Two Separate Questions

Your confusion around `Lesson05GreetingAutoConfiguration` is exactly why this
section exists.

The auto-configuration is not saying:

```text
I depend on Lesson05GreetingService,
and I guarantee Lesson05GreetingService.
```

That would feel circular.

The lesson now separates the two ideas:

```text
Lesson05GreetingLibraryMarker
    -> classpath marker
    -> answers: is the pretend library available?
    -> checked by @ConditionalOnClass
    -> not a Spring bean

Lesson05GreetingService
    -> bean contract
    -> answers: what kind of bean might the app need?
    -> checked by @ConditionalOnMissingBean
    -> created by the @Bean method only when conditions match
```

So the decision is:

```text
Can Java load Lesson05GreetingLibraryMarker?
    no  -> this auto-configuration is not relevant
    yes -> continue

Does the context already contain a Lesson05GreetingService bean?
    yes -> back off
    no  -> call the @Bean method and create a default service bean
```

The `@Bean` method does not guarantee creation. It is a recipe Spring may use
after conditions pass.

## Is Lesson05GreetingAutoConfiguration A Bean?

Yes, when this auto-configuration is imported and its class-level conditions
match, Spring treats `Lesson05GreetingAutoConfiguration` as a configuration
bean.

But it is not the same bean as `lesson05GreetingService`.

There are two different objects:

```text
Lesson05GreetingAutoConfiguration
    -> configuration bean
    -> source of @Bean methods
    -> Spring uses it to define/create other beans
    -> application code normally does not use this object directly

Lesson05DefaultGreetingService
    -> service bean
    -> returned by lesson05GreetingService()
    -> application code would use this through Lesson05GreetingService
```

The process is:

```text
ApplicationContextRunner
    -> imports Lesson05GreetingAutoConfiguration
    -> Spring evaluates @ConditionalOnClass on the configuration class
    -> if it matches, Spring registers the auto-configuration as a configuration bean
    -> Spring reads the @Bean methods inside it
    -> Spring evaluates @ConditionalOnMissingBean on lesson05GreetingService()
    -> if that matches, Spring registers a bean definition for lesson05GreetingService
    -> when singleton beans are created, Spring calls lesson05GreetingService()
    -> the returned Lesson05DefaultGreetingService object becomes the service bean
```

So a useful analogy is:

```text
configuration bean = cookbook
@Bean method       = recipe
service bean      = meal produced from the recipe
```

The cookbook is present so Spring can read recipes. The meal is the object your
application actually wants to use.

## Auto-Configuration Vs Component Scanning

These two mechanisms both create beans, but they answer different questions.

Before comparing discovery mechanisms, keep this simple annotation mental model:

```text
@Configuration
    = "Here are bean definitions for this application."

@AutoConfiguration
    = "Here are default bean definitions that Spring Boot may apply automatically,
       usually only if certain conditions match."
```

Both can contain `@Bean` methods. The important difference is intent:

```text
@Configuration
    -> application configuration you intentionally provide

@AutoConfiguration
    -> Boot/starter default configuration that should apply only when it fits
```

Component scanning asks:

```text
What application classes did the developer annotate under the app package?
```

Auto-configuration asks:

```text
What default infrastructure should Boot contribute based on classpath,
properties, and existing beans?
```

In Lesson 04, this was component scanning:

```java
@Service
public class Lesson04OrderService {
}
```

Spring found that class because it was under the root application package.

In Lesson 05, the auto-configuration is imported by the test:

```java
new ApplicationContextRunner()
    .withConfiguration(AutoConfigurations.of(Lesson05GreetingAutoConfiguration.class))
```

That line means:

```text
Start a tiny context and include this one auto-configuration class.
```

Real external starters usually list their auto-configuration classes in:

```text
META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

Boot reads those imports and decides which auto-configurations apply.

## Why No @SpringBootTest?

Lesson 05 does not use `@SpringBootTest` on purpose.

`@SpringBootTest` is for tests that need the application's Boot startup
machinery:

```text
@SpringBootTest
    -> asks Boot to find SpringBootLabApplication
    -> starts an ApplicationContext through SpringApplication
    -> component scans the application package
    -> loads many application beans
    -> may include many auto-configurations
```

That is useful when the question is:

```text
Does my application start and work with its real Spring Boot context?
```

Lesson 05 is asking a smaller question:

```text
Given this one auto-configuration,
what bean appears when conditions match,
and what bean disappears when conditions do not match?
```

For that, `ApplicationContextRunner` is a better tool:

```text
ApplicationContextRunner
    -> creates a tiny ApplicationContext
    -> imports only the auto-configuration selected by the test
    -> optionally adds one user bean
    -> optionally hides one class from the classpath
    -> runs assertions
    -> closes the context
```

So the Lesson 05 test is still using Spring. It still creates a real
`ApplicationContext`. It still evaluates real Spring Boot conditions. It still
creates real beans.

But it is not a full application startup test.

The difference is:

```text
@SpringBootTest
    = full Boot application context test

ApplicationContextRunner
    = focused auto-configuration context test
```

Whether to call it an integration test depends on how strict you want the label
to be:

```text
Not a unit test:
    because it creates a real Spring ApplicationContext

Not a full app integration test:
    because it does not start the whole SpringBootLabApplication

Best name:
    focused auto-configuration integration test
```

That focused shape is exactly why Spring Boot's own documentation recommends
`ApplicationContextRunner` for auto-configuration tests.

## Where Does lesson05GreetingService Come From?

This is the most important naming detail in the lesson:

```text
lesson05GreetingService is a Spring bean name.
lesson05GreetingService is not a Java class name.
lesson05GreetingService is not created by @Service.
lesson05GreetingService is not created by @Component.
```

It comes from this `@Bean` method in `Lesson05AutoConfigurationTest`:

```java
@Bean
@ConditionalOnMissingBean(Lesson05GreetingService.class)
Lesson05GreetingService lesson05GreetingService() {
    return new Lesson05DefaultGreetingService("lesson05-auto-configuration");
}
```

By default, Spring uses the method name as the bean name:

```text
method name: lesson05GreetingService
bean name:   lesson05GreetingService
```

The actual object is:

```text
new Lesson05DefaultGreetingService(...)
```

So the context stores:

```text
bean name:        lesson05GreetingService
bean type:        Lesson05GreetingService
actual class:     Lesson05DefaultGreetingService
created because:  @Bean method ran
```

That is why this inspector code can return `true`:

```java
context.containsBean("lesson05GreetingService")
```

It does not mean Spring found a class named `lesson05GreetingService`. It means:

```text
Does the ApplicationContext contain a bean registered under this name?
```

The answer is `true` only in the first scenario:

```text
no user bean exists
    -> @ConditionalOnMissingBean matches
    -> @Bean method runs
    -> bean named lesson05GreetingService exists
```

The answer is `false` in the second scenario:

```text
custom user bean already exists
    -> @ConditionalOnMissingBean does not match
    -> @Bean method does not run
    -> bean named lesson05GreetingService does not exist
```

## ApplicationContextRunner

`ApplicationContextRunner` is from Spring Boot Test.

It is useful because it starts a very small context for one experiment:

```text
create tiny context
    -> include selected auto-configuration
    -> optionally add user beans
    -> run assertions
    -> close tiny context
```

This is better than `@SpringBootTest` for this lesson because we do not want the
whole application. We want a controlled lab where only one auto-configuration is
under the microscope.

## @ConditionalOnClass

`@ConditionalOnClass` asks whether a class is available from the classpath.

In this lesson:

```java
@ConditionalOnClass(name = "...")
```

means:

```text
Only use this auto-configuration if Lesson05GreetingLibraryMarker is loadable.
```

The lesson test uses `FilteredClassLoader` to pretend that
`Lesson05GreetingLibraryMarker` is missing. When that happens, the
auto-configuration does not match and the default bean is not created.

This marker class is not the bean being created. It is only the classpath signal.

```text
Lesson05GreetingLibraryMarker
    -> "the pretend greeting library exists"

Lesson05GreetingService
    -> "the bean type the auto-configuration may create"
```

Real examples use the same idea:

```text
If web classes are present
    -> web auto-configuration can activate

If database classes are present
    -> database auto-configuration can activate

If a library is missing
    -> its auto-configuration stays inactive
```

## @ConditionalOnMissingBean

`@ConditionalOnMissingBean` is one of the most important Spring Boot conditions.

It means:

```text
Create this default bean only if the application has not already provided one.
```

In this lesson:

```java
@Bean
@ConditionalOnMissingBean(Lesson05GreetingService.class)
Lesson05GreetingService lesson05GreetingService() {
    return new Lesson05DefaultGreetingService(...);
}
```

That method creates the default greeting service only when no
`Lesson05GreetingService` bean exists yet.

This condition is different from `@ConditionalOnClass`:

```text
@ConditionalOnClass(Lesson05GreetingLibraryMarker)
    -> checks whether a class exists on the classpath

@ConditionalOnMissingBean(Lesson05GreetingService)
    -> checks whether a bean exists in the ApplicationContext
```

When the test registers this user bean:

```java
withBean(
    "lesson05CustomGreetingService",
    Lesson05GreetingService.class,
    () -> new Lesson05CustomGreetingService("student")
)
```

the auto-configuration backs off:

```text
user bean already exists
    -> @ConditionalOnMissingBean does not match
    -> default bean is not created
    -> custom bean wins
```

That is the basic Spring Boot customization model.

## ConditionEvaluationReport

`ConditionEvaluationReport` is Boot's record of conditional decisions.

It answers questions like:

```text
Did this auto-configuration class match?
Did this @Bean method match?
Which condition said yes?
Which condition said no?
What message explains the decision?
```

In this lesson, `Lesson05ConditionReportInspector` reads the report directly.

In real applications, you usually see condition information through:

- startup logs when condition report logging is enabled
- Actuator's condition report endpoint
- test utilities such as `ApplicationContextRunner`

The important habit is this:

```text
When auto-configuration surprises you, ask for the condition report.
```

## Mental Model

The lesson auto-configuration says:

```text
If Lesson05GreetingLibraryMarker is on the classpath,
and no Lesson05GreetingService bean already exists,
then create Lesson05DefaultGreetingService as the default bean.
```

Three test scenarios prove the model:

```text
Scenario 1: no user bean
    -> classpath condition matches
    -> missing-bean condition matches
    -> default bean appears

Scenario 2: user bean exists
    -> classpath condition matches
    -> missing-bean condition does not match
    -> default bean backs off
    -> custom bean remains

Scenario 3: required class is hidden
    -> classpath condition does not match
    -> auto-configuration is inactive
    -> default bean does not appear
```

## Concept Map

```text
ApplicationContextRunner
    -> starts tiny test context

AutoConfigurations.of(...)
    -> imports one auto-configuration class

@AutoConfiguration
    -> marks a class as Boot auto-configuration

@ConditionalOnClass
    -> checks classpath

@ConditionalOnMissingBean
    -> checks existing beans and backs off if needed

@Bean
    -> creates the default bean when conditions match

ConditionEvaluationReport
    -> explains match and no-match decisions
```

## Main Ideas

- Auto-configuration creates defaults conditionally.
- Classpath dependencies are signals that certain defaults may be useful.
- User-defined beans are stronger signals than Boot defaults.
- Backing off is usually a feature, not a failure.
- `ApplicationContextRunner` is the right tool for focused auto-configuration
  tests.
- The condition report is your first stop when an auto-configured bean appears
  or does not appear unexpectedly.

## Common Traps

- Thinking auto-configuration is the same as component scanning. They both
  create beans, but they are selected differently.
- Thinking Boot always creates a bean when a starter is present. Conditions still
  decide.
- Thinking a missing auto-configured bean means Boot is broken. Often a
  condition did not match.
- Defining a custom bean and being surprised that Boot's default disappeared.
  That disappearance is often `@ConditionalOnMissingBean` doing its job.
- Testing auto-configuration with a full `@SpringBootTest` when a tiny
  `ApplicationContextRunner` context would explain the behavior more clearly.

## Files

- `model/Lesson05AutoConfigurationSnapshot.java`: named facts from one
  auto-configuration experiment.
- `service/Lesson05GreetingService.java`: the interface the default and custom
  beans implement.
- `service/Lesson05DefaultGreetingService.java`: the default implementation
  created by auto-configuration.
- `service/Lesson05CustomGreetingService.java`: a user-defined implementation
  used to prove backing off.
- `support/Lesson05ConditionReportInspector.java`: reads beans and condition
  report messages from a tiny context.
- `support/Lesson05GreetingLibraryMarker.java`: a pretend classpath signal used
  by `@ConditionalOnClass`.
- `Lesson05AutoConfigurationTest.java`: defines the lesson auto-configuration
  and proves defaulting, backing off, and classpath conditions.

## Code Walkthrough

The test starts with:

```text
new ApplicationContextRunner()
    -> withConfiguration(AutoConfigurations.of(Lesson05GreetingAutoConfiguration.class))
```

That means:

```text
start a small context
include this one auto-configuration
do not start the whole application
```

The lesson auto-configuration has two levels of conditions:

```text
class level:
    @ConditionalOnClass(...)
    -> is the pretend library marker on the classpath?

bean method level:
    @ConditionalOnMissingBean(Lesson05GreetingService.class)
    -> has the app already supplied this kind of bean?
```

When no user bean exists:

```text
@ConditionalOnClass matches
@ConditionalOnMissingBean matches
lesson05GreetingService bean is created
```

When a user bean exists:

```text
@ConditionalOnClass matches
@ConditionalOnMissingBean does not match
lesson05GreetingService bean is not created
lesson05CustomGreetingService remains
```

When the class is hidden:

```text
@ConditionalOnClass does not match
auto-configuration is inactive
no greeting service bean is created
```

## Run The Lesson Test

From the repository root:

```bash
./mvnw -q -pl learning-apps/spring-boot-lab -Dtest=Lesson05AutoConfigurationTest test
```

To run all Spring Boot lab tests:

```bash
./mvnw -q -pl learning-apps/spring-boot-lab test
```

## Official Docs

- [Spring Boot: Auto-configuration](https://docs.spring.io/spring-boot/reference/using/auto-configuration.html)
- [Spring Boot: Creating Your Own Auto-configuration](https://docs.spring.io/spring-boot/reference/features/developing-auto-configuration.html)
