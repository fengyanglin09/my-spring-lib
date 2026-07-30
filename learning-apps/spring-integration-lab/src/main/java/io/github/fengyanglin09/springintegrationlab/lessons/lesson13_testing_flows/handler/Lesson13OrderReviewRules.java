package io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows.handler;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows.model.Lesson13OrderDraft;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows.model.Lesson13OrderReviewResult;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows.model.Lesson13ReviewDecision;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson13_testing_flows.model.Lesson13ReviewedOrder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Business rules used by the lesson 13 tests and flow.
 */
// @Component tells Spring:
// "Create one Lesson13OrderReviewRules object during startup."
//
// This class is intentionally ordinary Java. That is important for testing:
// the component spec can create this class with new Lesson13OrderReviewRules()
// and test the decision logic without starting Spring Integration.
@Component
public class Lesson13OrderReviewRules {

    private static final BigDecimal MANUAL_REVIEW_AMOUNT = new BigDecimal("500.00");

    public Lesson13ReviewedOrder normalize(Lesson13OrderDraft draft) {
        // This method is used by the transform step in the flow.
        //
        // It is also easy to test directly because it does not depend on a
        // channel, gateway, Spring context, or external system.
        BigDecimal amount = draft.amount() == null ? BigDecimal.ZERO : draft.amount();
        Lesson13ReviewDecision decision = decide(draft, amount);

        return new Lesson13ReviewedOrder(
                draft.orderId(),
                normalizeCustomerType(draft.customerType()),
                amount,
                decision,
                List.of(
                        "handler:normalized-order",
                        "handler:selected-" + trailName(decision)
                )
        );
    }

    public Lesson13OrderReviewResult approve(Lesson13ReviewedOrder order) {
        // This method is called only when the router chooses APPROVED.
        return result(order, true, "APPROVED", "router:approved-branch");
    }

    public Lesson13OrderReviewResult requestManualReview(Lesson13ReviewedOrder order) {
        // This method is called only when the router chooses MANUAL_REVIEW.
        return result(order, false, "MANUAL_REVIEW", "router:manual-review-branch");
    }

    public Lesson13OrderReviewResult reject(Lesson13ReviewedOrder order) {
        // This method is called only when the router chooses REJECTED.
        return result(order, false, "REJECTED", "router:rejected-branch");
    }

    private Lesson13ReviewDecision decide(Lesson13OrderDraft draft, BigDecimal amount) {
        // Rule order matters:
        //
        // 1. Reject invalid or unverified orders.
        // 2. Send high-value verified orders to manual review.
        // 3. Approve ordinary verified orders.
        if (!draft.customerVerified() || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Lesson13ReviewDecision.REJECTED;
        }
        if (amount.compareTo(MANUAL_REVIEW_AMOUNT) >= 0) {
            return Lesson13ReviewDecision.MANUAL_REVIEW;
        }
        return Lesson13ReviewDecision.APPROVED;
    }

    private Lesson13OrderReviewResult result(
            Lesson13ReviewedOrder order,
            boolean accepted,
            String outcome,
            String branchStep
    ) {
        // Copy the existing trail before adding the branch step.
        //
        // This keeps the result immutable from the caller's point of view and
        // gives tests a simple way to see which path the message took.
        List<String> reviewTrail = new ArrayList<>(order.reviewTrail());
        reviewTrail.add(branchStep);

        return new Lesson13OrderReviewResult(
                order.orderId(),
                accepted,
                outcome,
                order.amount(),
                List.copyOf(reviewTrail)
        );
    }

    private String normalizeCustomerType(String customerType) {
        if (customerType == null || customerType.isBlank()) {
            return "UNKNOWN";
        }
        return customerType.trim().toUpperCase(Locale.ROOT);
    }

    private String trailName(Lesson13ReviewDecision decision) {
        return decision.name().toLowerCase(Locale.ROOT).replace('_', '-');
    }
}
