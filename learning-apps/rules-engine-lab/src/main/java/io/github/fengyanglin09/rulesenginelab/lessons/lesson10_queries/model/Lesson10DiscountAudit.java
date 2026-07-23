package io.github.fengyanglin09.rulesenginelab.lessons.lesson10_queries.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Audit fact created by Lesson 10 rules.
 *
 * <p>This is the kind of fact the lesson query returns. The result does not get
 * audit messages directly from DRL; the service retrieves audit facts with
 * {@code executeQuery(...)} after rules finish.</p>
 */
@Getter
@AllArgsConstructor
public class Lesson10DiscountAudit {
    /**
     * Rule that created the audit fact.
     */
    private final String ruleName;

    /**
     * Human-readable audit message.
     */
    private final String message;
}
