# Lesson 02: Project Anatomy, Maven, Starters, And Classpath

## What This Solves

Spring Boot projects are easier to reason about when the build file stays small
and Boot manages compatible versions for the common libraries.

The core question for this lesson is:

```text
How does a short Maven POM become the classpath my application actually uses?
```

Lesson 01 showed Boot starting an application context. Lesson 02 steps one layer
lower: before Boot can start anything, Maven has to assemble compiled code,
resources, plugins, and dependencies.

The central chain is:

```text
pom.xml
    -> Maven resolves dependencies
    -> Maven compiles project code into target/
    -> Maven builds a classpath for the current run
    -> Java's ClassLoader searches that classpath
    -> Spring Boot uses the classes Java can load
```

So this lesson is not saying "Spring Boot magically knows every dependency."
The sharper idea is:

```text
Maven prepares the classpath.
Java loads classes from it.
Spring Boot builds on top of the classes Java can load.
```

## Lesson Coverage

This lesson covers six ideas:

1. How the root `pom.xml` registers `spring-boot-lab` as a Maven module.
2. How Boot dependency management chooses compatible dependency versions.
3. How `spring-boot-starter` brings core runtime libraries.
4. How direct helper dependencies like Lombok and Vavr are separate choices made
   by this lab.
5. How `spring-boot-starter-test` supplies test tools like JUnit and AssertJ.
6. Where the classpath comes from, how code uses it, and where class files live
   after packaging.

This lesson does not teach controllers, services, auto-configuration behavior,
or application context startup. Lesson 01 already introduced startup, and later
lessons add the application layers. Here we stay focused on the build-to-classpath
path.

## What Is The Classpath?

The classpath is the list of places Java can search when code refers to a class.
It is not always a folder. A classpath entry can be a directory of compiled
classes, a jar file, a test output folder, a resource folder, or a launcher
artifact created by the build tool. It is the assembled search space for one
specific run.

For this lesson's test, Maven builds a test classpath that contains:

```text
compiled spring-boot-lab main classes
compiled spring-boot-lab test classes
main dependency jars
test dependency jars
resource folders, when present
```

So yes, these are places Java can search. But be precise: Spring Boot does not
walk every folder on your machine. It searches through the active `ClassLoader`,
and that class loader is backed by the classpath for the current run.

There are two useful ways to inspect it:

```java
System.getProperty("java.class.path")
```

This shows the JVM's launch-time classpath roots. Under Maven Surefire, this may
sometimes point to a launcher jar instead of listing every dependency directly.

```java
classLoader.getResource("org/springframework/boot/SpringApplication.class")
```

This asks the active class loader where a specific class file was found. That is
often the clearest view because it points to the actual jar or compiled output
folder that supplied the class.

When the code checks for this class:

```text
org.springframework.boot.SpringApplication
```

it is asking:

```text
Can Java find SpringApplication on the classpath Maven prepared?
```

If the answer is yes, the relevant dependency is present. If the answer is no,
the dependency is missing, scoped incorrectly, or not part of the current run's
classpath.

## How Do We Use The Classpath?

Most application code uses the classpath indirectly. When you write an import
and compile code like this:

```java
import org.springframework.boot.SpringApplication;
```

the compiler and JVM need `SpringApplication` to be on the classpath. If it is
missing, compilation or startup fails.

Frameworks also use the classpath. Spring Boot uses classpath contents to decide
which auto-configuration may apply. Test runners use it to find tests. Resource
loading uses it to find files packaged with the app.

This lesson uses the classpath explicitly for diagnostics:

```text
read java.class.path
    -> see the JVM classpath roots

ask ClassLoader for SpringApplication.class
    -> see the jar or folder that supplied SpringApplication
```

So the code is not only asking "is this dependency available?" It now also asks
"where did Java find the class?"

Spring Boot enters after that. It can component-scan, auto-configure, and create
beans only from classes and resources the active class loader can load.

## What Does It Look Like Physically?

During a Maven test run, classpath entries are usually visible as real folders
and jars on disk:

```text
learning-apps/spring-boot-lab/target/classes
learning-apps/spring-boot-lab/target/test-classes
~/.m2/repository/org/springframework/boot/spring-boot/.../spring-boot-....jar
~/.m2/repository/org/junit/jupiter/junit-jupiter-api/.../junit-jupiter-api-....jar
```

