# Lesson 04: Beans And Dependency Injection

## What This Solves

Lesson 03 showed that `SpringApplication.run(...)` returns a live
`ApplicationContext`. Lesson 04 asks the next question:

```text
What exactly is inside that context, and how does Spring connect those objects?
```

The answer is beans and dependency injection.

The central chain is:

```text
SpringApplication starts
    -> component scanning looks under the root application package
    -> Spring finds @Service, @Component, and @Configuration classes
    -> @Bean methods create additional objects
    -> Spring stores these objects as beans in the ApplicationContext
    -> Spring sees constructor parameters on a bean
    -> Spring finds matching beans by type
    -> Spring passes those dependencies into the constructor
    -> application code can use the fully wired object
```

## Lesson Coverage

This lesson covers:

1. What a Spring bean is.
2. What the `ApplicationContext` does with beans.
3. How component scanning finds `@Component`, `@Service`, and `@Configuration`.
4. How an `@Bean` method registers a plain Java object.
5. How constructor injection connects one bean to another.
6. Why services usually depend on interfaces or collaborators, not on the
   container directly.
7. How Lombok `@RequiredArgsConstructor` helps constructor injection without
   changing Spring's behavior.

This lesson does not teach REST controllers, databases, profiles, validation,
mocking, or auto-configuration. Those need their own lessons.

## What Is A Bean?

A bean is an object managed by Spring.

That means Spring is responsible for creating it, storing it in the
`ApplicationContext`, and supplying it to other beans that need it.

Normal Java:

```java
Lesson04ReceiptFormatter formatter = new Lesson04ReceiptFormatter();
```

Spring-managed Java:

```text
Spring creates Lesson04ReceiptFormatter
    -> stores it in ApplicationContext
    -> injects it into Lesson04OrderService
```

Both paths produce a Java object. The difference is ownership.

```text
Plain object = your code creates it
Spring bean  = Spring creates and manages it
```

For this lesson, each bean uses Spring's default scope: singleton.

In Spring, singleton means:

```text
one bean instance per ApplicationContext
```

It does not mean:

```text
one global object for the whole JVM forever
```

If the same application starts two separate `ApplicationContext` instances, each
context gets its own singleton beans. Most application services, repositories,
configuration objects, and clients use this default scope.

Not every object should be a bean. In this lesson:

- `Lesson04OrderService` is a bean because it is a long-lived application
  collaborator.
- `Lesson04ReceiptFormatter` is a bean because another bean depends on it.
- `Lesson04OrderNumberGenerator` is a bean because the service needs it and the
  configuration class provides it.
- `Lesson04OrderRequest` is not a bean because it is short-lived input data.
- `Lesson04OrderReceipt` is not a bean because it is short-lived result data.

## ApplicationContext

`ApplicationContext` is the Spring container.

For this lesson, think of it as the running application's object registry:

```text
ApplicationContext
    -> bean named lesson04OrderService
    -> bean named lesson04ReceiptFormatter
    -> bean named lesson04BeanConfiguration
    -> bean named lesson04OrderNumberGenerator
```

The context knows:

- which bean definitions exist
- which objects have already been created
- which dependencies each bean needs
- how to provide one bean to another

Production code should not usually ask the context for beans manually. Instead,
production code should declare its dependencies in constructors and let Spring
provide them.

The lesson uses `ApplicationContext` in `Lesson04BeanGraphInspector` only because
the test needs to make the bean graph visible.

## Component Scanning

Component scanning means Spring walks through application packages looking for
classes with stereotype annotations.

The root application class is:

```java
@SpringBootApplication
public class SpringBootLabApplication
```

Because that class is in:

```text
io.github.fengyanglin09.springbootlab
```

Spring scans that package and its subpackages, including:

```text
io.github.fengyanglin09.springbootlab.lessons.lesson04_beans_dependency_injection
```

This is why Lesson 04 classes are found automatically.

## Stereotype Annotations

Stereotype annotations mark a class as a Spring-managed component.

In this lesson:

- `@Component` means "this is a general Spring-managed object."
- `@Service` means "this is a Spring-managed object that holds service or
  workflow logic."
- `@Configuration` means "this is a Spring-managed class that contains bean
  definition methods."

