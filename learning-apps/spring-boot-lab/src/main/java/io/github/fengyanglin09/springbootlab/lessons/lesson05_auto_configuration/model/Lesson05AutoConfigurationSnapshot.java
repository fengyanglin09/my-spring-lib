package io.github.fengyanglin09.springbootlab.lessons.lesson05_auto_configuration.model;

import java.util.List;

/**
 * A small report about one auto-configuration experiment.
 *
 * <p>Lesson 05 uses several tiny contexts instead of one full app context. Each
 * context represents a different situation: no user bean, user bean present, or
 * a required class missing from the classpath. This snapshot lets the test ask
 * named questions about what happened in each context.</p>
 */
public record Lesson05AutoConfigurationSnapshot(
        boolean autoConfigurationBeanPresent,
        String autoConfigurationBeanName,
        boolean defaultGreetingBeanPresent,
        boolean customGreetingBeanPresent,
        int greetingServiceBeanCount,
        String greetingBeanName,
        String greetingServiceImplementation,
        String greetingMessage,
        boolean autoConfigurationClassMatched,
        List<String> conditionMessages
) {

    /**
     * When the class-level conditions match, the auto-configuration class itself
     * is registered as a configuration bean.
     *
     * <p>This configuration bean is not the greeting service. It is the object
     * Spring uses as the source of {@code @Bean} methods. One configuration bean
     * can define many other beans.</p>
     */
    public boolean autoConfigurationWasRegisteredAsConfigurationBean() {
        return autoConfigurationBeanPresent && !autoConfigurationBeanName.isBlank();
    }

    /**
     * The default bean should appear when the auto-configuration matches and no
     * application-defined bean of the same type already exists.
     */
    public boolean autoConfigurationCreatedDefaultBean() {
        return defaultGreetingBeanPresent
                && !customGreetingBeanPresent
                && greetingServiceBeanCount == 1
                && "Lesson05DefaultGreetingService".equals(greetingServiceImplementation)
                && greetingMessage.contains("auto-configured greeting");
    }

    /**
     * Backing off means the default bean was not created because the application
     * already supplied a bean of that type.
     */
    public boolean userBeanMadeAutoConfigurationBackOff() {
        return !defaultGreetingBeanPresent
                && customGreetingBeanPresent
                && greetingServiceBeanCount == 1
                && "Lesson05CustomGreetingService".equals(greetingServiceImplementation)
                && greetingMessage.contains("custom greeting");
    }

    /**
     * If the classpath condition does not match, the auto-configuration should
     * not create the default bean.
     */
    public boolean missingClasspathInputPreventedDefaultBean() {
        return !autoConfigurationClassMatched
                && !defaultGreetingBeanPresent
                && greetingServiceBeanCount == 0;
    }

    /**
     * A condition report is Boot's explanation of why conditional configuration
     * matched or did not match.
     */
    public boolean conditionReportWasRecorded() {
        return !conditionMessages.isEmpty();
    }
}
