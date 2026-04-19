package io.github.fengyanglin09.spa.alert.autoconfigure.properties;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

/**
 * this is what other apps will configure in their application.yml
 * */


@Validated
@Getter
@Setter
@ConfigurationProperties(prefix = "spa.exception-alert")
public class ExceptionAlertProperties {

    /**
     * Enable or disable exception alerting
     */
    private boolean enabled = true;
    /**
     * Application name shown in email
     */
    private String applicationName = "application";
    /**
     * Environment name (DEV / TEST / INT / PROD)
     */
    private String environment = "local";
    /**
     * Sender email address
     */
    private String from = "no-reply@localhost";
    /**
     * List of recipient email addresses
     */
    @NotEmpty(message = "spa.exception-alert.to must not be empty when alerting is enabled")
    private List<String> to = new ArrayList<>();
    /**
     * Prefix added to email subject.
     */
    private String subjectPrefix = "";

    /**
     * Whether to include stack trace in email.
     */
    private boolean includeStackTrace = true;

    /**
     * Maximum stack trace length in email.
     */
    private int maxStackTraceLength = 12000;

    /**
     * Whether to include hostname in email.
     */
    private boolean includeHostname = true;

    /**
     * Whether to include trace ID in email.
     */
    private boolean includeTraceId = true;

}
