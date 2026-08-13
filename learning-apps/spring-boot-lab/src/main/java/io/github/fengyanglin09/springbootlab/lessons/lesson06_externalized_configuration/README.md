# Lesson 06: Externalized Configuration

## What This Solves

Lesson 05 showed that Spring Boot can create beans conditionally. Lesson 06 asks
another everyday Spring Boot question:

```text
How can the same code run with different values in local, dev, test, and prod?
```

The answer is externalized configuration.

Externalized configuration means:

```text
Keep environment-specific values outside the Java code.
Let Boot load those values during startup.
Let application code read the final resolved values.
```

The central chain is:

```text
SpringApplication.run(...)
    -> reads startup arguments
    -> decides which config files to load
    -> loads base YAML
    -> activates profile-specific YAML
    -> adds command-line properties
    -> builds the Environment
    -> binds lesson06.lab.* into Lesson06LabProperties
    -> application beans use typed settings
```

## Lesson Coverage

This lesson covers:

1. External configuration as values outside compiled Java code.
2. `Environment` as Spring's resolved property view.
3. YAML files as config data.
4. Profile-specific YAML files.
5. Command-line properties overriding file values.
6. `@ConfigurationProperties` as typed binding.
7. `@EnableConfigurationProperties` as the switch that registers a properties
   type as a bean.
8. Simple constructor guards for typed configuration.

This lesson does not deeply teach cloud secrets, config trees, Actuator
`/env`, Actuator `/configprops`, custom config data loaders, or Bean Validation
annotations. Those deserve their own later space.

## The Big Idea

Hard-coded Java:

```java
Duration refreshInterval = Duration.ofSeconds(30);
```

Externalized configuration:

```yaml
lesson06:
  lab:
    refresh-interval: 30s
```

Typed Java object after binding:

```java
labProperties.refreshInterval()
```

The Java code can stay the same while each environment supplies different
values.

```text
same jar
    -> local values
    -> dev values
    -> prod values
```

## Environment

`Environment` is from Spring Framework.

For this lesson, think of it as Spring's final property lookup table:

```text
Environment
    -> lesson06.lab.region = command-line-region
    -> lesson06.lab.refresh-interval = 5s
    -> lesson06.lab.security.require-https = false
    -> lesson06.lab.security.token-audience = dev-audience
```

The Environment does not care where the winning value originally came from. It
answers:

```java
environment.getProperty("lesson06.lab.region")
```

That means:

```text
Find the final resolved value for this property key.
```

In this lesson, the answer is:

```text
command-line-region
```

## Property Sources And Precedence

Spring Boot loads configuration from multiple property sources.

Common examples:

```text
application.yml
application-dev.yml
environment variables
Java system properties
command-line arguments
test properties
```

When more than one source defines the same key, precedence decides the winner.

This lesson uses three sources:

```text
lesson06-application.yml
    -> base defaults

lesson06-application-dev.yml
    -> dev profile overrides

command-line arguments
    -> highest-priority values used by this test
```

The test intentionally creates this situation:

```text
base YAML says:
    lesson06.lab.region = file-default-region

command line says:
    lesson06.lab.region = command-line-region

final Environment says:
    lesson06.lab.region = command-line-region
```

Command line wins because Boot gives command-line properties higher precedence
than file-based config data.

## Lesson Config Files

The test uses:

```text
src/test/resources/lesson06-application.yml
src/test/resources/lesson06-application-dev.yml
```

The base YAML file provides defaults:

```yaml
lesson06:
  lab:
    region: file-default-region
    refresh-interval: 30s
    security:
      require-https: true
      token-audience: base-audience
```

The dev YAML file overrides only some values:

```yaml
lesson06:
  lab:
    refresh-interval: 5s
    security:
      token-audience: dev-audience
```

The test activates the dev profile:

```text
--spring.profiles.active=dev
```

So Boot considers both:

```text
lesson06-application.yml
lesson06-application-dev.yml
```

The dev file does not need to repeat every value. It only overrides the values
that are different for the dev environment.

The important rule is:

```text
profile-specific YAML overrides base YAML for the same property key
```

It does not erase the whole base file.

Example:

```text
base file:
    lesson06.lab.region = file-default-region
    lesson06.lab.refresh-interval = 30s
    lesson06.lab.security.require-https = true
    lesson06.lab.security.token-audience = base-audience

dev file:
    lesson06.lab.refresh-interval = 5s
    lesson06.lab.security.token-audience = dev-audience

after profile override:
    lesson06.lab.region = file-default-region
    lesson06.lab.refresh-interval = 5s
    lesson06.lab.security.require-https = true
    lesson06.lab.security.token-audience = dev-audience
```

