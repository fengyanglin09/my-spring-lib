package io.github.fengyanglin09.rulesenginelab.lessons.lesson14_decision_table.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * One row from the Lesson 14 decision table.
 *
 * <p>This is a normal Drools fact. The service reads rows from CSV, inserts
 * them into {@code /decisionRows}, and then DRL matches the request against
 * those row facts.</p>
 */
@Getter
@AllArgsConstructor
public class Lesson14DecisionRow {
    /**
     * Use {@code ANY} when a row can match every customer type.
     */
    public static final String ANY_CUSTOMER_TYPE = "ANY";

    private final String rowName;
    private final String customerType;
    private final int minimumOrderAmountCents;
    private final int discountPercent;
    private final String reason;

    /**
     * Checks whether this row's customer condition accepts the request.
     *
     * <p>Keeping this small helper in Java makes the DRL easier to read. In a
     * larger app, this is the same idea as giving the table a reusable
     * "customer matcher" instead of repeating string logic in every rule.</p>
     *
     * @param requestCustomerType customer type from the request fact
     * @return {@code true} if the row applies to the customer
     */
    public boolean matchesCustomerType(String requestCustomerType) {
        return ANY_CUSTOMER_TYPE.equals(customerType) || customerType.equals(requestCustomerType);
    }
}
