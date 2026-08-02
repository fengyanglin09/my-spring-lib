package io.github.fengyanglin09.springbootlab.lessons.lesson01_why_spring_boot.support;

import io.github.fengyanglin09.springbootlab.lessons.lesson01_why_spring_boot.model.Lesson01BootSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * A small lesson bean that lets the test observe what Boot assembled.
 *
 * <p>This class exists for learning, not because production code normally asks
 * the context how many beans it has. It gives lesson 01 something concrete to
 * inspect without introducing HTTP, persistence, or configuration yet.</p>
 */
@Component
// Lombok generates the constructor Spring uses for dependency injection.
@RequiredArgsConstructor
public class Lesson01BootInspector {

    // Boot creates the ApplicationContext before this bean can be constructed.
    private final ApplicationContext applicationContext;

    // Environment shows which profiles and external settings Boot detected at startup.
    private final Environment environment;

    public Lesson01BootSnapshot inspect() {
        return new Lesson01BootSnapshot(
                applicationContext.getId(),
                applicationContext.getBeanDefinitionCount(),
                List.of(environment.getActiveProfiles()),
                // Default bean names come from class names with a lowercase first letter.
                applicationContext.containsBean("springBootLabApplication"),
                applicationContext.containsBean("lesson01BootInspector")
        );
    }
}
