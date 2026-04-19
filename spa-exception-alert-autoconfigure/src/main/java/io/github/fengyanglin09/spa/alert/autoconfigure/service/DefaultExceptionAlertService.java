package io.github.fengyanglin09.spa.alert.autoconfigure.service;

import io.github.fengyanglin09.spa.alert.autoconfigure.model.ExceptionAlertContext;
import io.github.fengyanglin09.spa.alert.autoconfigure.properties.ExceptionAlertProperties;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

@Slf4j
@RequiredArgsConstructor
public class DefaultExceptionAlertService implements ExceptionAlertService{

    private final JavaMailSender mailSender;
    private final ExceptionAlertProperties properties;
    private final ExceptionEmailTemplateService templateService;

    @Override
    public void sendAlert(ExceptionAlertContext context) {
        if (!properties.isEnabled() || properties.getTo() == null || properties.getTo().isEmpty()) {
            log.warn("Exception alert email skipped because alerting is disabled or no recipients are configured");
            return;
        }

        try {
            String subject = templateService.buildSubject(properties, context);
            String htmlBody = templateService.buildHtmlBody(properties, context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            if (properties.getFrom() != null && !properties.getFrom().isBlank()) {
                helper.setFrom(properties.getFrom());
            }
            helper.setTo(properties.getTo().toArray(String[]::new));
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);
        } catch (Exception ex) {
            log.error("Failed to send exception alert email", ex);
        }
    }

}
