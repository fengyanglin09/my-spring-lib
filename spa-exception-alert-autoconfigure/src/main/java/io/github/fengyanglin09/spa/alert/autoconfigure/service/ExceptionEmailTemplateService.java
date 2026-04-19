package io.github.fengyanglin09.spa.alert.autoconfigure.service;


import io.github.fengyanglin09.spa.alert.autoconfigure.model.ExceptionAlertContext;
import io.github.fengyanglin09.spa.alert.autoconfigure.properties.ExceptionAlertProperties;
import lombok.RequiredArgsConstructor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

@RequiredArgsConstructor
public class ExceptionEmailTemplateService {

    public String buildSubject(ExceptionAlertProperties props, ExceptionAlertContext ctx) {
        String prefix = safe(props.getSubjectPrefix());
        if (prefix.isBlank()) {
            prefix = "[" + safe(props.getApplicationName()) + " " + safe(props.getEnvironment()) + "]";
        }
        return prefix + " " + safe(ctx.getTitle());
    }

    public String buildHtmlBody(ExceptionAlertProperties props, ExceptionAlertContext ctx) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; font-size: 14px; color: #333;">
                <h2 style="color: #b00020;">%s</h2>
                <p>%s</p>
                <hr/>

                <h3>System Information</h3>
                <table style="border-collapse: collapse;">
                    <tr><td><b>Application</b></td><td>%s</td></tr>
                    <tr><td><b>Environment</b></td><td>%s</td></tr>
                    <tr><td><b>Component</b></td><td>%s</td></tr>
                    <tr><td><b>Severity</b></td><td>%s</td></tr>
                    <tr><td><b>Trace ID</b></td><td>%s</td></tr>
                    <tr><td><b>Host</b></td><td>%s</td></tr>
                    <tr><td><b>Timestamp</b></td><td>%s</td></tr>
                </table>

                <h3>Additional Details</h3>
                %s

                <h3>Exception</h3>
                <table style="border-collapse: collapse;">
                    <tr><td><b>Type</b></td><td>%s</td></tr>
                    <tr><td><b>Message</b></td><td>%s</td></tr>
                </table>

                %s
            </body>
            </html>
            """.formatted(
                safe(ctx.getTitle()),
                safe(ctx.getSummary()),
                safe(props.getApplicationName()),
                safe(props.getEnvironment()),
                safe(ctx.getComponent()),
                safe(ctx.getSeverity()),
                props.isIncludeTraceId() ? safe(ctx.getTraceId()) : "Hidden",
                props.isIncludeHostname() ? safe(ctx.getHost()) : "Hidden",
                ctx.getTimestamp(),
                buildDetailsTable(ctx.getDetails()),
                ctx.getException() != null ? ctx.getException().getClass().getName() : "N/A",
                ctx.getException() != null ? safe(ctx.getException().getMessage()) : "N/A",
                buildStackTraceSection(props, ctx.getException())
        );
    }

    private String buildDetailsTable(Map<String, String> details) {
        if (details == null || details.isEmpty()) {
            return "<p>No additional details.</p>";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<table style='border-collapse: collapse;'>");
        for (Map.Entry<String, String> entry : details.entrySet()) {
            sb.append("""
                <tr>
                    <td><b>%s</b></td>
                    <td>%s</td>
                </tr>
                """.formatted(escape(entry.getKey()), escape(entry.getValue())));
        }
        sb.append("</table>");
        return sb.toString();
    }

    private String buildStackTraceSection(ExceptionAlertProperties props, Throwable ex) {
        if (!props.isIncludeStackTrace() || ex == null) {
            return "";
        }

        String stackTrace = getStackTrace(ex);
        if (stackTrace.length() > props.getMaxStackTraceLength()) {
            stackTrace = stackTrace.substring(0, props.getMaxStackTraceLength()) + "\n...truncated...";
        }

        return """
            <h3>Stack Trace</h3>
            <pre style="background:#f5f5f5; padding:12px; border:1px solid #ddd; white-space: pre-wrap;">%s</pre>
            """.formatted(escape(stackTrace));
    }

    private String getStackTrace(Throwable ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "N/A" : escape(value);
    }

    private String escape(String value) {
        if (value == null) {
            return "N/A";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
