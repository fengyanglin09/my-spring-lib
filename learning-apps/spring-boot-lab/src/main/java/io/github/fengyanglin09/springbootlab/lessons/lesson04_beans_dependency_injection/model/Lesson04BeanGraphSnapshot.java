package io.github.fengyanglin09.springbootlab.lessons.lesson04_beans_dependency_injection.model;

/**
 * A small read-only report about the beans used in Lesson 04.
 *
 * <p>The lesson code could assert directly against the ApplicationContext, but
 * this snapshot gives the test named facts to check. That makes the lesson read
 * like a story: Spring found these classes, created these beans, connected the
 * service, and the service was able to do useful work.</p>
 */
public record Lesson04BeanGraphSnapshot(
        boolean serviceBeanFound,
        boolean formatterBeanFound,
        boolean configurationBeanFound,
        boolean beanMethodBeanFound,
        int orderNumberGeneratorBeanCount,
        Lesson04OrderReceipt receipt
) {

    /**
     * Component scanning should find classes annotated with Spring stereotypes.
     *
     * <p>In this lesson, {@code @Service}, {@code @Component}, and
     * {@code @Configuration} are all stereotype-style annotations that make a
     * class visible to Spring's component scanner. With Spring's default
     * singleton scope, the context creates one instance of each of these beans
     * for this running ApplicationContext.</p>
     */
    public boolean componentScanRegisteredStereotypeBeans() {
        return serviceBeanFound && formatterBeanFound && configurationBeanFound;
    }

    /**
     * The {@code @Bean} method should register one plain Java collaborator.
     *
     * <p>The order number generator is not annotated with {@code @Component}.
     * It becomes a Spring bean because a {@code @Configuration} class has a
     * method annotated with {@code @Bean} that returns it.</p>
     */
    public boolean beanMethodRegisteredCollaborator() {
        return beanMethodBeanFound && orderNumberGeneratorBeanCount == 1;
    }

    /**
     * The service should work because Spring supplied its constructor
     * dependencies.
     *
     * <p>This is the practical proof of dependency injection: the test never
     * calls {@code new Lesson04OrderService(...)}. Spring creates the service,
     * finds the constructor arguments it needs, and passes those beans in.</p>
     */
    public boolean constructorInjectionProducedWorkingService() {
        return receipt != null
                && "L04-ADA-03".equals(receipt.orderReference())
                && "ada".equals(receipt.customerId())
                && receipt.itemCount() == 3
                && receipt.message().contains("Accepted 3 item(s) for ada");
    }
}