So the base file still supplies values that the profile file does not mention.

## Why spring.config.name?

Normal Boot applications usually load:

```text
application.yml
application.properties
```

Lesson 06 uses lesson-specific file names so the lab does not accidentally turn
these teaching values into global application configuration.

The test passes:

```text
--spring.config.name=lesson06-application
```

That tells Boot:

```text
Look for lesson06-application.yml instead of application.yml.
```

Because the dev profile is active, Boot also looks for:

```text
lesson06-application-dev.yml
```

## Main Resources Vs Test Resources

This project currently has:

```text
src/test/resources/lesson06-application.yml
src/test/resources/lesson06-application-dev.yml
```

and does not currently have:

```text
src/main/resources/lesson06-application.yml
src/main/resources/lesson06-application-dev.yml
```

The difference matters:

```text
src/main/resources
    -> copied to target/classes
    -> included in the main application artifact
    -> available when the app is packaged and run normally

src/test/resources
    -> copied to target/test-classes
    -> available only during tests
    -> not included in the main application artifact
```

In this Maven module's test runtime, the classpath begins like this:

```text
target/test-classes
target/classes
...
```

That means if the same resource path exists in both places:

```text
src/test/resources/lesson06-application.yml
src/main/resources/lesson06-application.yml
```

then during Maven tests, the test resource is found first:

```text
target/test-classes/lesson06-application.yml
    -> shadows
target/classes/lesson06-application.yml
```

So for this lesson test:

```text
src/test/resources wins over src/main/resources when the file name and path are the same
```

At normal packaged runtime, the test resource is gone:

```text
src/test/resources is not packaged into the app jar
```

so the main resource would be the one available.

That is why Lesson 06 keeps its YAML in `src/test/resources`: the values are
teaching fixtures for this test, not app-wide defaults for every lesson.

## YAML Flattening

YAML is nested:

```yaml
lesson06:
  lab:
    security:
      token-audience: dev-audience
```

Spring's Environment sees flat property keys:

```text
lesson06.lab.security.token-audience=dev-audience
```

That is why this works:

```java
environment.getProperty("lesson06.lab.security.token-audience")
```

The nesting is nice for humans. The flattened key is how Spring resolves
properties.

## Profiles

A profile is a named environment mode.

In this lesson:

```text
dev
```

is active because the test passes:

```text
--spring.profiles.active=dev
```

That makes Boot load profile-specific config:

```text
lesson06-application-dev.yml
```

The important rule:

```text
profile-specific config overrides base config for the active profile
```

More precisely:

```text
profile-specific config overrides base config only for matching property keys
```

If a key exists only in the base file, the base value remains.

So:

```text
base refresh interval: 30s
dev refresh interval:  5s
final value:           5s
```

## Command-Line Overrides

Command-line properties start with `--`.

The test passes:

```text
--lesson06.lab.region=command-line-region
--lesson06.lab.security.require-https=false
```

Those become Environment properties.

They override the YAML files:

```text
base YAML:
    lesson06.lab.security.require-https = true

command line:
    lesson06.lab.security.require-https = false

final value:
    false
```

This is common in real deployments:

```text
java -jar app.jar --server.port=8081
```

The jar stays the same. The startup environment changes the value.

## @ConfigurationProperties

`@ConfigurationProperties` is from Spring Boot.

It binds a group of related Environment properties into a Java object.

This lesson has:

```java
@ConfigurationProperties("lesson06.lab")
public record Lesson06LabProperties(...)
```

That means:

```text
Take Environment keys starting with lesson06.lab
Bind them into this record
Register the finished object as a bean
```

Example:

```text
lesson06.lab.region
    -> region

lesson06.lab.refresh-interval
    -> refreshInterval

lesson06.lab.security.require-https
    -> security.requireHttps

lesson06.lab.security.token-audience
    -> security.tokenAudience
```

This is called relaxed binding. Spring Boot understands common naming variants:

```text
refresh-interval
refreshInterval
REFRESH_INTERVAL
```

The canonical property style in files is kebab case:

```text
lesson06.lab.refresh-interval
```

## Java Records In This Lesson

`Lesson06LabProperties` is a Java record:

```java
public record Lesson06LabProperties(
        String region,
        Duration refreshInterval,
        Security security
) {
}
```

A record is a compact Java type for immutable data.

The record header defines the data it carries:

```text
region
refreshInterval
security
```

Java then generates the usual boilerplate:

