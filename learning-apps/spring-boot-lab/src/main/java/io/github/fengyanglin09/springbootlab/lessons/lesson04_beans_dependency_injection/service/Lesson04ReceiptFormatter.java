package io.github.fengyanglin09.springbootlab.lessons.lesson04_beans_dependency_injection.service;

import io.github.fengyanglin09.springbootlab.lessons.lesson04_beans_dependency_injection.model.Lesson04OrderReceipt;
import io.github.fengyanglin09.springbootlab.lessons.lesson04_beans_dependency_injection.model.Lesson04OrderRequest;
import org.springframework.stereotype.Component;

/**
 * Turns order data into the receipt object returned by the lesson service.
 *
 * <p>This class exists as a separate collaborator so dependency injection is
 * visible. If all the logic lived in Lesson04OrderService, there would be less
 * for Spring to wire together.</p>
 */
/*
 * @Component is from Spring Framework.
 *
 * It is the most general stereotype annotation. It tells Spring's component
 * scanner: "register this class as a bean." The default bean name is the class
 * name with a lowercase first letter, so this becomes
 * "lesson04ReceiptFormatter".
 */
@Component
public class Lesson04ReceiptFormatter {

    public Lesson04OrderReceipt format(Lesson04OrderRequest request, String orderReference) {
        /*
         * This method is ordinary Java. Spring's main job is to create and
         * connect the object. After that, method calls are just normal method
         * calls between Java objects.
         */
        return new Lesson04OrderReceipt(
                orderReference,
                request.customerId(),
                request.itemCount(),
                "Accepted %d item(s) for %s".formatted(request.itemCount(), request.customerId())
        );
    }
}
