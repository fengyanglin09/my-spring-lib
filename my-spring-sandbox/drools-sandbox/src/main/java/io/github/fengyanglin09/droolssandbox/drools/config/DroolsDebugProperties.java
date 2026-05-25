package io.github.fengyanglin09.droolssandbox.drools.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "drools.debug")
public class DroolsDebugProperties {
    private boolean traceEnabled;
}