```text
private final fields
constructor
region()
refreshInterval()
security()
equals(...)
hashCode()
toString()
```

That is why code reads values like this:

```java
labProperties.region()
labProperties.refreshInterval()
labProperties.security()
```

instead of:

```java
labProperties.getRegion()
```

Records also support a compact constructor:

```java
public Lesson06LabProperties {
    if (region == null || region.isBlank()) {
        region = "local-default";
    }
}
```

This looks unusual because there is no parameter list. Java already knows the
parameters from the record header.

This compact constructor means:

```text
Whenever someone creates Lesson06LabProperties,
run this block before the final record fields are assigned.
```

Inside the compact constructor:

```java
region = "local-default";
```

does not mutate an existing field. It changes the constructor parameter value
that Java will store in the final `region` field after the constructor block
finishes.

For Lesson 06:

```text
Spring Boot binder
    -> reads lesson06.lab.* values from Environment
    -> converts them to Java types
    -> calls the Lesson06LabProperties compact constructor
    -> constructor applies defaults and guards
    -> Java stores the final record fields
```

The defaults exist because this Spring Boot lab shares one application context
across multiple lessons. If another lesson starts the app without Lesson 06's
special YAML files, these defaults keep the broader lab startable.

## @EnableConfigurationProperties

`@ConfigurationProperties` marks the type as bindable, but Lesson 06 still needs
to enable it.

This class does that:

```java
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(Lesson06LabProperties.class)
public class Lesson06Configuration {
}
```

The `@Configuration` part means:

```text
This class contributes configuration to the Spring ApplicationContext.
```

The `proxyBeanMethods = false` part means:

```text
Do not create a runtime proxy around this configuration class just to intercept
calls between @Bean methods.
```

Why would Spring ever proxy a configuration class?

Imagine this configuration:

```java
@Configuration
class ExampleConfiguration {

    @Bean
    UserService userService() {
        return new UserService(userRepository());
    }

    @Bean
    UserRepository userRepository() {
        return new UserRepository();
    }
}
```

In plain Java, this line:

```java
userRepository()
```

would call the method and create a new `UserRepository`.

But Spring beans are singleton by default. With the default
`proxyBeanMethods = true`, Spring creates a runtime proxy subclass for the
configuration class. When one `@Bean` method calls another `@Bean` method,
Spring intercepts the call and returns the managed singleton bean from the
container.

That mode is useful when:

```text
@Bean methods call other @Bean methods in the same configuration class.
```

Lesson 06 uses:

```java
@Configuration(proxyBeanMethods = false)
```

because `Lesson06Configuration` has no `@Bean` methods. It only enables
configuration properties binding:

```java
@EnableConfigurationProperties(Lesson06LabProperties.class)
```

There are no `@Bean` method calls for Spring to intercept, so a proxy would add
complexity without helping the lesson.

The practical rule:

```text
proxyBeanMethods = true
    -> full configuration mode
    -> Spring intercepts calls between @Bean methods
    -> useful when @Bean methods call each other

proxyBeanMethods = false
    -> lite configuration mode
    -> no interception between @Bean methods
    -> good when the configuration class only enables features
       or has independent @Bean methods
```

The process is:

```text
Spring finds Lesson06Configuration
    -> sees @EnableConfigurationProperties(Lesson06LabProperties.class)
    -> creates a Lesson06LabProperties bean
    -> binds lesson06.lab.* values into it
    -> other beans can inject Lesson06LabProperties
```

Without `@EnableConfigurationProperties`, this lesson's properties record would
just be a Java type with metadata. It would not automatically become an
injectable bean.

## Why Typed Binding?

You could read everything manually:

```java
String refresh = environment.getProperty("lesson06.lab.refresh-interval");
```

But then your code has to parse and validate strings.

Typed binding gives you:

```java
Duration refreshInterval
boolean requireHttps
```

In this lesson:

```text
YAML value:   5s
Java value:   Duration.ofSeconds(5)
```

That is the main reason application code usually prefers
`@ConfigurationProperties` over repeated `Environment.getProperty(...)` calls.

## Simple Validation

Lesson 06 uses constructor guards in the record:

```java
if (refreshInterval.isZero() || refreshInterval.isNegative()) {
    throw new IllegalArgumentException(...);
}
```

This is intentionally small.

The concept is:

```text
Configuration should be checked before business code relies on it.
```

Later, when the lab has validation dependencies, we can also show Bean
Validation annotations such as `@NotBlank`, `@Positive`, and `@Validated`.

## Environment Variables

This lesson does not set real OS environment variables in the test because doing
that portably inside a JVM test is awkward.

