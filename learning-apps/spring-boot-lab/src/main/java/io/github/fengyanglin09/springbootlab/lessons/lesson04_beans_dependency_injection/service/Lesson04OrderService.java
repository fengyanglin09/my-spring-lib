package io.github.fengyanglin09.springbootlab.lessons.lesson04_beans_dependency_injection.service;

import io.github.fengyanglin09.springbootlab.lessons.lesson04_beans_dependency_injection.model.Lesson04OrderReceipt;
import io.github.fengyanglin09.springbootlab.lessons.lesson04_beans_dependency_injection.model.Lesson04OrderRequest;
import io.github.fengyanglin09.springbootlab.lessons.lesson04_beans_dependency_injection.support.Lesson04OrderNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The main application service for Lesson 04.
 *
 * <p>An application service coordinates a small use case. Here the use case is
 * tiny on purpose: accept an order request, get an order reference, and format a
 * receipt. The business behavior is deliberately simple so the Spring wiring is
 * easy to see.</p>
 */
/*
 * @Service is from Spring Framework.
 *
 * @Service is a specialized form of @Component. Component scanning treats it as
 * "create a bean of this class", and human readers treat it as "this class
 * holds application/business workflow logic."
 *
 * Because this class is under SpringBootLabApplication's root package, Spring
 * finds it during component scanning and registers a bean named
 * "lesson04OrderService".
 */
@Service
/*
 * @RequiredArgsConstructor is from Lombok, not Spring.
 *
 * Lombok generates a constructor with one parameter for each final field:
 *
 * Lesson04OrderService(
 *     Lesson04OrderNumberGenerator orderNumberGenerator,
 *     Lesson04ReceiptFormatter receiptFormatter
 * )
 *
 * Spring sees that constructor and supplies matching beans from the
 * ApplicationContext. That is constructor injection.
 *
 * Without Lombok, we would write the constructor by hand. The Spring behavior
 * would be the same; Lombok only removes boilerplate.
 */
@RequiredArgsConstructor
public class Lesson04OrderService {

    /*
     * This field is final because the service should not switch collaborators
     * after it has been created. Constructor injection and final fields work
     * nicely together: Spring supplies the dependency once, then the service
     * keeps a stable reference.
     *
     * Spring resolves this by type. It looks for a bean whose type is
     * Lesson04OrderNumberGenerator. Lesson04BeanConfiguration provides exactly
     * one, so there is no ambiguity.
     */
    private final Lesson04OrderNumberGenerator orderNumberGenerator;

    /*
     * This dependency is also resolved by type. Lesson04ReceiptFormatter is a
     * @Component bean, so Spring can inject it into this constructor too.
     */
    private final Lesson04ReceiptFormatter receiptFormatter;

    public Lesson04OrderReceipt accept(Lesson04OrderRequest request) {
        /*
         * There is no "new Lesson04OrderNumberGenerator" and no
         * "new Lesson04ReceiptFormatter" here. That absence is important.
         *
         * Lesson04OrderService focuses on its workflow. Spring handled object
         * creation and wiring during startup.
         */
        String reference = orderNumberGenerator.nextReference(request);
        return receiptFormatter.format(request, reference);
    }
}