## What Is The Target Folder?

In Maven, `target/` is the module's build output directory. Source code lives
under `src/`; generated and compiled output goes under `target/`.

For this lab, the useful pieces usually look like this:

```text
learning-apps/spring-boot-lab/target/
├── classes/
│   └── compiled src/main/java classes and copied src/main/resources files
├── test-classes/
│   └── compiled src/test/java classes and copied src/test/resources files
├── surefire-reports/
│   └── test result files from Maven Surefire
├── generated-sources/
│   └── generated main sources, such as annotation-processor output
├── generated-test-sources/
│   └── generated test sources
├── maven-status/
│   └── compiler bookkeeping used for incremental builds
├── spring-boot-lab-0.0.1-SNAPSHOT.jar
│   └── the packaged artifact after mvn package
└── spring-boot-lab-0.0.1-SNAPSHOT.jar.original
    └── the plain jar before Spring Boot repackage rewrites it
```

In practice, `target/` is useful because it answers build questions:

- Did my main code compile? Look in `target/classes`.
- Did my test code compile? Look in `target/test-classes`.
- What did my tests report? Look in `target/surefire-reports`.
- What jar will I run or publish? Look for the final jar under `target/`.
- What did annotation processors generate? Look in `target/generated-sources`.
- Is my build output stale or suspicious? Run `mvn clean` to delete `target/`
  and rebuild from source.

This lesson now checks two project classes to make `target/` visible:

```text
SpringBootLabApplication.class
    -> should be found under target/classes during tests

Lesson02ProjectAnatomyTest.class
    -> should be found under target/test-classes during tests
```

That is why `target/` feels heavily used in real work: Maven uses it as the
workspace between source code and the final thing you run, test, package, or
publish.

In a Spring Boot executable jar, the layout is different. The packaged artifact
contains application classes and dependency jars inside the outer jar:

```text
spring-boot-lab-0.0.1-SNAPSHOT.jar
├── META-INF/
├── org/springframework/boot/loader/
└── BOOT-INF/
    ├── classes/
    │   └── io/github/fengyanglin09/springbootlab/...
    └── lib/
        ├── spring-boot-....jar
        ├── spring-context-....jar
        └── ...
```

For an executable war, the same idea uses the traditional web archive folders:

```text
example.war
├── META-INF/
├── org/springframework/boot/loader/
└── WEB-INF/
    ├── classes/
    │   └── io/github/fengyanglin09/springbootlab/...
    ├── lib/
    │   └── dependency jars used by the app
    └── lib-provided/
        └── dependencies supplied by an external servlet container
```

You can inspect a packaged jar or war with:

```bash
jar tf target/spring-boot-lab-0.0.1-SNAPSHOT.jar
```

The key difference: in Maven tests, the classpath usually points at separate
folders and dependency jars. In an executable Boot jar, Boot's launcher builds a
class loader that can search `BOOT-INF/classes` and nested jars under
`BOOT-INF/lib`.

## Mental Model

The POM is a packing list.

```text
parent build / BOM = compatible version catalog
starter dependency = curated bundle of useful libraries
direct dependency  = one library the lab chooses explicitly
plugin             = build-time tool
classpath          = what compiled code can actually see
```

You do not usually hand-pick every Spring Framework, logging, JSON, test, or
support-library version. Boot's dependency management keeps those pieces aligned.

## Core Vocabulary

- Maven module: one buildable project with its own `pom.xml`.
- Parent POM: shared build settings inherited by modules.
- BOM: a dependency version catalog imported through dependency management.
- Dependency management: version rules that let child modules omit many
  dependency versions.
- Starter: a dependency that brings a useful set of related libraries.
- Direct dependency: a library this module declares explicitly.
- Transitive dependency: a library brought in by another dependency.
- Plugin: a build-time tool such as the compiler or Boot repackage plugin.
- Classpath: the compiled classes, resources, and dependency jars Java can
  search when code runs.
- Class loader: the Java object that performs the classpath search at runtime.
- Target directory: Maven's build output folder for compiled classes, test
  output, reports, generated sources, and packaged artifacts.

## Concept Map