But Boot supports them.

The usual mapping is:

```text
lesson06.lab.region
    -> LESSON06_LAB_REGION

lesson06.lab.security.require-https
    -> LESSON06_LAB_SECURITY_REQUIREHTTPS
```

Operating systems often do not allow dots in environment variable names, so Boot
uses relaxed binding rules to connect environment-variable style names back to
property names.

## Mental Model

```text
YAML / command line / environment variables
    -> PropertySources
    -> Environment
    -> Binder
    -> Lesson06LabProperties bean
    -> application code
```

Or more concretely:

```text
lesson06-application.yml
lesson06-application-dev.yml
--lesson06.lab.region=command-line-region
    -> Environment contains final winning values
    -> Lesson06LabProperties receives typed values
    -> Lesson06ConfigurationInspector compares both views
```

## Main Ideas

- Externalized configuration keeps environment-specific values out of code.
- `Environment` is Spring's final property lookup view.
- YAML nesting becomes flat property names.
- Active profiles load profile-specific config files.
- Profile-specific values can override base values.
- Command-line properties can override file values.
- `@ConfigurationProperties` turns related properties into typed Java objects.
- `@EnableConfigurationProperties` registers the properties object as a bean.
- Typed configuration should be checked before normal application code uses it.

## Common Traps

- Thinking `application-dev.yml` is always loaded. It is loaded only when the
  `dev` profile is active.
- Thinking YAML nesting is how Java reads the value. Spring flattens YAML into
  property keys.
- Thinking `Environment.getProperty(...)` tells you where the value came from.
  It tells you the resolved value, not the full origin story.
- Scattering `@Value` everywhere for related settings. A typed
  `@ConfigurationProperties` object is usually clearer.
- Forgetting that command-line arguments can override file configuration.
- Forgetting that missing or invalid configuration should be handled before
  business code depends on it.

## Files

- `config/Lesson06Configuration.java`: enables the typed properties bean.
- `model/Lesson06LabProperties.java`: typed settings bound from
  `lesson06.lab.*`.
- `model/Lesson06ConfigurationSnapshot.java`: facts the test asserts.
- `support/Lesson06ConfigurationInspector.java`: compares Environment values
  with bound typed values.
- `Lesson06ExternalizedConfigurationTest.java`: starts Boot with config name,
  profile, and command-line overrides.
- `src/test/resources/lesson06-application.yml`: base lesson config.
- `src/test/resources/lesson06-application-dev.yml`: dev profile overrides.

## Code Walkthrough

The test starts Boot like this:

```text
SpringApplication(SpringBootLabApplication.class)
    -> WebApplicationType.NONE
    -> Banner.Mode.OFF
    -> run(
         --spring.config.name=lesson06-application,
         --spring.profiles.active=dev,
         --lesson06.lab.region=command-line-region,
         --lesson06.lab.security.require-https=false
       )
```

`WebApplicationType.NONE` means:

```text
Start a normal Spring ApplicationContext.
Do not start servlet or reactive web infrastructure.
Do not start an embedded web server.
```

Lesson 06 uses it because the lesson is about configuration loading, not HTTP.
Boot still loads YAML, profiles, command-line arguments, the `Environment`, and
`@ConfigurationProperties`.

`Banner.Mode.OFF` means:

```text
Do not print the Spring Boot startup banner in this test output.
```

It only removes output noise. It does not change configuration loading or bean
creation.

Boot then resolves values:

```text
region:
    base YAML:     file-default-region
    command line:  command-line-region
    final:         command-line-region

refresh interval:
    base YAML:     30s
    dev YAML:      5s
    final:         5s

require HTTPS:
    base YAML:     true
    command line:  false
    final:         false

token audience:
    base YAML:     base-audience
    dev YAML:      dev-audience
    final:         dev-audience
```

Then binding creates:

```text
Lesson06LabProperties(
    region = "command-line-region",
    refreshInterval = Duration.ofSeconds(5),
    security = Security(
        requireHttps = false,
        tokenAudience = "dev-audience"
    )
)
```

## Run The Lesson Test

From the repository root:

```bash
./mvnw -q -pl learning-apps/spring-boot-lab -Dtest=Lesson06ExternalizedConfigurationTest test
```

To run all Spring Boot lab tests:

```bash
./mvnw -q -pl learning-apps/spring-boot-lab test
```

## Official Docs

- [Spring Boot: Externalized Configuration](https://docs.spring.io/spring-boot/reference/features/external-config.html)
- [Spring Boot: Profiles](https://docs.spring.io/spring-boot/reference/features/profiles.html)
