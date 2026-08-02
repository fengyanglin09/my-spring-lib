package io.github.fengyanglin09.springbootlab.lessons.lesson04_beans_dependency_injection.support;

import io.github.fengyanglin09.springbootlab.lessons.lesson04_beans_dependency_injection.model.Lesson04OrderRequest;

/**
 * A small collaborator used by Lesson04OrderService.
 *
 * <p>This interface is a normal Java type. On its own, it is not a Spring bean.
 * Lesson04BeanConfiguration will create an implementation and expose that
 * implementation as a bean with an {@code @Bean} method.</p>
 */
@FunctionalInterface
public interface Lesson04OrderNumberGenerator {

    /**
     * Creates an order reference for the given request.
     *
     * <p>The exact format is not the important part. The important part is that
     * Lesson04OrderService depends on this interface, and Spring injects the
     * implementation at startup.</p>
     */
    String nextReference(Lesson04OrderRequest request);
}