```text
root pom.xml
    -> imports spring-boot-dependencies BOM
    -> lists learning-apps/spring-boot-lab as a module

spring-boot-lab/pom.xml
    -> declares spring-boot-starter
    -> declares helper libraries: lombok, vavr
    -> declares spring-boot-starter-test
    -> configures compiler plugin
    -> binds spring-boot:repackage for an executable Boot jar

Maven
    -> resolves versions
    -> compiles code
    -> writes main output to target/classes
    -> writes test output to target/test-classes
    -> builds test classpath
    -> Java ClassLoader searches that classpath
    -> Spring Boot can use classes Java can load
    -> runs lesson test
    -> packages BOOT-INF/classes and BOOT-INF/lib for java -jar
```

## Main Ideas

- A Spring Boot starter is not magic; it is a dependency that brings other
  dependencies.
- Boot dependency management is the reason many Spring dependencies do not need
  explicit versions in a module POM.
- Direct helper libraries like Lombok and Vavr should be added intentionally
  because they are choices this lab made, not automatic Boot features.
- Build plugins shape compilation and packaging. In this lab,
  `spring-boot:repackage` creates the executable jar layout shown above.
- The classpath is the concrete result of the POM after Maven resolves it.
- Java's `ClassLoader` performs the actual class search; Spring Boot relies on
  that loaded classpath for scanning and auto-configuration.

## Decision Rules

- Let Boot manage Spring and common third-party versions unless there is a clear
  compatibility reason to override.
- Use a starter when the lesson needs the whole capability area.
- Use a narrow dependency when the lesson needs one library, not a full stack.
- Keep helper libraries subordinate to the Spring Boot lesson objective.
- Prefer plain JUnit for this lesson because the classpath is the subject, not
  context startup.

## Common Traps

- Adding explicit versions for Spring libraries already managed by Boot.
- Treating a starter as a runtime feature instead of a dependency bundle.
- Adding a broad starter when one narrow dependency would be clearer.
- Assuming Lombok changes runtime behavior. It mostly generates code at compile
  time.
- Letting helper libraries become the lesson before the Boot idea is learned.

## Files

- `pom.xml`: declares the module's starters, helper libraries, and build plugins.
- `support/Lesson02ProjectClasspathInspector.java`: checks which classes Maven made
  available and where the class loader found them.
- `model/Lesson02ClasspathEntry.java`: records one expected classpath
  capability, the class resource path, and the jar or folder that supplied it.
- `model/Lesson02DependencySnapshot.java`: groups the classpath checks into
  lesson concepts, records the JVM classpath roots, and proves target output is
  visible during tests.
- `Lesson02ProjectAnatomyTest.java`: proves the starter, helper, and test
  dependencies are available.

## Code Walkthrough

This lesson uses a plain JUnit test:

```text
Maven resolves dependencies
    -> Maven compiles src/main/java into target/classes
    -> Maven compiles src/test/java into target/test-classes
    -> Maven builds the test classpath
    -> java.class.path exposes launch classpath roots
    -> Java ClassLoader searches those roots
    -> Lesson02ProjectClasspathInspector checks known classes
    -> each classpath entry records where that class was found
    -> Lesson02DependencySnapshot groups those checks by lesson reason
```

No Spring context is needed here. That is intentional. If the lesson is about
the classpath, starting the whole application would be extra noise.

## Run The Lesson Test

```bash
./mvnw -pl learning-apps/spring-boot-lab -Dtest=Lesson02ProjectAnatomyTest test
```

## What Comes Next

Lesson 03 returns to runtime behavior. It studies application startup,
`SpringApplication`, command-line arguments, and the application context in more
detail.

## Official Docs

- [Build Systems](https://docs.spring.io/spring-boot/reference/using/build-systems.html)
- [Dependency Versions](https://docs.spring.io/spring-boot/appendix/dependency-versions/)
- [Spring Boot Maven Plugin](https://docs.spring.io/spring-boot/maven-plugin/)
- [Executable Jar Format](https://docs.spring.io/spring-boot/specification/executable-jar/)
- [Nested Jars](https://docs.spring.io/spring-boot/specification/executable-jar/nested-jars.html)
- [Launching Executable Jars](https://docs.spring.io/spring-boot/specification/executable-jar/launching.html)
