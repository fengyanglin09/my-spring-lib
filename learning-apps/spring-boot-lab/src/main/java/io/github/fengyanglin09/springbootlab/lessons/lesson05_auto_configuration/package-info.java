/**
 * Lesson 05 objective: understand Spring Boot auto-configuration as conditional
 * default bean creation.
 *
 * <p>Study path:</p>
 *
 * <ol>
 *     <li>Use a tiny {@code ApplicationContextRunner} context instead of a full
 *     application startup.</li>
 *     <li>Import one lesson auto-configuration explicitly.</li>
 *     <li>Observe a default bean created by {@code @Bean}.</li>
 *     <li>Use {@code @ConditionalOnClass} to ask whether a pretend library
 *     marker class is available on the classpath.</li>
 *     <li>Use {@code @ConditionalOnMissingBean} to show Boot backing off when
 *     the application provides its own bean.</li>
 *     <li>Read {@code ConditionEvaluationReport} as Boot's explanation of why
 *     conditions matched or did not match.</li>
 * </ol>
 */
package io.github.fengyanglin09.springbootlab.lessons.lesson05_auto_configuration;
