package io.github.fengyanglin09.springbootlab.lessons.lesson05_auto_configuration.support;

import io.github.fengyanglin09.springbootlab.lessons.lesson05_auto_configuration.model.Lesson05AutoConfigurationSnapshot;
import io.github.fengyanglin09.springbootlab.lessons.lesson05_auto_configuration.service.Lesson05GreetingService;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.Map;

/**
 * Reads the result of a small auto-configuration experiment.
 *
 * <p>The key Spring Boot type here is {@link ConditionEvaluationReport}. Boot
 * records condition outcomes while it decides whether conditional configuration
 * should apply. In a real app, you often see this information through the
 * condition evaluation report in logs or Actuator. In this lesson, we read it
 * directly so the test can explain what happened.</p>
 */
public class Lesson05ConditionReportInspector {

    public Lesson05AutoConfigurationSnapshot inspect(
            ConfigurableApplicationContext context,
            Class<?> autoConfigurationClass
    ) {
        /*
         * The auto-configuration class can itself be registered as a Spring
         * configuration bean when its class-level conditions match.
         *
         * That means there can be two different objects involved:
         *
         * 1. Lesson05GreetingAutoConfiguration
         *        -> configuration bean
         *        -> source of @Bean methods
         *        -> not the greeting service
         *
         * 2. Lesson05DefaultGreetingService
         *        -> service bean returned by a @Bean method
         *        -> registered under the bean name "lesson05GreetingService"
         *
         * This is similar to a cookbook and a meal: the configuration bean is
         * the cookbook with recipes; the @Bean method can produce the meal.
         */
        Map<String, ?> autoConfigurationBeans = context.getBeansOfType(autoConfigurationClass);
        String autoConfigurationBeanName = autoConfigurationBeans.keySet().stream()
                .findFirst()
                .orElse("");

        /*
         * getBeansOfType(...) asks the context for every bean assignable to the
         * Lesson05GreetingService interface.
         *
         * This is useful for the lesson because @ConditionalOnMissingBean also
         * thinks in terms of type: "only create the default if no
         * Lesson05GreetingService bean already exists."
         */
        Map<String, Lesson05GreetingService> greetingServices =
                context.getBeansOfType(Lesson05GreetingService.class);

        String firstBeanName = greetingServices.keySet().stream()
                .findFirst()
                .orElse("");

        Lesson05GreetingService firstService = greetingServices.values().stream()
                .findFirst()
                .orElse(null);

        /*
         * This is the line that is easy to misunderstand:
         *
         * context.containsBean("lesson05GreetingService")
         *
         * "lesson05GreetingService" is NOT a Java class name.
         * There is no class named lesson05GreetingService.
         * It is also NOT discovered through @Service or @Component.
         *
         * It is a Spring bean name.
         *
         * The bean name comes from the @Bean method in the lesson
         * auto-configuration:
         *
         * @Bean
         * Lesson05GreetingService lesson05GreetingService() { ... }
         *
         * By default, Spring uses the @Bean method name as the bean name. If
         * that method runs, the context contains a bean named
         * "lesson05GreetingService". The actual object stored under that name is
         * a Lesson05DefaultGreetingService instance.
         */
        boolean autoConfiguredDefaultBeanPresent = context.containsBean("lesson05GreetingService");

        /*
         * This bean name comes from the test's withBean(...) call, not from an
         * annotation:
         *
         * .withBean("lesson05CustomGreetingService", ...)
         *
         * That simulates "the application already provided its own bean."
         */
        boolean customUserBeanPresent = context.containsBean("lesson05CustomGreetingService");

        /*
         * ConditionEvaluationReport is from Spring Boot autoconfigure.
         *
         * Boot writes to this report while it evaluates conditions such as
         * @ConditionalOnClass and @ConditionalOnMissingBean. The report is the
         * "why" behind the final set of beans.
         */
        ConditionEvaluationReport report = ConditionEvaluationReport.find(context.getBeanFactory());

        /*
         * The source key is usually the fully qualified configuration class name
         * for class-level conditions. Method-level conditions use a source that
         * starts with the same class name and continues with the bean method.
         *
         * Collecting every source that starts with the auto-configuration class
         * name lets the lesson include both:
         * - did the auto-configuration class match?
         * - did the @Bean method match or back off?
         */
        String autoConfigurationSource = autoConfigurationClass.getName();
        List<String> conditionMessages = report.getConditionAndOutcomesBySource()
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith(autoConfigurationSource))
                .flatMap(entry -> entry.getValue().stream())
                .map(conditionAndOutcome -> conditionAndOutcome.getCondition()
                        .getClass()
                        .getSimpleName()
                        + ": "
                        + conditionAndOutcome.getOutcome().getMessage())
                .filter(message -> !message.isBlank())
                .toList();

        boolean autoConfigurationClassMatched = report.getConditionAndOutcomesBySource()
                .getOrDefault(autoConfigurationSource, new ConditionEvaluationReport.ConditionAndOutcomes())
                .isFullMatch();

        return new Lesson05AutoConfigurationSnapshot(
                !autoConfigurationBeans.isEmpty(),
                autoConfigurationBeanName,
                autoConfiguredDefaultBeanPresent,
                customUserBeanPresent,
                greetingServices.size(),
                firstBeanName,
                firstService == null ? "" : firstService.getClass().getSimpleName(),
                firstService == null ? "" : firstService.greet("Ada"),
                autoConfigurationClassMatched,
                conditionMessages
        );
    }
}
