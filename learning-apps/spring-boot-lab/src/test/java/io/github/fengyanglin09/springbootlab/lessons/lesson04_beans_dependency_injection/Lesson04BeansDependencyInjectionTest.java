package io.github.fengyanglin09.springbootlab.lessons.lesson04_beans_dependency_injection;

import io.github.fengyanglin09.springbootlab.lessons.lesson04_beans_dependency_injection.model.Lesson04BeanGraphSnapshot;
import io.github.fengyanglin09.springbootlab.lessons.lesson04_beans_dependency_injection.support.Lesson04BeanGraphInspector;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Lesson 04's executable proof.
 *
 * <p>This test lets Spring Boot start the normal application context, then
 * checks that component scanning, {@code @Bean} methods, and constructor
 * injection produced a working object graph.</p>
 */
/*
 * @SpringBootTest is from Spring Boot Test.
 *
 * It starts a real Spring Boot ApplicationContext for the test. That means the
 * same core machinery used by the app is active here:
 *
 * - component scanning finds @Component, @Service, and @Configuration classes
 * - @Bean methods are called and registered
 * - constructor injection supplies dependencies
 *
 * This is heavier than a plain unit test, but it is the right fit for a lesson
 * whose main subject is "what did Spring put in the context?"
 */
@SpringBootTest
class Lesson04BeansDependencyInjectionTest {

    /*
     * @Autowired is from Spring Framework.
     *
     * It asks Spring to inject a bean into this test field. Field injection is
     * acceptable in small tests because JUnit creates the test class and Spring
     * then fills in the field.
     *
     * In application code, this lesson uses constructor injection instead,
     * because constructor injection makes required dependencies visible and
     * allows fields to be final.
     */
    @Autowired
    private Lesson04BeanGraphInspector inspector;

    @Test
    void componentScanBeanMethodsAndConstructorInjectionCreateWorkingBeanGraph() {
        Lesson04BeanGraphSnapshot snapshot = inspector.inspect();

        /*
         * This proves Spring found the classes marked with stereotypes:
         *
         * - @Service Lesson04OrderService
         * - @Component Lesson04ReceiptFormatter
         * - @Configuration Lesson04BeanConfiguration
         */
        assertThat(snapshot.componentScanRegisteredStereotypeBeans()).isTrue();

        /*
         * This proves the @Bean method created the order-number generator bean.
         * The generator is a plain Java lambda, not a @Component class.
         */
        assertThat(snapshot.beanMethodRegisteredCollaborator()).isTrue();

        /*
         * This proves constructor injection connected the service to both of
         * its dependencies. If Spring had not injected them, this service call
         * could not produce the expected receipt.
         */
        assertThat(snapshot.constructorInjectionProducedWorkingService()).isTrue();
        assertThat(snapshot.receipt().orderReference()).isEqualTo("L04-ADA-03");
    }
}
