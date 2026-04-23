package io.github.fengyanglin09.spa.alert.autoconfigure.autoconfigure;

import io.github.fengyanglin09.spa.alert.autoconfigure.properties.ExceptionAlertProperties;
import io.github.fengyanglin09.spa.alert.autoconfigure.service.DefaultExceptionAlertService;
import io.github.fengyanglin09.spa.alert.autoconfigure.service.ExceptionAlertService;
import io.github.fengyanglin09.spa.alert.autoconfigure.service.ExceptionEmailTemplateService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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

    @Bean(name = "spaExceptionAlertTaskExecutor")
    @ConditionalOnMissingBean(name = "spaExceptionAlertTaskExecutor")
    public TaskExecutor exceptionAlertTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("exception-alert-");
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.initialize();
        return executor;
    }

    @Bean
    @ConditionalOnMissingBean(ExceptionAlertService.class)
    public ExceptionAlertService exceptionAlertService(
            JavaMailSender javaMailSender,
            ExceptionAlertProperties properties,
            ExceptionEmailTemplateService templateService,
            @Qualifier("spaExceptionAlertTaskExecutor") TaskExecutor exceptionAlertTaskExecutor
    ) {
        return new DefaultExceptionAlertService(
                javaMailSender,
                properties,
                templateService,
                exceptionAlertTaskExecutor
        );
    }
}
