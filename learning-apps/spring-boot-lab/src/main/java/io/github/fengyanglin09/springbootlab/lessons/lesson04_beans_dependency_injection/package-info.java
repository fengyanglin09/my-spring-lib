/**
 * Lesson 04 objective: understand how Spring creates beans and connects them
 * through dependency injection.
 *
 * <p>Study path:</p>
 *
 * <ol>
 *     <li>See that a bean is a Java object managed by the Spring
 *     ApplicationContext.</li>
 *     <li>Use {@code @Component} and {@code @Service} to let component scanning
 *     find application classes automatically.</li>
 *     <li>Use {@code @Configuration} and {@code @Bean} to register a plain Java
 *     object explicitly.</li>
 *     <li>Use constructor injection so Spring supplies required collaborators
 *     when it creates a service.</li>
 *     <li>Use Lombok {@code @RequiredArgsConstructor} only to remove constructor
 *     boilerplate; Spring still performs the actual injection.</li>
 * </ol>
 */
package io.github.fengyanglin09.springbootlab.lessons.lesson04_beans_dependency_injection;