`@Service` and `@Configuration` are more specific than `@Component`, but they
still participate in component scanning.

Default bean names usually come from the class name with a lowercase first
letter:

```text
Lesson04OrderService       -> lesson04OrderService
Lesson04ReceiptFormatter   -> lesson04ReceiptFormatter
Lesson04BeanConfiguration  -> lesson04BeanConfiguration
```

## @Bean Methods

`@Bean` is different from `@Component`.

`@Component` goes on a class:

```java
@Component
public class Lesson04ReceiptFormatter {
}
```

`@Bean` goes on a method inside a configuration class:

```java
@Bean
public Lesson04OrderNumberGenerator lesson04OrderNumberGenerator() {
    return request -> "...";
}
```

That method tells Spring:

```text
Call this method.
Take the returned object.
Store it in the ApplicationContext as a bean.
Use the method name as the default bean name.
```

So this method creates a bean named:

```text
lesson04OrderNumberGenerator
```

This is useful when:

- the object is from a third-party library
- the object is an interface implementation
- the object needs setup code
- you do not want to annotate the class directly
- the class cannot be modified

## Constructor Injection

Dependency injection means an object receives the collaborators it needs instead
of creating them itself.

Without dependency injection:

```java
public class Lesson04OrderService {
    private final Lesson04ReceiptFormatter formatter = new Lesson04ReceiptFormatter();
}
```

With constructor injection:

```java
public class Lesson04OrderService {
    private final Lesson04ReceiptFormatter formatter;

    public Lesson04OrderService(Lesson04ReceiptFormatter formatter) {
        this.formatter = formatter;
    }
}
```

Spring uses the constructor version like this:

```text
Spring creates Lesson04ReceiptFormatter
Spring creates Lesson04OrderNumberGenerator
Spring sees Lesson04OrderService needs both in its constructor
Spring passes those beans into the constructor
Spring stores the finished Lesson04OrderService as a bean
```

Constructor injection is preferred for required dependencies because:

- the dependency is visible in the constructor
- the field can be `final`
- the object cannot be created without its required collaborators
- tests can create the object directly if needed
- the service does not need to know about the Spring container

## Lombok In This Lesson

`@RequiredArgsConstructor` is from Lombok, not Spring.

It generates a constructor for final fields.

This:

```java
@RequiredArgsConstructor
public class Lesson04OrderService {
    private final Lesson04OrderNumberGenerator orderNumberGenerator;
    private final Lesson04ReceiptFormatter receiptFormatter;
}
```

acts like this:

```java
public Lesson04OrderService(
        Lesson04OrderNumberGenerator orderNumberGenerator,
        Lesson04ReceiptFormatter receiptFormatter
) {
    this.orderNumberGenerator = orderNumberGenerator;
    this.receiptFormatter = receiptFormatter;
}
```

Spring still performs the injection. Lombok only writes the constructor for us
at compile time.

## Mental Model

The object graph for this lesson looks like this:

```text
Lesson04BeanGraphInspector
    depends on ApplicationContext
    depends on Lesson04OrderService

Lesson04OrderService
    depends on Lesson04OrderNumberGenerator
    depends on Lesson04ReceiptFormatter

Lesson04OrderNumberGenerator
    created by @Bean method

Lesson04ReceiptFormatter
    found by @Component scanning
```

The flow of the lesson test is:

```text
@SpringBootTest starts the ApplicationContext
    -> component scanning finds Lesson04OrderService
    -> component scanning finds Lesson04ReceiptFormatter
    -> component scanning finds Lesson04BeanConfiguration
    -> Spring calls lesson04OrderNumberGenerator()
    -> Spring creates Lesson04OrderService with constructor injection
    -> Spring creates Lesson04BeanGraphInspector with constructor injection
test receives Lesson04BeanGraphInspector
    -> inspector checks bean names in ApplicationContext
    -> inspector calls Lesson04OrderService.accept(...)
    -> service uses injected collaborators
    -> test asserts the object graph works
```

## Concept Map

```text
@SpringBootApplication
    -> enables component scanning from the root package

@Component
    -> class becomes a bean

@Service
    -> class becomes a bean and signals service/workflow responsibility

@Configuration
    -> class becomes a bean and contains @Bean methods

@Bean
    -> method return value becomes a bean

Constructor injection
    -> Spring passes required beans into a constructor

@RequiredArgsConstructor
    -> Lombok generates the constructor that Spring will use
```

