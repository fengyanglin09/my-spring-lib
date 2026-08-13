package io.github.fengyanglin09.springbootlab.lessons.lesson06_externalized_configuration.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Typed configuration for the Lesson 06 lab feature.
 *
 * <p>This record is not a normal request or response model. It is a
 * configuration properties type: Spring Boot binds external property values into
 * it during startup.</p>
 */
/*
 * @ConfigurationProperties is from Spring Boot.
 *
 * The prefix "lesson06.lab" means this record is populated from properties
 * whose names start with lesson06.lab.
 *
 * YAML:
 *
 * lesson06:
 *   lab:
 *     region: local-file
 *
 * Flattened Environment key:
 *
 * lesson06.lab.region=local-file
 *
 * Record component:
 *
 * region
 *
 * Spring Boot's binder connects those shapes.
 */
@ConfigurationProperties("lesson06.lab")
/*
 * This is a Java record.
 *
 * A record is a compact way to define an immutable data carrier. This header:
 *
 * public record Lesson06LabProperties(
 *     String region,
 *     Duration refreshInterval,
 *     Security security
 * )
 *
 * tells Java to generate the usual boilerplate for us:
 *
 * - private final fields
 * - a constructor
 * - accessor methods named region(), refreshInterval(), and security()
 * - equals(...)
 * - hashCode()
 * - toString()
 *
 * In a normal class, you would write fields and constructor parameters by hand.
 * In a record, the values listed in the header are called record components.
 */
public record Lesson06LabProperties(
        String region,
        Duration refreshInterval,
        Security security
) {

    /*
     * This is a compact constructor.
     *
     * A normal constructor would repeat the parameter list:
     *
     * public Lesson06LabProperties(
     *     String region,
     *     Duration refreshInterval,
     *     Security security
     * ) {
     *     ...
     * }
     *
     * A compact constructor omits the parameter list because Java already knows
     * the parameters from the record header. So this:
     *
     * public Lesson06LabProperties {
     *     ...
     * }
     *
     * means:
     *
     * "Run this code whenever someone creates a Lesson06LabProperties record."
     *
     * Spring Boot creates this record during configuration binding. It reads the
     * Environment values for lesson06.lab.*, converts them to Java types, then
     * calls this constructor.
     *
     * Important record detail:
     *
     * Inside a compact constructor, the names region, refreshInterval, and
     * security refer to constructor parameters, not already-assigned fields.
     * Java assigns the final record fields automatically after this block
     * finishes.
     *
     * That means this assignment:
     *
     * region = "local-default";
     *
     * changes the constructor parameter value that Java will later store in the
     * final record field. It is not mutating an existing object field.
     */
    public Lesson06LabProperties {
        /*
         * These defaults keep the broader Spring Boot lab context startable even
         * when Lesson 06's lesson-specific YAML file is not being loaded.
         *
         * In a real production app, you might prefer to fail fast when required
         * configuration is missing. This lesson is shared with other lessons, so
         * safe defaults prevent Lesson 06 from breaking Lesson 01-05 tests.
         */
        if (region == null || region.isBlank()) {
            /*
             * If Spring Boot did not find lesson06.lab.region, the constructor
             * parameter is null. This gives the record a safe lesson default
             * before Java stores the value in the final region field.
             */
            region = "local-default";
        }

        if (refreshInterval == null) {
            /*
             * Duration is a Java time type. Spring Boot can bind values such as
             * "30s" or "5s" into Duration instances. If no value was provided,
             * this default keeps the shared lab context startable.
             */
            refreshInterval = Duration.ofSeconds(30);
        }

        if (refreshInterval.isZero() || refreshInterval.isNegative()) {
            /*
             * This is a small guard, not the full validation lesson.
             *
             * If a bad value such as "0s" or "-5s" reaches this constructor,
             * startup should fail before normal application code uses an
             * impossible refresh interval.
             */
            throw new IllegalArgumentException("lesson06.lab.refresh-interval must be positive");
        }

        if (security == null) {
            /*
             * security is a nested record. If none of the nested
             * lesson06.lab.security.* values were provided, create a default
             * nested Security object.
             */
            security = new Security(true, "local-audience");
        }
    }

    /**
     * Nested typed configuration below lesson06.lab.security.
     *
     * <p>YAML keys such as {@code lesson06.lab.security.require-https} bind into
     * this nested record.</p>
     */
    public record Security(Boolean requireHttps, String tokenAudience) {

        /*
         * This is also a compact constructor, but for the nested Security
         * record. It runs before Java stores requireHttps and tokenAudience in
         * the final fields of the Security record.
         */
        public Security {
            /*
             * Boolean, not boolean, is used so the binder can pass null when the
             * value is absent. The constructor then applies the lesson default.
             */
            if (requireHttps == null) {
                requireHttps = true;
            }

            if (tokenAudience == null || tokenAudience.isBlank()) {
                tokenAudience = "local-audience";
            }
        }
    }
}
