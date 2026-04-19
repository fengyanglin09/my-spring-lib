package io.github.fengyanglin09.spa.alert.autoconfigure.autoconfigure;

import io.github.fengyanglin09.spa.alert.autoconfigure.properties.ExceptionAlertProperties;
import io.github.fengyanglin09.spa.alert.autoconfigure.service.DefaultExceptionAlertService;
import io.github.fengyanglin09.spa.alert.autoconfigure.service.ExceptionAlertService;
import io.github.fengyanglin09.spa.alert.autoconfigure.service.ExceptionEmailTemplateService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

@AutoConfiguration
@ConditionalOnClass(JavaMailSender.class)
@EnableConfigurationProperties(ExceptionAlertProperties.class)
@ConditionalOnProperty(prefix = "spa.exception-alert", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ExceptionAlertAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(ExceptionEmailTemplateService.class)
    public ExceptionEmailTemplateService exceptionEmailTemplateService() {
        return new ExceptionEmailTemplateService();
    }

    @Bean
    @ConditionalOnMissingBean(ExceptionAlertService.class)
    public ExceptionAlertService exceptionAlertService(
            JavaMailSender javaMailSender,
            ExceptionAlertProperties properties,
            ExceptionEmailTemplateService templateService
    ) {
        return new DefaultExceptionAlertService(javaMailSender, properties, templateService);
    }
}