## Main Ideas

- A bean is a Java object managed by Spring.
- `ApplicationContext` is the container that holds and connects beans.
- Component scanning finds annotated classes under the root application package.
- `@Service` is still a component, but it communicates service-layer intent.
- `@Bean` registers the returned object, even if that object's class has no
  Spring annotation.
- Constructor injection is the clean default for required dependencies.
- Lombok can reduce constructor boilerplate, but it does not replace Spring's
  dependency injection.

## Common Traps

- Thinking every Java object should be a bean. Data objects like requests and
  results are usually plain objects.
- Thinking `@Service` is required for business logic to work. It is mainly a
  clearer stereotype; `@Component` would also register the class as a bean.
- Thinking `@Bean` and `@Component` do the same thing. They both register beans,
  but one is method-based and the other is class-based.
- Thinking Lombok performs injection. It only generates constructor code.
- Asking the `ApplicationContext` for dependencies inside normal services. That
  hides requirements; constructor injection makes them obvious.
- Creating collaborators with `new` inside a service when they should be
  replaceable or shared Spring-managed dependencies.

## Files

- `model/Lesson04OrderRequest.java`: short-lived input data, intentionally not a
  bean.
- `model/Lesson04OrderReceipt.java`: short-lived result data, intentionally not
  a bean.
- `model/Lesson04BeanGraphSnapshot.java`: named facts the test can assert.
- `support/Lesson04OrderNumberGenerator.java`: a plain Java interface used as a
  dependency.
- `config/Lesson04BeanConfiguration.java`: registers the generator with
  `@Configuration` and `@Bean`.
- `service/Lesson04ReceiptFormatter.java`: a `@Component` collaborator.
- `service/Lesson04OrderService.java`: a `@Service` wired through constructor
  injection.
- `support/Lesson04BeanGraphInspector.java`: lesson-only inspector that makes
  the context visible to the test.
- `Lesson04BeansDependencyInjectionTest.java`: starts Spring and proves the
  bean graph works.

## Code Walkthrough

The test starts a real Boot context:

```text
@SpringBootTest
    -> starts SpringBootLabApplication
    -> component scanning runs
    -> lesson beans are created
```

Spring finds these class-based beans:

```text
@Service Lesson04OrderService
@Component Lesson04ReceiptFormatter
@Component Lesson04BeanGraphInspector
@Configuration Lesson04BeanConfiguration
```

Spring also finds this method-based bean:

```text
@Bean lesson04OrderNumberGenerator()
    -> returns a Lesson04OrderNumberGenerator lambda
    -> bean name becomes lesson04OrderNumberGenerator
```

Then Spring wires the service:

```text
Lesson04OrderService constructor needs:
    -> Lesson04OrderNumberGenerator
    -> Lesson04ReceiptFormatter

ApplicationContext contains both:
    -> one from @Bean
    -> one from @Component

Spring passes both into the constructor.
```

Finally, the test proves the service works:

```text
Lesson04OrderService.accept(new Lesson04OrderRequest("ada", 3))
    -> orderNumberGenerator.nextReference(...)
    -> receiptFormatter.format(...)
    -> receipt order reference is L04-ADA-03
```

## Run The Lesson Test

From the repository root:

```bash
./mvnw -q -pl learning-apps/spring-boot-lab -Dtest=Lesson04BeansDependencyInjectionTest test
```

To run all Spring Boot lab tests:

```bash
./mvnw -q -pl learning-apps/spring-boot-lab test
```

## Official Docs

- [Spring Framework: Classpath Scanning and Managed Components](https://docs.spring.io/spring-framework/reference/core/beans/classpath-scanning.html)
- [Spring Framework: Dependencies and Configuration in Detail](https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-collaborators.html)
- [Spring Framework: Using the `@Configuration` Annotation](https://docs.spring.io/spring-framework/reference/core/beans/java/configuration-annotation.html)
- [Spring Framework: Using `@Autowired`](https://docs.spring.io/spring-framework/reference/core/beans/annotation-config/autowired.html)
