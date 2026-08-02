package io.github.fengyanglin09.springbootlab.lessons.lesson04_beans_dependency_injection.support;

import io.github.fengyanglin09.springbootlab.lessons.lesson04_beans_dependency_injection.model.Lesson04BeanGraphSnapshot;
import io.github.fengyanglin09.springbootlab.lessons.lesson04_beans_dependency_injection.model.Lesson04OrderReceipt;
import io.github.fengyanglin09.springbootlab.lessons.lesson04_beans_dependency_injection.model.Lesson04OrderRequest;
import io.github.fengyanglin09.springbootlab.lessons.lesson04_beans_dependency_injection.service.Lesson04OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Makes the Lesson 04 bean graph observable for tests.
 *
 * <p>Normal production code should not frequently ask the ApplicationContext
 * whether beans exist. Production code should usually receive dependencies
 * directly through constructors. This inspector exists because a learning test
 * needs to prove what Spring registered.</p>
 */
/*
 * @Component makes this inspector itself a Spring bean. The test can then ask
 * Spring for this inspector and use it to inspect the rest of the lesson graph.
 */
@Component
/*
 * Lombok generates a constructor for the final fields below. Spring uses that
 * generated constructor to inject ApplicationContext and Lesson04OrderService.
 */
@RequiredArgsConstructor
public class Lesson04BeanGraphInspector {

    /*
     * ApplicationContext is from Spring Framework.
     *
     * It is the container that holds bean definitions and created bean objects.
     * You can think of it as Spring's object registry for the running app.
     * Most beans use singleton scope by default, meaning one instance inside
     * this ApplicationContext.
     *
     * This lesson uses ApplicationContext only for inspection:
     * - containsBean("lesson04OrderService") asks whether a bean with that name
     *   exists.
     * - getBeansOfType(Lesson04OrderNumberGenerator.class) asks how many beans
     *   are assignable to that Java type.
     */
    private final ApplicationContext applicationContext;

    /*
     * Lesson04OrderService is our main lesson service. Spring injects it here
     * because it is an @Service bean and this inspector's generated constructor
     * asks for it.
     */
    private final Lesson04OrderService orderService;

    public Lesson04BeanGraphSnapshot inspect() {
        /*
         * Lesson04OrderRequest is not a bean. It is short-lived input data, so
         * this method creates it directly. That distinction matters:
         *
         * Spring beans: long-lived collaborators managed by the container.
         * Plain objects: request data, results, values, and temporary objects.
         */
        Lesson04OrderRequest request = new Lesson04OrderRequest("ada", 3);

        /*
         * This service call proves the whole object graph works:
         *
         * Lesson04BeanGraphInspector
         *     -> calls Lesson04OrderService
         *     -> service calls Lesson04OrderNumberGenerator
         *     -> service calls Lesson04ReceiptFormatter
         *     -> formatter returns Lesson04OrderReceipt
         *
         * The inspector did not create the service or its collaborators. Spring
         * already did that while starting the ApplicationContext.
         */
        Lesson04OrderReceipt receipt = orderService.accept(request);

        return new Lesson04BeanGraphSnapshot(
                /*
                 * containsBean(...) checks by bean name. The default name for a
                 * stereotype bean is usually the class name with the first
                 * letter lowercased.
                 */
                applicationContext.containsBean("lesson04OrderService"),
                applicationContext.containsBean("lesson04ReceiptFormatter"),
                applicationContext.containsBean("lesson04BeanConfiguration"),
                /*
                 * This one comes from the @Bean method name in
                 * Lesson04BeanConfiguration.
                 */
                applicationContext.containsBean("lesson04OrderNumberGenerator"),
                applicationContext.getBeansOfType(Lesson04OrderNumberGenerator.class).size(),
                receipt
        );
    }
}
