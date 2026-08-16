package io.github.fengyanglin09.springbootlab.lessons.lesson08_error_handling.service;

/**
 * Domain exception for inventory that cannot satisfy a valid request.
 *
 * <p>This exception stores custom data in addition to the normal exception
 * message. When the service throws this exception, Spring MVC later passes the
 * same exception object to Lesson08ApiExceptionHandler, so the handler can read
 * sku, requestedQuantity, and availableQuantity through the getter methods.</p>
 */
public class Lesson08OutOfStockException extends RuntimeException {

    /*
     * These fields are custom exception data.
     *
     * They are different from an exception "cause". In Java, the cause is
     * another Throwable available through getCause(), usually used when one
     * exception wraps another exception.
     *
     * Here, sku/requestedQuantity/availableQuantity are ordinary fields that
     * describe the domain failure. They travel with this exception object until
     * some later code catches it.
     */
    private final String sku;
    private final int requestedQuantity;
    private final int availableQuantity;

    public Lesson08OutOfStockException(String sku, int requestedQuantity, int availableQuantity) {
        /*
         * The exception message is human-readable. The API error handler adds a
         * separate machine-readable code: inventory.out-of-stock.
         *
         * The message is not the only useful information. The three fields below
         * remain available through getter methods after this exception is
         * thrown and caught.
         */
        super("Only %d item(s) are available for sku %s".formatted(availableQuantity, sku));
        this.sku = sku;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    public String getSku() {
        return sku;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }
}
